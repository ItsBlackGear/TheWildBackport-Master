package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.access.api.Poses;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.entities.brain.WardenBrain;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class Sniffing<E extends Warden> extends Behavior<E> {
    public Sniffing(int duration) {
        super(ImmutableMap.of(WBMemoryModules.IS_SNIFFING.get(), MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.NEAREST_ATTACKABLE, MemoryStatus.REGISTERED, WBMemoryModules.DISTURBANCE_LOCATION.get(), MemoryStatus.REGISTERED, WBMemoryModules.SNIFF_COOLDOWN.get(), MemoryStatus.REGISTERED), duration);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, E entity, long time) {
        return true;
    }

    @Override
    protected void start(ServerLevel level, E entity, long time) {
        entity.playSound(WBSoundEvents.WARDEN_SNIFF, 5.0F, 1.0F);
    }

    @Override
    protected void stop(ServerLevel level, E entity, long time) {
        if (entity.hasPose(Poses.SNIFFING.get())) {
            entity.setPose(Pose.STANDING);
        }

        entity.getBrain().eraseMemory(WBMemoryModules.IS_SNIFFING.get());
        entity.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE).filter(entity::isValidTarget).ifPresent(target -> {
            if (entity.closerThan(target, 6.0D, 20.0D)) {
                entity.increaseAngerAt(target);
            }

            if (!entity.getBrain().hasMemoryValue(WBMemoryModules.DISTURBANCE_LOCATION.get())) {
                WardenBrain.lookAtDisturbance(entity, target.blockPosition());
            }
        });
    }
}