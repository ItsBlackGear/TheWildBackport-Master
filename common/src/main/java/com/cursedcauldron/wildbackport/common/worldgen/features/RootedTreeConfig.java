package com.cursedcauldron.wildbackport.common.worldgen.features;

import com.cursedcauldron.wildbackport.common.worldgen.placers.RootPlacer;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

import java.util.List;
import java.util.Optional;

//<>

public class RootedTreeConfig extends TreeConfiguration {
    public static final Codec<RootedTreeConfig> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter(config -> {
            return config.trunkProvider;
        }), TrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter(config -> {
            return config.trunkPlacer;
        }), BlockStateProvider.CODEC.fieldOf("foliage_provider").forGetter(config -> {
            return config.foliageProvider;
        }), FoliagePlacer.CODEC.fieldOf("foliage_placer").forGetter(config -> {
            return config.foliagePlacer;
        }), RootPlacer.CODEC.optionalFieldOf("root_placer").forGetter(config -> {
            return config.rootPlacer;
        }), BlockStateProvider.CODEC.fieldOf("dirt_provider").forGetter(config -> {
            return config.dirtProvider;
        }), FeatureSize.CODEC.fieldOf("minimum_size").forGetter(config -> {
            return config.minimumSize;
        }), TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter(config -> {
            return config.decorators;
        }), Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter(config -> {
            return config.ignoreVines;
        }), Codec.BOOL.fieldOf("force_dirt").orElse(false).forGetter(config -> {
            return config.forceDirt;
        })).apply(instance, RootedTreeConfig::new);
    });
    public final Optional<RootPlacer> rootPlacer;

    protected RootedTreeConfig(BlockStateProvider trunkProvider, TrunkPlacer trunkPlacer, BlockStateProvider foliageProvider, FoliagePlacer foliagePlacer, Optional<RootPlacer> rootPlacer, BlockStateProvider dirtProvider, FeatureSize minimumSize, List<TreeDecorator> decorators, boolean ignoreVines, boolean forceDirt) {
        super(trunkProvider, trunkPlacer, foliageProvider, foliagePlacer, dirtProvider, minimumSize, decorators, ignoreVines, forceDirt);
        this.rootPlacer = rootPlacer;
    }

    public static class Builder {
        public final BlockStateProvider trunkProvider;
        private final TrunkPlacer trunkPlacer;
        public final BlockStateProvider foliageProvider;
        private final FoliagePlacer foliagePlacer;
        private final Optional<RootPlacer> rootPlacer;
        private BlockStateProvider dirtProvider;
        private final FeatureSize minimumSize;
        private List<TreeDecorator> decorators = ImmutableList.of();
        private boolean ignoreVines;
        private boolean forceDirt;

        public Builder(BlockStateProvider trunkProvider, TrunkPlacer trunkPlacer, BlockStateProvider foliageProvider, FoliagePlacer foliagePlacer, Optional<RootPlacer> rootPlacer, FeatureSize minimumSize) {
            this.trunkProvider = trunkProvider;
            this.trunkPlacer = trunkPlacer;
            this.foliageProvider = foliageProvider;
            this.dirtProvider = BlockStateProvider.simple(Blocks.DIRT);
            this.foliagePlacer = foliagePlacer;
            this.rootPlacer = rootPlacer;
            this.minimumSize = minimumSize;
        }

        public Builder(BlockStateProvider trunkProvider, TrunkPlacer trunkPlacer, BlockStateProvider foliageProvider, FoliagePlacer foliagePlacer, FeatureSize minimumSize) {
            this(trunkProvider, trunkPlacer, foliageProvider, foliagePlacer, Optional.empty(), minimumSize);
        }

        public Builder dirtProvider(BlockStateProvider dirtProvider) {
            this.dirtProvider = dirtProvider;
            return this;
        }

        public Builder decorators(List<TreeDecorator> decorators) {
            this.decorators = decorators;
            return this;
        }

        public Builder ignoreVines() {
            this.ignoreVines = true;
            return this;
        }

        public Builder forceDirt() {
            this.forceDirt = true;
            return this;
        }

        public RootedTreeConfig build() {
            return new RootedTreeConfig(this.trunkProvider, this.trunkPlacer, this.foliageProvider, this.foliagePlacer, this.rootPlacer, this.dirtProvider, this.minimumSize, this.decorators, this.ignoreVines, this.forceDirt);
        }
    }
}