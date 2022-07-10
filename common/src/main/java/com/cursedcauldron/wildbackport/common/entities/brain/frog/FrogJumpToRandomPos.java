package com.cursedcauldron.wildbackport.common.entities.brain.frog;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FrogJumpToRandomPos<E extends Mob> extends Behavior<E> {
    private static final List<Integer> ANGLES = Lists.newArrayList(65, 70, 75, 80);
    private final UniformInt cooldown;
    protected final int yRange;
    protected final int xzRange;
    protected final float maxRange;
    protected List<Target> targets = Lists.newArrayList();
    protected Optional<Vec3> lastPos = Optional.empty();
    @Nullable
    protected Vec3 lastTarget;
    protected int tries;
    protected long targetTime;
    private final Function<E, SoundEvent> landingSound;
    private final Predicate<BlockState> landingBlocks;

    public FrogJumpToRandomPos(UniformInt cooldown, int yRange, int xzRange, float range, Function<E, SoundEvent> landingSound, Predicate<BlockState> landingBlocks) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT), 200);
        this.cooldown = cooldown;
        this.yRange = yRange;
        this.xzRange = xzRange;
        this.maxRange = range;
        this.landingSound = landingSound;
        this.landingBlocks = landingBlocks;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Mob entity) {
        boolean canUse = entity.isOnGround() && !entity.isInWater() && !entity.isInLava() && !level.getBlockState(entity.blockPosition()).is(Blocks.HONEY_BLOCK);
        if (!canUse) entity.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, this.cooldown.sample(level.random) / 2);
        return canUse;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Mob entity, long time) {
        boolean canUse = this.lastPos.isPresent() && this.lastPos.get().equals(entity.position()) && this.tries > 0 && !entity.isInWaterOrBubble() && (this.lastTarget != null || !this.targets.isEmpty());
        if (!canUse && entity.getBrain().getMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isEmpty()) {
            entity.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, this.cooldown.sample(level.random) / 2);
            entity.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        }

        return canUse;
    }

    @Override
    protected void start(ServerLevel level, E entity, long time) {
        this.lastTarget = null;
        this.tries = 20;
        this.lastPos = Optional.of(entity.position());
        BlockPos pos = entity.blockPosition();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        this.targets = BlockPos.betweenClosedStream(x - this.xzRange, y - this.yRange, z - this.xzRange, x + this.xzRange, y + this.yRange, z + this.xzRange).filter(position -> !position.equals(pos)).map(position -> new Target(position.immutable(), Mth.ceil(pos.distSqr(position)))).collect(Collectors.toCollection(Lists::newArrayList));
    }

    @Override
    protected void tick(ServerLevel level, E entity, long time) {
        if (this.lastTarget != null) {
            if (time - this.targetTime >= 40L) {
                entity.setYRot(entity.yBodyRot);
                entity.setDiscardFriction(true);
                double length = this.lastTarget.length();
                double height = length + entity.getJumpBoostPower();
                entity.setDeltaMovement(this.lastTarget.scale(height / length));
                entity.getBrain().setMemory(MemoryModuleType.LONG_JUMP_MID_JUMP, true);
                level.playSound(null, entity, this.landingSound.apply(entity), SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        } else {
            --this.tries;
            this.pickTarget(level, entity, time);
        }
    }

    protected void pickTarget(ServerLevel level, E entity, long time) {
        while (true) {
            if (!this.targets.isEmpty()) {
                Optional<Target> jumpTarget = this.jumpTarget(level);
                if (jumpTarget.isEmpty()) continue;

                Target target = jumpTarget.get();
                BlockPos pos = target.getPos();
                if (!canLandOn(level, entity, pos)) continue;

                Vec3 center = Vec3.atCenterOf(pos);
                Vec3 lastTarget = this.getRammingVelocity(entity, center);
                if (lastTarget == null) continue;

                entity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos));
                PathNavigation navigation = entity.getNavigation();
                Path path = navigation.createPath(pos, 0, 8);
                if (path != null && path.canReach()) continue;

                this.lastTarget = lastTarget;
                this.targetTime = time;
                return;
            }

            return;
        }
    }

    protected Optional<Target> jumpTarget(ServerLevel level) {
        Optional<Target> target = WeightedRandom.getRandomItem(level.random, this.targets);
        target.ifPresent(this.targets::remove);
        return target;
    }

    protected boolean canLandOn(ServerLevel level, E entity, BlockPos pos) {
        BlockPos position = entity.blockPosition();
        int x = position.getX();
        int z = position.getZ();
        if (x == pos.getX() && z == pos.getZ()) {
            return false;
        } else if (!entity.getNavigation().isStableDestination(pos) && !this.landingBlocks.test(level.getBlockState(pos.below()))) {
            return false;
        } else {
            return entity.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic(entity.level, pos.mutable())) == 0.0F;
        }
    }

    @Nullable
    protected Vec3 getRammingVelocity(Mob entity, Vec3 pos) {
        List<Integer> angles = Lists.newArrayList(ANGLES);
        Collections.shuffle(angles);

        for (int angle : angles) {
            Vec3 velocity = this.getRammingVelocity(entity, pos, angle);
            if (velocity != null) return velocity;
        }

        return null;
    }

    @Nullable
    private Vec3 getRammingVelocity(Mob entity, Vec3 pos, int angle) {
        Vec3 position = entity.position();
        Vec3 scale = new Vec3(pos.x - position.x, 0.0, pos.z - position.z).normalize().scale(0.5D);
        pos = pos.subtract(scale);
        Vec3 distance = pos.subtract(position);
        float maxAngle = (float)angle * (float)Math.PI / 180.0F;
        double xzRange = Math.atan2(distance.z, distance.x);
        double yRange = distance.subtract(0.0D, distance.y, 0.0D).lengthSqr();
        double yRadius = Math.sqrt(yRange);
        double i = Math.sin(2.0F * maxAngle);
        double k = Math.pow(Math.cos(maxAngle), 2.0D);
        double yMax = Math.sin(maxAngle);
        double xzMax = Math.cos(maxAngle);
        double zOffset = Math.sin(xzRange);
        double xOffset = Math.cos(xzRange);
        double jumpHeight = yRange * 0.08 / (yRadius * i - 2.0 * distance.y * k);
        if (jumpHeight < 0.0) {
            return null;
        } else {
            double range = Math.sqrt(jumpHeight);
            if (range > (double)this.maxRange) {
                return null;
            } else {
                double xzDistance = range * xzMax;
                double yDistance = range * yMax;
                int radius = Mth.ceil(yRadius / xzDistance) * 2;
                double index = 0.0;
                Vec3 source = null;
                
                for (int j = 0; j < radius - 1; ++j) {
                    index += yRadius / (double)radius;
                    double x = index * xOffset;
                    double y = yMax / xzMax * index - Math.pow(index, 2.0) * 0.08 / (2.0 * jumpHeight * Math.pow(xzMax, 2.0));
                    double z = index * zOffset;
                    Vec3 target = new Vec3(position.x + x, position.y + y, position.z + z);
                    if (source != null && !this.canReach(entity, source, target)) {
                        return null;
                    }
                    
                    source = target;
                }
                
                return new Vec3(xzDistance * xOffset, yDistance, xzDistance * zOffset).scale(0.95F);
            }
        }
    }

    private boolean canReach(Mob entity, Vec3 source, Vec3 target) {
        EntityDimensions dimensions = entity.getDimensions(Pose.LONG_JUMPING);
        Vec3 distance = target.subtract(source);
        double size = Math.min(dimensions.width, dimensions.height);
        int height = Mth.ceil(distance.length() / size);
        Vec3 normal = distance.normalize();
        Vec3 vector = source;
        for (int i = 0; i < height; ++i) {
            vector = i == height - 1 ? target : vector.add(normal.scale(size * (double)0.9F));
            AABB box = dimensions.makeBoundingBox(vector);
            if (!entity.level.noCollision(entity, box)) {
                return false;
            }
        }

        return true;
    }

    public static class Target extends WeightedEntry.IntrusiveBase {
        private final BlockPos pos;

        public Target(BlockPos pos, int weight) {
            super(weight);
            this.pos = pos;
        }

        public BlockPos getPos() {
            return this.pos;
        }
    }
}