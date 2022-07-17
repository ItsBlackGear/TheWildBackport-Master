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

public class LayFrogSpawn extends Behavior<Frog> {
    private final Block frogSpawn;
    private final MemoryModuleType<?> triggerMemory;

    public LayFrogSpawn(Block block, MemoryModuleType<?> triggerMemory) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT, WBMemoryModules.IS_PREGNANT.get(), MemoryStatus.VALUE_PRESENT));
        this.frogSpawn = block;
        this.triggerMemory = triggerMemory;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Frog frog) {
        return !frog.isInWaterOrBubble() && frog.isOnGround();
    }

    @Override
    protected void start(ServerLevel level, Frog frog, long time) {
        BlockPos blockPos = frog.blockPosition().below();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos offset = blockPos.relative(direction);
            BlockPos above = offset.above();
            if (level.getBlockState(offset).is(Blocks.WATER) && level.getBlockState(above).isAir()) {
                level.setBlock(above, this.frogSpawn.defaultBlockState(), 3);
                level.playSound(null, frog, WBSoundEvents.FROG_LAY_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                frog.getBrain().eraseMemory(this.triggerMemory);
                return;
            }
        }
    }
}