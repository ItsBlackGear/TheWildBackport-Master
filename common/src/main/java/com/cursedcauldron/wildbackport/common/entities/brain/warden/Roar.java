package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.access.api.Poses;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.entities.brain.WardenBrain;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class Roar extends Behavior<Warden> {
    public Roar() {
        super(ImmutableMap.of(WBMemoryModules.ROAR_TARGET.get(), MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, WBMemoryModules.ROAR_SOUND_COOLDOWN.get(), MemoryStatus.REGISTERED, WBMemoryModules.ROAR_SOUND_DELAY.get(), MemoryStatus.REGISTERED), WardenBrain.ROAR_DURATION);
    }

    @Override
    protected void start(ServerLevel level, Warden warden, long time) {
        Brain<Warden> brain = warden.getBrain();
        brain.setMemoryWithExpiry(WBMemoryModules.ROAR_SOUND_DELAY.get(), Unit.INSTANCE, 25L);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        LivingEntity target = brain.getMemory(WBMemoryModules.ROAR_TARGET.get()).get();
        BehaviorUtils.lookAtEntity(warden, target);
        warden.setPose(Poses.ROARING.get());
        warden.increaseAngerAt(target, 20, false);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Warden warden, long time) {
        return true;
    }

    @Override
    protected void tick(ServerLevel level, Warden warden, long time) {
        if (!warden.getBrain().hasMemoryValue(WBMemoryModules.ROAR_SOUND_DELAY.get()) && !warden.getBrain().hasMemoryValue(WBMemoryModules.ROAR_SOUND_COOLDOWN.get())) {
            warden.getBrain().setMemoryWithExpiry(WBMemoryModules.ROAR_SOUND_COOLDOWN.get(), Unit.INSTANCE, WardenBrain.ROAR_DURATION - 25);
            warden.playSound(WBSoundEvents.WARDEN_ROAR, 3.0F, 1.0F);
        }
    }

    @Override
    protected void stop(ServerLevel level, Warden warden, long time) {
        if (warden.hasPose(Poses.ROARING.get())) warden.setPose(Pose.STANDING);

        warden.getBrain().getMemory(WBMemoryModules.ROAR_TARGET.get()).ifPresent(warden::updateAttackTarget);
        warden.getBrain().eraseMemory(WBMemoryModules.ROAR_TARGET.get());
    }
}