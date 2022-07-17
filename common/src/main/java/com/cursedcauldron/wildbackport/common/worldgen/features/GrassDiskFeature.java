package com.cursedcauldron.wildbackport.common.worldgen.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.Random;

public class GrassDiskFeature extends Feature<GrassDiskConfiguration> {
    public GrassDiskFeature(Codec<GrassDiskConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<GrassDiskConfiguration> context) {
        GrassDiskConfiguration config = context.config();
        BlockPos pos = context.origin();
        WorldGenLevel level = context.level();
        Random random = context.random();
        boolean place = false;
        int y = pos.getY();
        int topY = y + config.halfHeight();
        int bottomY = y - config.halfHeight() - 1;
        int radius = config.radius().sample(random);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (BlockPos position : BlockPos.betweenClosed(pos.offset(-radius, 0, -radius), pos.offset(radius, 0, radius))) {
            int x = position.getX() - position.getX();
            int z = position.getZ() - position.getZ();
            if (x * x + z * z <= radius * radius) {
                place |= this.placeBlock(config, level, random, topY, bottomY, mutable.set(position));
            }
        }

        return place;
    }

    protected boolean placeBlock(GrassDiskConfiguration config, WorldGenLevel level, Random random, int topY, int bottomY, BlockPos.MutableBlockPos pos) {
        boolean place = false;
        for (int y = topY; y > bottomY; y--) {
            pos.setY(y);
            if (config.target().test(level, pos)) {
                BlockState state = config.stateProvider().getBlockState(level, random, pos);
                level.setBlock(pos, state, 2);
                this.markAboveForPostProcessing(level, pos);
                place = true;
            }
        }

        return place;
    }
}