package com.cursedcauldron.wildbackport.common.entities;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.animation.api.AnimationState;
import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.access.api.Poses;
import com.cursedcauldron.wildbackport.common.entities.brain.WardenBrain;
import com.cursedcauldron.wildbackport.common.entities.brain.warden.SonicBoom;
import com.cursedcauldron.wildbackport.common.entities.warden.Angriness;
import com.cursedcauldron.wildbackport.common.entities.warden.MobPositionSource;
import com.cursedcauldron.wildbackport.common.entities.warden.VibrationHandler;
import com.cursedcauldron.wildbackport.common.entities.warden.WardenAngerManager;
import com.cursedcauldron.wildbackport.common.registry.WBMobEffects;
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntities;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.cursedcauldron.wildbackport.common.tag.WBGameEventTags;
import com.cursedcauldron.wildbackport.common.utils.MobUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.GameEventListenerRegistrar;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;

//<>

public class Warden extends Monster implements VibrationHandler.VibrationConfig {
    private static final EntityDataAccessor<Integer> ANGER = SynchedEntityData.defineId(Warden.class, EntityDataSerializers.INT);
    private int tendrilPitchEnd;
    private int tendrilPitchStart;
    private int heartPitchEnd;
    private int heartPitchStart;
    public AnimationState roaringAnimationState = new AnimationState();
    public AnimationState sniffingAnimationState = new AnimationState();
    public AnimationState emergingAnimationState = new AnimationState();
    public AnimationState diggingAnimationState = new AnimationState();
    public AnimationState attackingAnimationState = new AnimationState();
    public AnimationState sonicBoomAnimationState = new AnimationState();
    private final GameEventListenerRegistrar gameEventHandler;
    private final VibrationHandler listener;
    private WardenAngerManager angerManager = new WardenAngerManager(this::isValidTarget, Collections.emptyList());

    public Warden(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.listener = new VibrationHandler(new MobPositionSource(this, this.getEyeHeight()), 16, this);
        this.gameEventHandler = new GameEventListenerRegistrar(this.listener);
        this.xpReward = 5;
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_OTHER, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.hasPose(Poses.EMERGING.get()) ? 1 : 0);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        if (packet.getData() == 1) this.setPose(Poses.EMERGING.get());
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader reader) {
        return super.checkSpawnObstruction(reader) && reader.noCollision(this, this.getType().getDimensions().makeBoundingBox(this.position()));
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader reader) {
        return 0.0F;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return this.isDiggingOrEmerging() && !source.isBypassInvul() || super.isInvulnerableTo(source);
    }

    private boolean isDiggingOrEmerging() {
        return this.hasPose(Poses.DIGGING.get()) || this.hasPose(Poses.EMERGING.get());
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    @Override
    protected float nextStep() {
        return this.moveDist + 0.55F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 500.0D).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D).add(Attributes.ATTACK_KNOCKBACK, 1.5D).add(Attributes.ATTACK_DAMAGE, 30.0D);
    }

    @Override
    public boolean occludesVibrations() {
        return true;
    }

    @Override
    protected float getSoundVolume() {
        return 4.0F;
    }

    @Nullable @Override
    protected SoundEvent getAmbientSound() {
        return !this.hasPose(Poses.ROARING.get()) && !this.isDiggingOrEmerging() ? this.getAngriness().getSound() : null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return WBSoundEvents.WARDEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return WBSoundEvents.WARDEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(WBSoundEvents.WARDEN_STEP, 10.0F, 1.0F);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.level.broadcastEntityEvent(this, (byte)4);
        this.playSound(WBSoundEvents.WARDEN_ATTACK_IMPACT, 10.0F, this.getVoicePitch());
        SonicBoom.setCooldown(this, 40);
        return super.doHurtTarget(entity);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANGER, 0);
    }

    public int getAnger() {
        return this.entityData.get(ANGER);
    }

    private void updateAnger() {
        this.entityData.set(ANGER, this.getPrimeSuspectAnger());
    }

    @Override
    public void tick() {
        if (this.level instanceof ServerLevel server) {
            this.listener.tick(server);
            if (this.isPersistenceRequired() || this.requiresCustomPersistence()) {
                WardenBrain.setDigCooldown(this);
            }
        }

        super.tick();
        if (this.level.isClientSide()) {
            if (this.tickCount % this.getHeartRate() == 0) {
                this.heartPitchEnd = 10;
                if (!this.isSilent()) {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), WBSoundEvents.WARDEN_HEARTBEAT, this.getSoundSource(), 5.0F, this.getVoicePitch(), false);
                }
            }

            this.tendrilPitchStart = this.tendrilPitchEnd;
            if (this.tendrilPitchEnd > 0) {
                --this.tendrilPitchEnd;
            }

            this.heartPitchStart = this.heartPitchEnd;
            if (this.heartPitchEnd > 0) {
                --this.heartPitchEnd;
            }

            if (this.hasPose(Poses.EMERGING.get())) {
                this.addDigParticles(this.emergingAnimationState);
            }

            if (this.hasPose(Poses.DIGGING.get())) {
                this.addDigParticles(this.diggingAnimationState);
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        ServerLevel level = (ServerLevel)this.level;
        level.getProfiler().push("wardenBrain");
        this.getBrain().tick(level, this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
        if ((this.tickCount + this.getId()) % 120 == 0) {
            addDarknessToClosePlayers(level, this.position(), this, 20);
        }

        if (this.tickCount % 20 == 0) {
            this.angerManager.tick(level, this::isValidTarget);
            this.updateAnger();
        }

        WardenBrain.updateActivity(this);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 4) {
            this.roaringAnimationState.stop();
            this.attackingAnimationState.start(this.tickCount);
        } else if (status == 61) {
            this.tendrilPitchEnd = 10;
        } else if (status == 62) {
            this.sonicBoomAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(status);
        }
    }

    private int getHeartRate() {
        return 40 - Mth.floor(Mth.clamp((float)this.getAnger() / (float)Angriness.ANGRY.getThreshold(), 0.0F, 1.0F) * 30.0F);
    }

    public float getTendrilPitch(float tickDelta) {
        return Mth.lerp(tickDelta, (float)this.tendrilPitchStart, (float)this.tendrilPitchEnd) / 10.0F;
    }

    public float getHeartPitch(float tickDelta) {
        return Mth.lerp(tickDelta, (float)this.heartPitchStart, (float)this.heartPitchEnd) / 10.0F;
    }

    private void addDigParticles(AnimationState animationState) {
        if ((float)animationState.runningTime() < 4500.0F) {
            Random random = this.getRandom();
            BlockState state = this.getBlockStateOn();
            if (state.getRenderShape() != RenderShape.INVISIBLE) {
                for (int i = 0; i < 30; i++) {
                    double x = this.getX() + (double)Mth.randomBetween(random, -0.7F, 0.7F);
                    double y = this.getY();
                    double z = this.getZ() + (double)Mth.randomBetween(random, -0.7F, 0.7F);
                    this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), x, y, z, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (DATA_POSE.equals(data)) {
            if (this.hasPose(Poses.EMERGING.get())) {
                this.emergingAnimationState.start(this.tickCount);
            } else if (this.hasPose(Poses.DIGGING.get())) {
                this.diggingAnimationState.start(this.tickCount);
            } else if (this.hasPose(Poses.ROARING.get())) {
                this.roaringAnimationState.start(this.tickCount);
            } else if (this.hasPose(Poses.SNIFFING.get())) {
                this.sniffingAnimationState.start(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(data);
    }

    public boolean hasPose(Pose pose) {
        return this.getPose() == pose;
    }

    @Override
    public boolean ignoreExplosion() {
        return this.isDiggingOrEmerging();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return WardenBrain.makeBrain(this, dynamic);
    }

    @Override @SuppressWarnings("unchecked")
    public Brain<Warden> getBrain() {
        return (Brain<Warden>)super.getBrain();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public TagKey<GameEvent> getListenableEvents() {
        return WBGameEventTags.WARDEN_CAN_LISTEN;
    }

    @Override
    public boolean canTriggerAvoidVibration() {
        return true;
    }

    public boolean isValidTarget(@Nullable Entity entity) {
        if (entity instanceof LivingEntity living) {
            return this.level == entity.level && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) && !this.isAlliedTo(entity) && living.getType() != EntityType.ARMOR_STAND && living.getType() != WBEntities.WARDEN.get() && !living.isInvulnerable() && !living.isDeadOrDying() && this.level.getWorldBorder().isWithinBounds(living.getBoundingBox());
        }

        return false;
    }

    public static void addDarknessToClosePlayers(ServerLevel world, Vec3 pos, @Nullable Entity entity, int range) {
        MobEffectInstance instance = new MobEffectInstance(WBMobEffects.DARKNESS.get(), 260, 0, false, false);
        MobUtils.addEffectToPlayersWithinDistance(world, entity, pos, range, instance, 200);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        WardenAngerManager.createCodec(this::isValidTarget).encodeStart(NbtOps.INSTANCE, this.angerManager).resultOrPartial(WildBackport.LOGGER::error).ifPresent(manager -> tag.put("anger", manager));

    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("anger")) {
            WardenAngerManager.createCodec(this::isValidTarget).parse(new Dynamic<>(NbtOps.INSTANCE, tag.get("anger"))).resultOrPartial(WildBackport.LOGGER::error).ifPresent(manager -> this.angerManager = manager);
            this.updateAnger();
        }
    }

    private void playListeningSound() {
        if (!this.hasPose(Poses.ROARING.get())) this.playSound(this.getAngriness().getListeningSound(), 10.0F, this.getVoicePitch());
    }

    public Angriness getAngriness() {
        return Angriness.getForAnger(this.getPrimeSuspectAnger());
    }

    public int getPrimeSuspectAnger() {
        return this.angerManager.getPrimeSuspectAnger();
    }

    public void removeSuspect(Entity entity) {
        this.angerManager.removeSuspect(entity);
    }

    public void increaseAngerAt(Entity entity) {
        this.increaseAngerAt(entity, 35, true);
    }

    public void increaseAngerAt(Entity entity, int amount, boolean listening) {
        if (!this.isNoAi() && this.isValidTarget(entity)) {
            WardenBrain.setDigCooldown(this);
            boolean targetNotPlayer = !(this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null) instanceof Player);
            int anger = this.angerManager.increaseAngerAt(entity, amount);
            if (entity instanceof Player && targetNotPlayer && Angriness.getForAnger(anger).isAngry()) {
                this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            }

            if (listening) this.playListeningSound();
        }
    }

    public Optional<LivingEntity> getPrimeSuspect() {
        return this.getAngriness().isAngry() ? this.angerManager.getPrimeSuspect() : Optional.empty();
    }

    @Nullable @Override
    public LivingEntity getTarget() {
        return this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    @Override
    public boolean removeWhenFarAway(double sqrDist) {
        return false;
    }

    @Nullable @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance instance, MobSpawnType spawn, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        this.getBrain().setMemoryWithExpiry(WBMemoryModules.DIG_COOLDOWN.get(), Unit.INSTANCE, 1200L);
        if (spawn == MobSpawnType.TRIGGERED) {
            this.setPose(Poses.EMERGING.get());
            this.getBrain().setMemoryWithExpiry(WBMemoryModules.IS_EMERGING.get(), Unit.INSTANCE, WardenBrain.EMERGE_DURATION);
            this.playSound(WBSoundEvents.WARDEN_AGITATED, 5.0F, 1.0F);
        }

        return super.finalizeSpawn(level, instance, spawn, data, tag);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (!this.level.isClientSide && !this.isNoAi() && !this.isDiggingOrEmerging()) {
            Entity entity = source.getEntity();
            this.increaseAngerAt(entity, Angriness.ANGRY.getThreshold() + 20, false);
            if (this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof LivingEntity living) {
                if (!(source instanceof IndirectEntityDamageSource) || this.closerThan(living, 5.0D)) {
                    this.updateAttackTarget(living);
                }
            }
        }

        return hurt;
    }

    public void updateAttackTarget(LivingEntity entity) {
        this.getBrain().eraseMemory(WBMemoryModules.ROAR_TARGET.get());
        this.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, entity);
        this.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        SonicBoom.setCooldown(this, 200);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions dimensions = super.getDimensions(pose);
        return this.isDiggingOrEmerging() ? EntityDimensions.fixed(dimensions.width, 1.0F) : dimensions;
    }

    @Override
    public boolean isPushable() {
        return !this.isDiggingOrEmerging() && super.isPushable();
    }

    @Override
    protected void doPush(Entity entity) {
        if (!this.isNoAi() && !this.getBrain().hasMemoryValue(WBMemoryModules.TOUCH_COOLDOWN.get())) {
            this.getBrain().setMemoryWithExpiry(WBMemoryModules.TOUCH_COOLDOWN.get(), Unit.INSTANCE, 20L);
            this.increaseAngerAt(entity);
            WardenBrain.setDisturbanceLocation(this, entity.blockPosition());
        }

        super.doPush(entity);
    }

    @Override
    public boolean shouldListen(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity) {
        if (!this.isNoAi() && !this.isDeadOrDying() && !this.getBrain().hasMemoryValue(WBMemoryModules.VIBRATION_COOLDOWN.get()) && !this.isDiggingOrEmerging() && level.getWorldBorder().isWithinBounds(pos) && !this.isRemoved() && this.level == level) {
            if (entity instanceof LivingEntity living) {
                return this.isValidTarget(living);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onSignalReceive(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity source, float distance) {
        if (!this.isDeadOrDying()) {
            this.brain.setMemoryWithExpiry(WBMemoryModules.VIBRATION_COOLDOWN.get(), Unit.INSTANCE, 40L);
            level.broadcastEntityEvent(this, (byte)61);
            this.playSound(WBSoundEvents.WARDEN_TENDRIL_CLICKS, 5.0F, this.getVoicePitch());
            BlockPos position = pos;
            if (source != null) {
                if (this.closerThan(source, 30.0D)) {
                    if (this.getBrain().hasMemoryValue(WBMemoryModules.RECENT_PROJECTILE.get())) {
                        if (this.isValidTarget(source)) {
                            position = source.blockPosition();
                        }

                        this.increaseAngerAt(source);
                    } else {
                        this.increaseAngerAt(source, 10, true);
                    }
                }

                this.getBrain().setMemoryWithExpiry(WBMemoryModules.RECENT_PROJECTILE.get(), Unit.INSTANCE, 100L);
            } else {
                this.increaseAngerAt(entity);
            }

            if (!this.getAngriness().isAngry()) {
                Optional<LivingEntity> primeSuspect = this.angerManager.getPrimeSuspect();
                if (source != null || primeSuspect.isEmpty() || primeSuspect.get() == entity) {
                    WardenBrain.setDisturbanceLocation(this, position);
                }
            }
        }
    }

    @Nullable @Override
    public GameEventListenerRegistrar getGameEventListenerRegistrar() {
        return this.gameEventHandler;
    }
}