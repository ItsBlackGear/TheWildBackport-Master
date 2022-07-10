package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.cursedcauldron.wildbackport.common.entities.access.api.Poses;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class TryToSniff extends Behavior<Warden> {
    private static final IntProvider SNIFF_COOLDOWN = UniformInt.of(100, 200);

    public TryToSniff() {
        super(ImmutableMap.of(WBMemoryModules.SNIFF_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_ATTACKABLE, MemoryStatus.VALUE_PRESENT, WBMemoryModules.DISTURBANCE_LOCATION.get(), MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected void start(ServerLevel level, Warden warden, long time) {
        warden.getBrain().setMemory(WBMemoryModules.IS_SNIFFING.get(), Unit.INSTANCE);
        warden.getBrain().setMemoryWithExpiry(WBMemoryModules.SNIFF_COOLDOWN.get(), Unit.INSTANCE, SNIFF_COOLDOWN.sample(level.getRandom()));
        warden.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        warden.setPose(Poses.SNIFFING.get());
    }
}