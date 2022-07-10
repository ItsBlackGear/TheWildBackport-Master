package com.cursedcauldron.wildbackport.common.entities.brain.frog;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.CollisionContext;

//<>

public class WalkTowardsWater extends Behavior<AgeableMob> {
    private final int distance;
    private final float speedModifier;
    private long nextStartTime;

    public WalkTowardsWater(int distance, float speedModifier) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
        this.distance = distance;
        this.speedModifier = speedModifier;
    }

    @Override
    protected void stop(ServerLevel level, AgeableMob entity, long time) {
        this.nextStartTime = time + 40L;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, AgeableMob entity) {
        return !entity.level.getFluidState(entity.blockPosition()).is(FluidTags.WATER);
    }

    @Override
    protected void start(ServerLevel level, AgeableMob entity, long time) {
        if (time >= this.nextStartTime) {
            CollisionContext context = CollisionContext.of(entity);
            BlockPos pos = entity.blockPosition();
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

            for (BlockPos position : BlockPos.withinManhattan(pos, this.distance, this.distance, this.distance)) {
                if (position.getX() != pos.getX() || position.getZ() != pos.getZ() && level.getBlockState(position).getCollisionShape(level, position, context).isEmpty() && level.getBlockState(mutable.setWithOffset(position, Direction.DOWN)).getCollisionShape(level, position, context).isEmpty()) {
                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        mutable.setWithOffset(position, direction);
                        if (level.getBlockState(mutable).isAir() && level.getBlockState(mutable.move(Direction.DOWN)).is(Blocks.WATER)) {
                            this.nextStartTime = time + 40L;
                            BehaviorUtils.setWalkAndLookTargetMemories(entity, position, this.speedModifier, 0);
                            return;
                        }
                    }
                }
            }
        }
    }
}