package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.access.api.Poses;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

//<>

public class Emerging<E extends Warden> extends Behavior<E> {
    public Emerging(int duration) {
        super(ImmutableMap.of(WBMemoryModules.IS_EMERGING.get(), MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), duration);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, E entity, long time) {
        return true;
    }

    @Override
    protected void start(ServerLevel level, E entity, long time) {
        entity.setPose(Poses.EMERGING.get());
        entity.playSound(WBSoundEvents.WARDEN_EMERGE, 5.0F, 1.0F);
    }

    @Override
    protected void stop(ServerLevel level, E entity, long time) {
        if (entity.hasPose(Poses.EMERGING.get())) {
            entity.setPose(Pose.STANDING);
        }
    }
}