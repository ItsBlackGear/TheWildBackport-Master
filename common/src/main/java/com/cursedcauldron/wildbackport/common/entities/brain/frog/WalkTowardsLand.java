package com.cursedcauldron.wildbackport.common.entities.brain.frog;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class WalkTowardsLand extends Behavior<PathfinderMob> {
    private final int distance;
    private final float speedModifier;
    private long nextStartTime;

    public WalkTowardsLand(int distance, float speedModifier) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
        this.distance = distance;
        this.speedModifier = speedModifier;
    }

    @Override
    protected void stop(ServerLevel level, PathfinderMob entity, long time) {
        this.nextStartTime = time + 60L;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PathfinderMob entity) {
        return entity.level.getFluidState(entity.blockPosition()).is(FluidTags.WATER);
    }

    @Override
    protected void start(ServerLevel level, PathfinderMob entity, long time) {
        if (time >= this.nextStartTime) {
            BlockPos pos = entity.blockPosition();
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            CollisionContext context = CollisionContext.of(entity);

            for (BlockPos position : BlockPos.withinManhattan(pos, this.distance, this.distance, this.distance)) {
                if (position.getX() != pos.getX() || position.getZ() != pos.getZ()) {
                    BlockState state = level.getBlockState(position);
                    BlockState landState = level.getBlockState(mutable.setWithOffset(position, Direction.DOWN));
                    if (!state.is(Blocks.WATER) && level.getFluidState(position).isEmpty() && state.getCollisionShape(level, position, context).isEmpty() && landState.isFaceSturdy(level, mutable, Direction.UP)) {
                        this.nextStartTime = time + 60L;
                        BehaviorUtils.setWalkAndLookTargetMemories(entity, position.immutable(), this.speedModifier, 1);
                        return;
                    }
                }
            }
        }
    }
}