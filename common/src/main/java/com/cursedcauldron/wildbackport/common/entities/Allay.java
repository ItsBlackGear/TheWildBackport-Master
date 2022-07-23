package com.cursedcauldron.wildbackport.common.entities;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.brain.AllayBrain;
import com.cursedcauldron.wildbackport.common.entities.warden.MobPositionSource;
import com.cursedcauldron.wildbackport.common.entities.warden.VibrationHandler;
import com.cursedcauldron.wildbackport.common.registry.WBGameEvents;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.cursedcauldron.wildbackport.common.tag.WBGameEventTags;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.GameEventListenerRegistrar;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//<>

public class Allay extends PathfinderMob implements InventoryCarrier, VibrationHandler.VibrationConfig {
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Allay>>> SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.NEAREST_ITEMS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORIES = ImmutableList.of(MemoryModuleType.PATH, MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.HURT_BY, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, WBMemoryModules.LIKED_PLAYER.get(), WBMemoryModules.LIKED_NOTEBLOCK.get(), WBMemoryModules.LIKED_NOTEBLOCK_COOLDOWN_TICKS.get(), WBMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS.get());
    public static final ImmutableList<Float> THROW_SOUND_PITCHES = ImmutableList.of(0.5625F, 0.625F, 0.75F, 0.9375F, 1.0F, 1.0F, 1.125F, 1.25F, 1.5F, 1.875F, 2.0F, 2.25F, 2.5F, 3.0F, 3.75F, 4.0F);
    private final GameEventListenerRegistrar registrar;
    private VibrationHandler listener;
    private final SimpleContainer inventory = new SimpleContainer(1);
    private float holdingTicks;
    private float holdingTicksOld;

    public Allay(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setCanPickUpLoot(this.canPickUpLoot());
        this.listener = new VibrationHandler(new MobPositionSource(this, this.getEyeHeight()), 16, this, null, 0.0F, 0);
        this.registrar = new GameEventListenerRegistrar(this.listener);
    }

    @Override
    protected Brain.Provider<Allay> brainProvider() {
        return Brain.provider(MEMORIES, SENSORS);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return AllayBrain.create(this.brainProvider().makeBrain(dynamic));
    }

    @Override @SuppressWarnings("unchecked")
    public Brain<Allay> getBrain() {
        return (Brain<Allay>)super.getBrain();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.FLYING_SPEED, 0.1F).add(Attributes.MOVEMENT_SPEED, 0.1F).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    public void travel(Vec3 movementInput) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, movementInput);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, movementInput);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5F));
            } else {
                this.moveRelative(this.getSpeed(), movementInput);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.91F));
            }
        }

        this.calculateEntityAnimation(this, false);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.6F;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player) {
            Optional<UUID> likedPlayer = this.getBrain().getMemory(WBMemoryModules.LIKED_PLAYER.get());
            if (likedPlayer.isPresent() && player.getUUID().equals(likedPlayer.get())) return false;
        }

        return super.hurt(source, amount);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {}

    @Override
    protected void checkFallDamage(double fallenDistance, boolean canLand, BlockState state, BlockPos pos) {}

    @Override @Nullable
    protected SoundEvent getAmbientSound() {
        return this.hasItemInSlot(EquipmentSlot.MAINHAND) ? WBSoundEvents.ALLAY_AMBIENT_WITH_ITEM : WBSoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return WBSoundEvents.ALLAY_HURT;
    }

    @Override @Nullable
    protected SoundEvent getDeathSound() {
        return WBSoundEvents.ALLAY_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("allayBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("allayActivityUpdate");
        AllayBrain.updateActivities(this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("looting");
        if (!this.level.isClientSide && this.canPickUpLoot() && this.isAlive() && !this.dead && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            List<ItemEntity> items = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(1.0, 1.0, 1.0));
            for (ItemEntity item : items) {
                if (item.isRemoved() || item.getItem().isEmpty() || item.hasPickUpDelay() || !this.wantsToPickUp(item.getItem())) continue;
                this.pickUpItem(item);
            }
        }
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide && this.isAlive() && this.tickCount % 10 == 0) this.heal(1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.holdingTicksOld = this.holdingTicks;
            if (this.isHoldingItem()) {
                this.holdingTicks = Mth.clamp(this.holdingTicks + 1.0F, 0.0F, 5.0F);
            } else {
                this.holdingTicks = Mth.clamp(this.holdingTicks - 1.0F, 0.0F, 5.0F);
            }
        } else {
            this.listener.tick(this.level);
        }
    }

    @Override
    public boolean canPickUpLoot() {
        return !this.isOnItemPickupCooldown() && this.isHoldingItem();
    }

    public boolean isHoldingItem() {
        return !this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
    }

    @Override
    public boolean canTakeItem(ItemStack stack) {
        return false;
    }

    private boolean isOnItemPickupCooldown() {
        return this.getBrain().checkMemory(WBMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS.get(), MemoryStatus.VALUE_PRESENT);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack playerStack = player.getItemInHand(hand);
        ItemStack allayStack = this.getItemInHand(InteractionHand.MAIN_HAND);
        if (allayStack.isEmpty() && !playerStack.isEmpty()) {
            ItemStack stack = playerStack.copy();
            stack.setCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, stack);
            if (!player.getAbilities().instabuild) playerStack.shrink(1);
            this.level.playSound(player, this, WBSoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
            this.getBrain().setMemory(WBMemoryModules.LIKED_PLAYER.get(), player.getUUID());
            return InteractionResult.SUCCESS;
        } else if (!allayStack.isEmpty() && hand == InteractionHand.MAIN_HAND && playerStack.isEmpty()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.level.playSound(player, this, WBSoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
            this.swing(InteractionHand.MAIN_HAND);
            for (ItemStack stack : this.getInventory().removeAllItems()) BehaviorUtils.throwItem(this, stack, this.position());
            this.getBrain().eraseMemory(WBMemoryModules.LIKED_PLAYER.get());
            player.addItem(allayStack);
            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        ItemStack allayStack = this.getItemInHand(InteractionHand.MAIN_HAND);
        return !allayStack.isEmpty() && allayStack.sameItemStackIgnoreDurability(stack) && this.inventory.canAddItem(stack);
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        if (this.wantsToPickUp(stack)) {
            SimpleContainer inventory = this.getInventory();
            boolean canAdd = inventory.canAddItem(stack);
            if (!canAdd) return;

            this.onItemPickup(itemEntity);
            this.take(itemEntity, stack.getCount());
            ItemStack addedStack = inventory.addItem(stack);
            if (addedStack.isEmpty()) {
                itemEntity.discard();
            } else {
                stack.setCount(addedStack.getCount());
            }
        }
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    protected boolean isFlapping() {
        return !this.isOnGround();
    }

    @Override @Nullable
    public GameEventListenerRegistrar getGameEventListenerRegistrar() {
        return this.registrar;
    }

    public float getHoldingItemAnimationProgress(float animationProgress) {
        return Mth.lerp(animationProgress, this.holdingTicksOld, this.holdingTicks) / 5.0F;
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
        ItemStack stack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!stack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(stack)) {
            this.spawnAtLocation(stack);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean shouldListen(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity) {
        if (this.level == level && !this.isRemoved() && !this.isNoAi()) {
            if (!this.brain.hasMemoryValue(WBMemoryModules.LIKED_NOTEBLOCK.get())) {
                return true;
            } else {
                Optional<GlobalPos> likedNoteblock = this.brain.getMemory(WBMemoryModules.LIKED_NOTEBLOCK.get());
                return likedNoteblock.isPresent() && likedNoteblock.get().dimension() == level.dimension() && likedNoteblock.get().pos() == pos;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onSignalReceive(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity source, float distance) {
        if (event == WBGameEvents.NOTE_BLOCK_PLAY.get()) AllayBrain.rememberNoteBlock(this, new BlockPos(pos));
    }

    @Override
    public TagKey<GameEvent> getListenableEvents() {
        return WBGameEventTags.ALLAY_CAN_LISTEN;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Inventory", this.inventory.createTag());
        VibrationHandler.codec(this).encodeStart(NbtOps.INSTANCE, this.listener).resultOrPartial(WildBackport.LOGGER::error).ifPresent(listener -> tag.put("listener", listener));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.inventory.fromTag(tag.getList("Inventory", 10));
        if (tag.contains("listener", 10)) VibrationHandler.codec(this).parse(new Dynamic<>(NbtOps.INSTANCE, tag.getCompound("listener"))).resultOrPartial(WildBackport.LOGGER::error).ifPresent(listener -> this.listener = listener);
    }

    public Iterable<BlockPos> getPotentialEscapePositions() {
        AABB box = this.getBoundingBox();
        int minX = Mth.floor(box.minX - 0.5D);
        int maxX = Mth.floor(box.maxX + 0.5D);
        int minY = Mth.floor(box.minY - 0.5D);
        int maxY = Mth.floor(box.maxY + 0.5D);
        int minZ = Mth.floor(box.minZ - 0.5D);
        int maxZ = Mth.floor(box.maxZ + 0.5D);
        return BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0D, this.getEyeHeight() * 0.6D, this.getBbWidth() * 0.1D);
    }
}