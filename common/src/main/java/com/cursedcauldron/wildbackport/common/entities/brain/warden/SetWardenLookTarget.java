package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

//<>

public class SetWardenLookTarget extends Behavior<Warden> {
    public SetWardenLookTarget() {
        super(ImmutableMap.of(WBMemoryModules.DISTURBANCE_LOCATION.get(), MemoryStatus.REGISTERED, WBMemoryModules.ROAR_TARGET.get(), MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Warden warden) {
        return warden.getBrain().hasMemoryValue(WBMemoryModules.DISTURBANCE_LOCATION.get()) || warden.getBrain().hasMemoryValue(WBMemoryModules.ROAR_TARGET.get());
    }

    @Override
    protected void start(ServerLevel level, Warden warden, long time) {
        BlockPos pos = warden.getBrain().getMemory(WBMemoryModules.ROAR_TARGET.get()).map(Entity::blockPosition).or(() -> warden.getBrain().getMemory(WBMemoryModules.DISTURBANCE_LOCATION.get())).get();
        warden.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos));
    }
}