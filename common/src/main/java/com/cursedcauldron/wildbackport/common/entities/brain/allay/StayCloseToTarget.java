package com.cursedcauldron.wildbackport.common.entities.brain.allay;

import com.cursedcauldron.wildbackport.common.utils.MobUtils;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Optional;
import java.util.function.Function;

public class StayCloseToTarget<E extends LivingEntity> extends Behavior<E> {
    private final Function<LivingEntity, Optional<PositionTracker>> targetPosition;
    private final int closeEnough;
    private final int tooFar;
    private final float speedModifier;

    public StayCloseToTarget(Function<LivingEntity, Optional<PositionTracker>> targetPosition, int closeEnough, int tooFar, float speedModifier) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.targetPosition = targetPosition;
        this.closeEnough = closeEnough;
        this.tooFar = tooFar;
        this.speedModifier = speedModifier;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        Optional<PositionTracker> tracker = this.targetPosition.apply(entity);
        return tracker.isPresent() && !entity.position().closerThan(tracker.get().currentPosition(), this.tooFar);
    }

    @Override
    protected void start(ServerLevel level, E entity, long time) {
        MobUtils.setWalkAndLookTargetMemories(entity, this.targetPosition.apply(entity).get(), this.speedModifier, this.closeEnough);
    }
}