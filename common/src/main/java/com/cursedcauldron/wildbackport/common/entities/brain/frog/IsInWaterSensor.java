package com.cursedcauldron.wildbackport.common.entities.brain.frog;

import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.Set;

public class IsInWaterSensor extends Sensor<LivingEntity> {

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(WBMemoryModules.IS_IN_WATER.get());
    }

    @Override
    protected void doTick(ServerLevel world, LivingEntity entity) {
        if (entity.isInWater()) {
            entity.getBrain().setMemory(WBMemoryModules.IS_IN_WATER.get(), Unit.INSTANCE);
        } else {
            entity.getBrain().eraseMemory(WBMemoryModules.IS_IN_WATER.get());
        }
    }
}