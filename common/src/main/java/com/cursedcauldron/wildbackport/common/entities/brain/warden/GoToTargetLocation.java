package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Random;

public class GoToTargetLocation<E extends Mob> extends Behavior<E> {
    private final MemoryModuleType<BlockPos> locationMemory;
    private final int closeEnoughDistance;
    private final float speedModifier;

    public GoToTargetLocation(MemoryModuleType<BlockPos> locationMemory, int closeEnoughDistance, float speedModifier) {
        super(ImmutableMap.of(locationMemory, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
        this.locationMemory = locationMemory;
        this.closeEnoughDistance = closeEnoughDistance;
        this.speedModifier = speedModifier;
    }

    @Override
    protected void start(ServerLevel level, E entity, long time) {
        BlockPos pos = this.getTargetLocation(entity);
        boolean inRange = pos.closerThan(entity.blockPosition(), this.closeEnoughDistance);
        if (!inRange) BehaviorUtils.setWalkAndLookTargetMemories(entity, getNearbyPos(entity, pos), this.speedModifier, this.closeEnoughDistance);
    }

    private static BlockPos getNearbyPos(Mob mob, BlockPos pos) {
        Random random = mob.level.getRandom();
        return pos.offset(getRandomOffset(random), 0, getRandomOffset(random));
    }

    private static int getRandomOffset(Random random) {
        return random.nextInt(3) - 1;
    }

    private BlockPos getTargetLocation(Mob mob) {
        return mob.getBrain().getMemory(this.locationMemory).get();
    }
}