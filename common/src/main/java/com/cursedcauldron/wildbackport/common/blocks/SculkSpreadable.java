package com.cursedcauldron.wildbackport.common.blocks;

import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Random;

public interface SculkSpreadable {
    SculkSpreadable DEFAULT = new SculkSpreadable() {
        @Override
        public boolean spread(LevelAccessor level, BlockPos pos, BlockState state, @Nullable Collection<Direction> directions, boolean postProcess) {
            if (directions == null) {
                return ((SculkVeinBlock)WBBlocks.SCULK_VEIN.get()).getSamePositionOnlyGrower().grow(level.getBlockState(pos), level, pos, postProcess) > 0L;
            } else if(!directions.isEmpty()) {
                return (state.isAir() || state.getFluidState().is(Fluids.WATER)) && SculkVeinBlock.place(level, pos, state, directions);
            } else {
                return SculkSpreadable.super.spread(level, pos, state, directions, postProcess);
            }
        }

        @Override
        public int spread(SculkSpreadManager.Cursor cursor, LevelAccessor level, BlockPos pos, Random random, SculkSpreadManager spreadManager, boolean shouldConvert) {
            return cursor.getDecayDelay() > 0 ? cursor.getCharge() : 0;
        }

        @Override
        public int getDecay(int oldDecay) {
            return Math.max(oldDecay - 1, 0);
        }
    };

    default byte getUpdate() {
        return 1;
    }

    default void spreadAtSamePosition(LevelAccessor level, BlockState state, BlockPos pos, Random random) {}

    default boolean spread(LevelAccessor level, BlockPos pos, BlockState state, @Nullable Collection<Direction> directions, boolean postProcess) {
        return ((SculkVeinBlock)WBBlocks.SCULK_VEIN.get()).getAllGrowTypeGrower().grow(state, level, pos, postProcess) > 0L;
    }

    default boolean shouldConvertToSpreadable() {
        return true;
    }

    default int getDecay(int oldDecay) {
        return 1;
    }

    int spread(SculkSpreadManager.Cursor cursor, LevelAccessor level, BlockPos pos, Random random, SculkSpreadManager manager, boolean shouldConvert);
}