package com.cursedcauldron.wildbackport.common.worldgen.features;

import com.cursedcauldron.wildbackport.common.blocks.SculkShriekerBlock;
import com.cursedcauldron.wildbackport.common.blocks.SculkSpreadManager;
import com.cursedcauldron.wildbackport.common.blocks.SculkSpreadable;
import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import com.cursedcauldron.wildbackport.common.utils.DirectionUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.Random;

//<>

public class SculkPatchFeature extends Feature<SculkPatchConfiguration> {
    public SculkPatchFeature(Codec<SculkPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SculkPatchConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        if (!this.canSpreadFrom(level, pos)) {
            return false;
        } else {
            SculkPatchConfiguration config = context.config();
            Random random = context.random();
            SculkSpreadManager spreader = SculkSpreadManager.createWorldGen();
            int rounds = config.spreadRounds() + config.growthRounds();

            for (int i = 0; i < rounds; i++) {
                for (int count = 0; count < config.chargeCount(); count++) {
                    spreader.spread(pos, config.amountPerCharge());
                }

                boolean spreadable = i < config.spreadRounds();

                for (int attempts = 0; attempts < config.spreadAttempts(); attempts++) {
                    spreader.tick(level, pos, random, spreadable);
                }

                spreader.clearCursors();
            }

            BlockPos catalystPos = pos.below();
            if (random.nextFloat() <= config.catalystChance() && level.getBlockState(catalystPos).isCollisionShapeFullBlock(level, catalystPos)) {
                level.setBlock(pos, WBBlocks.SCULK_CATALYST.get().defaultBlockState(), 3);
            }

            int extraRareGrowths = config.extraRareGrowths().sample(random);

            for (int i = 0; i < extraRareGrowths; i++) {
                BlockPos shriekPos = pos.offset(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
                if (level.getBlockState(shriekPos).isAir() && level.getBlockState(shriekPos.below()).isFaceSturdy(level, shriekPos.below(), Direction.UP)) {
                    level.setBlock(shriekPos, WBBlocks.SCULK_SHRIEKER.get().defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, true), 3);
                }
            }

            return true;
        }
    }

    private boolean canSpreadFrom(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof SculkSpreadable) {
            return true;
        } else {
            return (state.isAir() || (state.is(Blocks.WATER) && state.getFluidState().isSource())) && DirectionUtils.stream().map(pos::relative).anyMatch(position -> {
                return level.getBlockState(position).isCollisionShapeFullBlock(level, position);
            });
        }
    }
}