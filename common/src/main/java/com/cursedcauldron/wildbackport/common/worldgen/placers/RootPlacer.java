package com.cursedcauldron.wildbackport.common.worldgen.placers;

import com.cursedcauldron.wildbackport.common.registry.worldgen.RootPlacerType;
import com.cursedcauldron.wildbackport.common.registry.WBRegistries;
import com.cursedcauldron.wildbackport.common.worldgen.decorator.AboveRootPlacement;
import com.cursedcauldron.wildbackport.common.worldgen.features.RootedTreeConfig;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;

public abstract class RootPlacer {
    public static final Codec<RootPlacer> CODEC = WBRegistries.ROOT_PLACER_TYPES.registry().byNameCodec().dispatch(RootPlacer::getType, RootPlacerType::codec);
    protected final IntProvider trunkOffsetY;
    protected final BlockStateProvider rootProvider;
    protected final Optional<AboveRootPlacement> aboveRootPlacement;

    protected static <P extends RootPlacer> Products.P3<RecordCodecBuilder.Mu<P>, IntProvider, BlockStateProvider, Optional<AboveRootPlacement>> codec(RecordCodecBuilder.Instance<P> instance) {
        return instance.group(IntProvider.CODEC.fieldOf("trunk_offset_y").forGetter(placer -> {
            return placer.trunkOffsetY;
        }), BlockStateProvider.CODEC.fieldOf("root_provider").forGetter(placer -> {
            return placer.rootProvider;
        }), AboveRootPlacement.CODEC.optionalFieldOf("above_root_placement").forGetter(placer -> {
            return placer.aboveRootPlacement;
        }));
    }

    public RootPlacer(IntProvider trunkOffsetY, BlockStateProvider rootProvider, Optional<AboveRootPlacement> aboveRootPlacement) {
        this.trunkOffsetY = trunkOffsetY;
        this.rootProvider = rootProvider;
        this.aboveRootPlacement = aboveRootPlacement;
    }

    protected abstract RootPlacerType<?> getType();

    public abstract boolean generate(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, BlockPos origin, RootedTreeConfig config);

    protected boolean canGrowThrough(LevelSimulatedReader level, BlockPos pos) {
        return TreeFeature.validTreePos(level, pos);
    }

    protected void placeRoots(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, RootedTreeConfig config) {
        if (this.canGrowThrough(level, pos)) {
            replacer.accept(pos, this.applyWaterlogging(level, pos, this.rootProvider.getState(random, pos)));
            if (this.aboveRootPlacement.isPresent()) {
                AboveRootPlacement placement = this.aboveRootPlacement.get();
                BlockPos above = pos.above();
                if (random.nextFloat() < placement.aboveRootPlacementChance() && level.isStateAtPosition(above, BlockBehaviour.BlockStateBase::isAir)) {
                    replacer.accept(above, this.applyWaterlogging(level, above, placement.aboveRootProvider().getState(random, above)));
                }
            }
        }
    }

    protected BlockState applyWaterlogging(LevelSimulatedReader level, BlockPos pos, BlockState state) {
        return state.hasProperty(BlockStateProperties.WATERLOGGED) ? state.setValue(BlockStateProperties.WATERLOGGED, level.isFluidAtPosition(pos, fluid -> fluid.is(FluidTags.WATER))) : state;
    }

    public BlockPos trunkOffset(BlockPos pos, Random random) {
        return pos.above(this.trunkOffsetY.sample(random));
    }
}