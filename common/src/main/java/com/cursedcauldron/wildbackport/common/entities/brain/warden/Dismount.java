package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;

public class Dismount extends Behavior<LivingEntity> {
    public Dismount() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LivingEntity entity) {
        return entity.isPassenger();
    }

    @Override
    protected void start(ServerLevel level, LivingEntity entity, long time) {
        entity.unRide();
    }
}