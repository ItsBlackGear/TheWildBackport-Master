package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class WardenEntitySensor extends NearestLivingEntitySensor<Warden> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.copyOf(Iterables.concat(super.requires(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

    @Override
    protected void doTick(ServerLevel level, Warden warden) {
        super.doTick(level, warden);
        getClosest(warden, target -> {
            return target.getType() == EntityType.PLAYER;
        }).or(() -> {
            return getClosest(warden, target -> {
                return target.getType() != EntityType.PLAYER;
            });
        }).ifPresentOrElse(target -> {
            warden.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, target);
        }, () -> {
            warden.getBrain().eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE);
        });
    }

    private static Optional<LivingEntity> getClosest(Warden warden, Predicate<LivingEntity> targetFilter) {
        return warden.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).stream().flatMap(Collection::stream).filter(warden::isValidTarget).filter(targetFilter).findFirst();
    }

    @Override
    protected int radiusXZ() {
        return 24;
    }

    @Override
    protected int radiusY() {
        return 24;
    }
}