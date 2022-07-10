package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.access.api.Poses;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

//<>

public class Digging<E extends Warden> extends Behavior<E> {
    public Digging(int duration) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), duration);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, E entity, long time) {
        return entity.getRemovalReason() == null;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return entity.isOnGround() || entity.isInWater() || entity.isInLava();
    }

    @Override
    protected void start(ServerLevel level, E entity, long time) {
        if (entity.isOnGround()) {
            entity.setPose(Poses.DIGGING.get());
            entity.playSound(WBSoundEvents.WARDEN_DIG, 5.0F, 1.0F);
        } else {
            entity.playSound(WBSoundEvents.WARDEN_AGITATED, 5.0F, 1.0F);
            this.stop(level, entity, time);
        }
    }

    @Override
    protected void stop(ServerLevel level, E entity, long time) {
        if (entity.getRemovalReason() == null) {
            entity.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}