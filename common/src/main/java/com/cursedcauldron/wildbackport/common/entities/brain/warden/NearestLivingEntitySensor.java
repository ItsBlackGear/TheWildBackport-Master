package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

//<>

public class NearestLivingEntitySensor<T extends LivingEntity> extends Sensor<T> {
    @Override
    protected void doTick(ServerLevel level, T entity) {
        AABB box = entity.getBoundingBox().inflate(this.radiusXZ(), this.radiusY(), this.radiusXZ());
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, (target) -> target != entity && target.isAlive());
        entities.sort(Comparator.comparingDouble(entity::distanceToSqr));
        entity.getBrain().setMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES, entities);
        entity.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, new NearestVisibleLivingEntities(entity, entities));
    }

    protected int radiusXZ() {
        return 16;
    }

    protected int radiusY() {
        return 16;
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }
}