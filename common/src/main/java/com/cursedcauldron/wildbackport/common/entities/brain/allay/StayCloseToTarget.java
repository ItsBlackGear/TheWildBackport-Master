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
    private final Function<LivingEntity, Optional<PositionTracker>> lookTarget;
    private final int completitionRange;
    private final int searchRange;
    private final float speed;

    public StayCloseToTarget(Function<LivingEntity, Optional<PositionTracker>> lookTarget, int completitionRange, int searchRange, float speed) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
        this.lookTarget = lookTarget;
        this.completitionRange = completitionRange;
        this.searchRange = searchRange;
        this.speed = speed;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        Optional<PositionTracker> tracker = this.lookTarget.apply(entity);
        return tracker.isPresent() && !entity.position().closerThan(tracker.get().currentPosition(), this.searchRange);
    }

    @Override
    protected void start(ServerLevel level, E entity, long time) {
        MobUtils.walkTowards(entity, this.lookTarget.apply(entity).get(), this.speed, this.completitionRange);
    }
}