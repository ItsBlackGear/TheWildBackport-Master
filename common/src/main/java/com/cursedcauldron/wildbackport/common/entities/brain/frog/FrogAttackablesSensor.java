package com.cursedcauldron.wildbackport.common.entities.brain.frog;

import com.cursedcauldron.wildbackport.common.entities.Frog;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.NearestVisibleLivingEntitySensor;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//<>

public class FrogAttackablesSensor extends NearestVisibleLivingEntitySensor {
    @Override
    protected boolean isMatchingEntity(LivingEntity entity, LivingEntity target) {
        return !entity.getBrain().hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN) && Sensor.isEntityAttackable(entity, target) && Frog.isValidFrogFood(target) && !this.cantReachTarget(entity, target) && target.closerThan(entity, 10.0D);
    }

    private boolean cantReachTarget(LivingEntity entity, LivingEntity target) {
        List<UUID> targets = entity.getBrain().getMemory(WBMemoryModules.UNREACHABLE_TONGUE_TARGETS.get()).orElseGet(ArrayList::new);
        return targets.contains(target.getUUID());
    }

    @Override
    protected MemoryModuleType<LivingEntity> getMemory() {
        return MemoryModuleType.NEAREST_ATTACKABLE;
    }
}