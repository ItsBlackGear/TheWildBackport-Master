package com.cursedcauldron.wildbackport.common.worldgen.decorator;

import com.cursedcauldron.wildbackport.common.registry.worldgen.WBTreeDecorators;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

//<>

public class WeightedLeaveVineDecorator extends TreeDecorator {
    public static final Codec<WeightedLeaveVineDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(WeightedLeaveVineDecorator::new, decorator -> {
        return decorator.probability;
    }).codec();
    private final float probability;

    public WeightedLeaveVineDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    public void place(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> replacer, Random random, List<BlockPos> trunkPositions, List<BlockPos> foliagePositions) {
        foliagePositions.forEach(pos -> {
            if (random.nextFloat() < this.probability && Feature.isAir(level, pos.west())) addHangingVine(level, pos.west(), VineBlock.EAST, replacer);
            if (random.nextFloat() < this.probability && Feature.isAir(level, pos.east())) addHangingVine(level, pos.east(), VineBlock.WEST, replacer);
            if (random.nextFloat() < this.probability && Feature.isAir(level, pos.north())) addHangingVine(level, pos.north(), VineBlock.SOUTH, replacer);
            if (random.nextFloat() < this.probability && Feature.isAir(level, pos.south())) addHangingVine(level, pos.south(), VineBlock.NORTH, replacer);
        });
    }

    private static void addHangingVine(LevelSimulatedReader level, BlockPos pos, BooleanProperty property, BiConsumer<BlockPos, BlockState> replacer) {
        LeaveVineDecorator.placeVine(replacer, pos, property);
        pos = pos.below();

        for (int i = 4; Feature.isAir(level, pos) && i > 0; --i) {
            LeaveVineDecorator.placeVine(replacer, pos, property);
            pos = pos.below();
        }
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return WBTreeDecorators.WEIGHTED_LEAVE_VINE.get();
    }
}