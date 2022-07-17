package com.cursedcauldron.wildbackport.common.worldgen.features;

import com.cursedcauldron.wildbackport.common.blocks.SculkVeinBlock;
import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.GlowLichenConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SculkGrowthFeature extends Feature<GlowLichenConfiguration> {
    public SculkGrowthFeature(Codec<GlowLichenConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<GlowLichenConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        Random random = context.random();
        GlowLichenConfiguration config = context.config();
        if (!isNotAirOrWater(level.getBlockState(pos))) {
            List<Direction> directions = getShuffledDirections(config, random);
            if (placeGrowthIfPossible(level, pos, level.getBlockState(pos), config, random, directions)) {
                return true;
            } else {
                BlockPos.MutableBlockPos mutable = pos.mutable();

                for (Direction direction : directions) {
                    mutable.set(pos);
                    List<Direction> filteredDirections = getShuffledDirectionsExcept(config, random, direction.getOpposite());

                    for (int i = 0; i < config.searchRange; i++) {
                        mutable.setWithOffset(pos, direction);
                        BlockState state = level.getBlockState(mutable);
                        if (isNotAirOrWater(state) && !state.is(WBBlocks.SCULK_VEIN.get())) {
                            break;
                        }

                        if (placeGrowthIfPossible(level, mutable, state, config, random, filteredDirections)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static boolean placeGrowthIfPossible(WorldGenLevel level, BlockPos pos, BlockState state, GlowLichenConfiguration config, Random random, List<Direction> directions) {
        BlockPos.MutableBlockPos mutable = pos.mutable();

        for (Direction direction : directions) {
            BlockState blockState = level.getBlockState(mutable.setWithOffset(pos, direction));
            if (blockState.is(config.canBePlacedOn)) {
                SculkVeinBlock veinBlock = (SculkVeinBlock) WBBlocks.SCULK_VEIN.get();
                BlockState veinState = veinBlock.getStateForPlacement(state, level, pos, direction);
                if (veinState == null) {
                    return false;
                }

                level.setBlock(pos, veinState, 3);
                level.getChunk(pos).markPosForPostprocessing(pos);
                if (random.nextFloat() < config.chanceOfSpreading) {
                    veinBlock.allGrowTypeGrower.grow(veinState, level, pos, direction, random, true);
                }

                return true;
            }
        }

        return false;
    }

    public static List<Direction> getShuffledDirections(GlowLichenConfiguration config, Random random) {
        List<Direction> list = Lists.newArrayList(config.validDirections);
        Collections.shuffle(list, random);
        return list;
    }

    public static List<Direction> getShuffledDirectionsExcept(GlowLichenConfiguration config, Random random, Direction except) {
        List<Direction> list = config.validDirections.stream().filter(direction -> direction != except).collect(Collectors.toList());
        Collections.shuffle(list, random);
        return list;
    }

    private static boolean isNotAirOrWater(BlockState state) {
        return !state.isAir() && !state.is(Blocks.WATER);
    }
}