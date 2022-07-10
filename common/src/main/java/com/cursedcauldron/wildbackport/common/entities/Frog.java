package com.cursedcauldron.wildbackport.common.entities;

import com.cursedcauldron.wildbackport.client.animation.api.AnimationState;
import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.brain.FrogBrain;
import com.cursedcauldron.wildbackport.common.entities.access.api.Poses;
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntities;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.cursedcauldron.wildbackport.common.registry.entity.WBSensorTypes;
import com.cursedcauldron.wildbackport.common.tag.WBBiomeTags;
import com.cursedcauldron.wildbackport.common.tag.WBBlockTags;
import com.cursedcauldron.wildbackport.common.tag.WBEntityTypeTags;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;

//<>

public class Frog extends Animal {
    public static final Ingredient FOOD = Ingredient.of(Items.SLIME_BALL);
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Frog>>> SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, WBSensorTypes.FROG_ATTACKABLES.get(), WBSensorTypes.FROG_TEMPTATIONS.get(), WBSensorTypes.IS_IN_WATER.get());
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORIES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.BREED_TARGET, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_ATTACKABLE, WBMemoryModules.IS_IN_WATER.get(), WBMemoryModules.IS_PREGNANT.get(), WBMemoryModules.UNREACHABLE_TONGUE_TARGETS.get());
    private static final EntityDataAccessor<Integer> VARIANT_ID = SynchedEntityData.defineId(Frog.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<OptionalInt> TARGET_ID = SynchedEntityData.defineId(Frog.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    public final AnimationState longJumpingAnimationState = new AnimationState();
    public final AnimationState croakingAnimationState = new AnimationState();
    public final AnimationState usingTongueAnimationState = new AnimationState();
    public final AnimationState walkingAnimationState = new AnimationState();
    public final AnimationState swimmingAnimationState = new AnimationState();
    public final AnimationState idlingInWaterAnimationState = new AnimationState();

    public Frog(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.lookControl = new FrogLookController(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 4.0F);
        this.setPathfindingMalus(BlockPathTypes.TRAPDOOR, -1.0F);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.maxUpStep = 1.0F;
    }

    @Override
    protected Brain.Provider<Frog> brainProvider() {
        return Brain.provider(MEMORIES, SENSORS);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return FrogBrain.create(this.brainProvider().makeBrain(dynamic));
    }

    @Override @SuppressWarnings("unchecked")
    public Brain<Frog> getBrain() {
        return (Brain<Frog>)super.getBrain();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT_ID, 0);
        this.entityData.define(TARGET_ID, OptionalInt.empty());
    }

    public void clearFrogTarget() {
        this.entityData.set(TARGET_ID, OptionalInt.empty());
    }

    public Optional<Entity> getFrogTarget() {
        return this.entityData.get(TARGET_ID).stream().mapToObj(this.level::getEntity).filter(Objects::nonNull).findFirst();
    }

    public void setFrogTarget(Entity entity) {
        this.entityData.set(TARGET_ID, OptionalInt.of(entity.getId()));
    }

    @Override
    public int getHeadRotSpeed() {
        return 35;
    }

    @Override
    public int getMaxHeadYRot() {
        return 5;
    }

    public Variant getVariant() {
        return Variant.byId(this.entityData.get(VARIANT_ID));
    }

    public void setVariant(Variant variant) {
        this.entityData.set(VARIANT_ID, variant.getId());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setVariant(Variant.byId(tag.getInt("Variant")));
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    private boolean isMovingOnLand() {
        return this.onGround && this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D && !this.isInWaterOrBubble();
    }

    private boolean isMovingOnWater() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D && this.isInWaterOrBubble();
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("frogBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("frogActivityUpdate");
        FrogBrain.updateActivities(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void tick() {
        if (this.level.isClientSide()) {
            if (this.isMovingOnLand()) {
                this.walkingAnimationState.startIfNotRunning(this.tickCount);
            } else {
                this.walkingAnimationState.stop();
            }

            if (this.isMovingOnWater()) {
                this.idlingInWaterAnimationState.stop();
                this.swimmingAnimationState.startIfNotRunning(this.tickCount);
            } else if (this.isInWaterOrBubble()) {
                this.swimmingAnimationState.stop();
                this.idlingInWaterAnimationState.startIfNotRunning(this.tickCount);
            } else {
                this.swimmingAnimationState.stop();
                this.idlingInWaterAnimationState.stop();
            }
        }

        super.tick();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (DATA_POSE.equals(data)) {
            if (this.isInPose(Pose.LONG_JUMPING)) {
                this.longJumpingAnimationState.start(this.tickCount);
            } else {
                this.longJumpingAnimationState.stop();
            }

            if (this.isInPose(Poses.CROAKING.get())) {
                this.croakingAnimationState.start(this.tickCount);
            } else {
                this.croakingAnimationState.stop();
            }

            if (this.isInPose(Poses.USING_TONGUE.get())) {
                this.usingTongueAnimationState.start(this.tickCount);
            } else {
                this.usingTongueAnimationState.stop();
            }
        }

        super.onSyncedDataUpdated(data);
    }

    public boolean isInPose(Pose pose) {
        return this.getPose() == pose;
    }

    @Nullable @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        Frog frog = WBEntities.FROG.get().create(level);
        if (frog != null) FrogBrain.coolDownLongJump(frog, level.getRandom());
        return frog;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void setBaby(boolean baby) {}

    @Override
    public void spawnChildFromBreeding(ServerLevel level, Animal partner) {
        ServerPlayer player = this.getLoveCause();
        if (player == null) player = partner.getLoveCause();

        if (player != null) {
            player.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(player, this, partner, null);
        }

        this.setAge(6000);
        partner.setAge(6000);
        this.resetLove();
        partner.resetLove();
        this.getBrain().setMemory(WBMemoryModules.IS_PREGNANT.get(), Unit.INSTANCE);
        level.broadcastEntityEvent(this, (byte)18);
        if (level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) level.addFreshEntity(new ExperienceOrb(level, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag tag) {
        Holder<Biome> biome = accessor.getBiome(this.blockPosition());
        if (biome.is(WBBiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
            this.setVariant(Variant.COLD);
        } else if (biome.is(WBBiomeTags.SPAWNS_WARM_VARIANT_FROGS)) {
            this.setVariant(Variant.WARM);
        } else {
            this.setVariant(Variant.TEMPERATE);
        }

        FrogBrain.coolDownLongJump(this, accessor.getRandom());
        return super.finalizeSpawn(accessor, difficulty, spawnType, groupData, tag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 1.0D).add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 10.0D);
    }

    @Nullable @Override
    protected SoundEvent getAmbientSound() {
        return WBSoundEvents.FROG_AMBIENT;
    }

    @Nullable @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return WBSoundEvents.FROG_HURT;
    }

    @Nullable @Override
    protected SoundEvent getDeathSound() {
        return WBSoundEvents.FROG_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(WBSoundEvents.FROG_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    protected int calculateFallDamage(float fallDistance, float damageMultiplier) {
        return super.calculateFallDamage(fallDistance, damageMultiplier) - 5;
    }

    @Override
    public void travel(Vec3 motion) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), motion);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(motion);
        }
    }

    @Override
    public boolean canCutCorner(BlockPathTypes path) {
        return super.canCutCorner(path) && path != BlockPathTypes.WATER_BORDER;
    }

    public static boolean isValidFrogFood(LivingEntity entity) {
        return (!(entity instanceof Slime slime) || slime.getSize() == 1) && entity.getType().is(WBEntityTypeTags.FROG_FOOD);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FrogPathNavigator(this, level);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return FOOD.test(stack);
    }

    public static boolean checkFrogSpawnRules(EntityType<? extends Animal> type, LevelAccessor accessor, MobSpawnType spawnType, BlockPos pos, Random random) {
        return accessor.getBlockState(pos.below()).is(WBBlockTags.FROGS_SPAWNABLE_ON) && isBrightEnoughToSpawn(accessor, pos);
    }

    class FrogLookController extends LookControl {
        FrogLookController(Mob mobEntity) {
            super(mobEntity);
        }
        @Override
        protected boolean resetXRotOnTick() {
            return Frog.this.getFrogTarget().isEmpty();
        }
    }

    static class FrogNodeEvaluator extends AmphibiousNodeEvaluator {
        private final BlockPos.MutableBlockPos preferredBlock = new BlockPos.MutableBlockPos();

        public FrogNodeEvaluator(boolean penalizeDeepWater) {
            super(penalizeDeepWater);
        }

        @Override
        public BlockPathTypes getBlockPathType(BlockGetter getter, int x, int y, int z) {
            this.preferredBlock.set(x, y - 1, z);
            BlockState state = getter.getBlockState(this.preferredBlock);
            return state.is(WBBlockTags.FROG_PREFER_JUMP_TO) ? BlockPathTypes.OPEN : FrogNodeEvaluator.getBlockPathTypeStatic(getter, this.preferredBlock.move(Direction.UP));
        }
    }

    static class FrogPathNavigator extends WaterBoundPathNavigation {
        FrogPathNavigator(Frog frog, Level world) {
            super(frog, world);
        }
        @Override
        protected PathFinder createPathFinder(int range) {
            this.nodeEvaluator = new FrogNodeEvaluator(true);
            return new PathFinder(this.nodeEvaluator, range);
        }

        @Override
        protected boolean canUpdatePath() {
            return true;
        }

        @Override
        public boolean isStableDestination(BlockPos pos) {
            return !this.level.getBlockState(pos.below()).isAir();
        }
    }

    public enum Variant {
        TEMPERATE(0, "temperate"),
        WARM(1, "warm"),
        COLD(2, "cold");
        private static final Variant[] VARIANTS = Arrays.stream(Variant.values()).sorted(Comparator.comparingInt(Variant::getId)).toArray(Variant[]::new);
        private final int id;
        private final String name;

        Variant(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static Variant byId(int id) {
            if (id < 0 || id >= VARIANTS.length) {
                id = 0;
            }

            return VARIANTS[id];
        }
    }
}
