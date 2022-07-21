package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(PointedDripstoneBlock.class)
public interface PointedDripstoneBlockAccessor {
    @Invoker
    static Optional<BlockPos> callFindRootBlock(Level level, BlockPos blockPos2, BlockState blockState2, int i) {
        throw new UnsupportedOperationException();
    }

    @Invoker
    static boolean callIsStalactite(BlockState blockState) {
        throw new UnsupportedOperationException();
    }
}
