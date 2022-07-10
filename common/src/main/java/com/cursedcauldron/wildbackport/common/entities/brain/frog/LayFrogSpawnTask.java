package com.cursedcauldron.wildbackport.common.entities.brain.frog;

import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.Frog;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LayFrogSpawnTask extends Behavior<Frog> {
    private final Block frogSpawn;
    private final MemoryModuleType<?> triggerMemory;

    public LayFrogSpawnTask(Block block, MemoryModuleType<?> memoryModuleType) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT, WBMemoryModules.IS_PREGNANT.get(), MemoryStatus.VALUE_PRESENT));
        this.frogSpawn = block;
        this.triggerMemory = memoryModuleType;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverWorld, Frog arg) {
        return !arg.isInWaterOrBubble() && arg.isOnGround();
    }

    @Override
    protected void start(ServerLevel serverWorld, Frog arg, long l) {
        BlockPos blockPos = arg.blockPosition().below();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockPos3;
            BlockPos blockPos2 = blockPos.relative(direction);
            if (!serverWorld.getBlockState(blockPos2).is(Blocks.WATER) || !serverWorld.getBlockState(blockPos3 = blockPos2.above()).isAir()) continue;
            serverWorld.setBlock(blockPos3, this.frogSpawn.defaultBlockState(), 3);
            serverWorld.playSound(null, arg, WBSoundEvents.FROG_LAY_SPAWN, SoundSource.BLOCKS, 1.0f, 1.0f);
            arg.getBrain().eraseMemory(this.triggerMemory);
            return;
        }
    }
}