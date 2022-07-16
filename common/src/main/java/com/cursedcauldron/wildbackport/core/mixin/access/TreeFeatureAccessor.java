package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TreeFeature.class)
public interface TreeFeatureAccessor {
    @Invoker("isVine")
    static boolean isVine(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
        throw new UnsupportedOperationException();
    }

    @Invoker("isBlockWater")
    static boolean isBlockWater(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
        throw new UnsupportedOperationException();
    }

    @Invoker("isReplaceablePlant")
    static boolean isReplaceablePlant(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
        throw new UnsupportedOperationException();
    }

    @Invoker("setBlockKnownShape")
    static void setBlockKnownShape(LevelWriter levelWriter, BlockPos blockPos, BlockState blockState) {
        throw new UnsupportedOperationException();
    }
}
