package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Optional;
import java.util.function.Function;

public class SetRoarTarget<E extends Warden> extends Behavior<E> {
    private final Function<E, Optional<? extends LivingEntity>> targetFinder;

    public SetRoarTarget(Function<E, Optional<? extends LivingEntity>> target) {
        super(ImmutableMap.of(WBMemoryModules.ROAR_TARGET.get(), MemoryStatus.VALUE_ABSENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED));
        this.targetFinder = target;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return this.targetFinder.apply(entity).filter(entity::isValidTarget).isPresent();
    }

    @Override
    protected void start(ServerLevel level, E entity, long time) {
        this.targetFinder.apply(entity).ifPresent(target -> {
            entity.getBrain().setMemory(WBMemoryModules.ROAR_TARGET.get(), target);
            entity.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        });
    }
}