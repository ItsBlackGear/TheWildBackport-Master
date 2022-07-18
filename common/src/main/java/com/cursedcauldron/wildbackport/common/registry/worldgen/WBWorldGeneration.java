package com.cursedcauldron.wildbackport.common.registry.worldgen;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.blocks.MangrovePropaguleBlock;
import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import com.cursedcauldron.wildbackport.common.tag.WBBlockTags;
import com.cursedcauldron.wildbackport.common.worldgen.PredicatedStateProvider;
import com.cursedcauldron.wildbackport.common.worldgen.WorldGenerator;
import com.cursedcauldron.wildbackport.common.worldgen.decorator.AttachedToLeavesDecorator;
import com.cursedcauldron.wildbackport.common.worldgen.decorator.LayerRootDecorator;
import com.cursedcauldron.wildbackport.common.worldgen.decorator.MangroveRootPlacement;
import com.cursedcauldron.wildbackport.common.worldgen.decorator.WeightedLeaveVineDecorator;
import com.cursedcauldron.wildbackport.common.worldgen.features.GrassDiskConfiguration;
import com.cursedcauldron.wildbackport.common.worldgen.features.RootedTreeConfig;
import com.cursedcauldron.wildbackport.common.worldgen.features.SculkPatchConfiguration;
import com.cursedcauldron.wildbackport.common.worldgen.placers.MangroveRootPlacer;
import com.cursedcauldron.wildbackport.common.worldgen.placers.UpwardBranchingTrunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GlowLichenConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import net.minecraft.world.level.material.Fluids;

import java.util.List;
import java.util.Optional;

//<>

public class WBWorldGeneration {
    public static void bootstrap() {
        WorldGenerator.setup();
    }

    // Mangrove Swamp

    public static final Holder<ConfiguredFeature<RootedTreeConfig, ?>> MANGROVE                                 = config("mangrove", WBFeatures.TREE.get(), new RootedTreeConfig.Builder(
            BlockStateProvider.simple(WBBlocks.MANGROVE_LOG.get()),
            new UpwardBranchingTrunk(2, 1, 4, UniformInt.of(1, 4), 0.5F, UniformInt.of(0, 1), Registry.BLOCK.getOrCreateTag(WBBlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)),
            BlockStateProvider.simple(WBBlocks.MANGROVE_LEAVES.get()),
            new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70),
            Optional.of(new MangroveRootPlacer(UniformInt.of(1, 3), BlockStateProvider.simple(WBBlocks.MANGROVE_ROOTS.get()), Optional.of(new LayerRootDecorator(BlockStateProvider.simple(Blocks.MOSS_CARPET), 0.5F)), new MangroveRootPlacement(Registry.BLOCK.getOrCreateTag(WBBlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), HolderSet.direct(Block::builtInRegistryHolder, WBBlocks.MUD.get(), WBBlocks.MUDDY_MANGROVE_ROOTS.get()), BlockStateProvider.simple(WBBlocks.MUDDY_MANGROVE_ROOTS.get()), 8, 15, 0.2F))),
            new TwoLayersFeatureSize(2, 0, 2)
    ).decorators(List.of(
        new WeightedLeaveVineDecorator(0.125F),
        new AttachedToLeavesDecorator(0.14F, 1, 0, new RandomizedIntStateProvider(BlockStateProvider.simple(WBBlocks.MANGROVE_PROPAGULE.get().defaultBlockState().setValue(MangrovePropaguleBlock.HANGING, true)), MangrovePropaguleBlock.AGE, UniformInt.of(0, 4)), 2, List.of(Direction.DOWN)),
        new BeehiveDecorator(0.01F)
    )).ignoreVines().build());

    public static final Holder<ConfiguredFeature<RootedTreeConfig, ?>> TALL_MANGROVE                            = config("tall_mangrove", WBFeatures.TREE.get(), new RootedTreeConfig.Builder(
            BlockStateProvider.simple(WBBlocks.MANGROVE_LOG.get()),
            new UpwardBranchingTrunk(4, 1, 9, UniformInt.of(1, 6), 0.5F, UniformInt.of(0, 1), Registry.BLOCK.getOrCreateTag(WBBlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)),
            BlockStateProvider.simple(WBBlocks.MANGROVE_LEAVES.get()),
            new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 70),
            Optional.of(new MangroveRootPlacer(UniformInt.of(3, 7), BlockStateProvider.simple(WBBlocks.MANGROVE_ROOTS.get()), Optional.of(new LayerRootDecorator(BlockStateProvider.simple(Blocks.MOSS_CARPET), 0.5F)), new MangroveRootPlacement(Registry.BLOCK.getOrCreateTag(WBBlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH), HolderSet.direct(Block::builtInRegistryHolder, WBBlocks.MUD.get(), WBBlocks.MUDDY_MANGROVE_ROOTS.get()), BlockStateProvider.simple(WBBlocks.MUDDY_MANGROVE_ROOTS.get()), 8, 15, 0.2F))),
            new TwoLayersFeatureSize(3, 0, 2)
    ).decorators(List.of(
            new WeightedLeaveVineDecorator(0.125F),
            new AttachedToLeavesDecorator(0.14F, 1, 0, new RandomizedIntStateProvider(BlockStateProvider.simple(WBBlocks.MANGROVE_PROPAGULE.get().defaultBlockState().setValue(MangrovePropaguleBlock.HANGING, true)), MangrovePropaguleBlock.AGE, UniformInt.of(0, 4)), 2, List.of(Direction.DOWN)),
            new BeehiveDecorator(0.01F)
    )).ignoreVines().build());

    public static final Holder<PlacedFeature> MANGROVE_CHECKED                                                  = place("mangrove_checked", MANGROVE, PlacementUtils.filteredByBlockSurvival(WBBlocks.MANGROVE_PROPAGULE.get()));
    public static final Holder<PlacedFeature> TALL_MANGROVE_CHECKED                                             = place("tall_mangrove_checked", TALL_MANGROVE, PlacementUtils.filteredByBlockSurvival(WBBlocks.MANGROVE_PROPAGULE.get()));

    public static final Holder<ConfiguredFeature<RandomFeatureConfiguration, ?>> MANGROVE_VEGETATION            = config("mangrove_vegetation", Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(
            new WeightedPlacedFeature(TALL_MANGROVE_CHECKED, 0.85F)),
            MANGROVE_CHECKED
    ));
    public static final Holder<PlacedFeature> TREES_MANGROVE                                                    = place("trees_mangrove", MANGROVE_VEGETATION, CountPlacement.of(25), InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(5), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome(), BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(WBBlocks.MANGROVE_PROPAGULE.get().defaultBlockState(), BlockPos.ZERO)));

    public static final Holder<ConfiguredFeature<GrassDiskConfiguration, ?>> DISK_GRASS_CONFIG                  = config("disk_grass", WBFeatures.DISK.get(), new GrassDiskConfiguration(
            new PredicatedStateProvider(BlockStateProvider.simple(Blocks.DIRT), List.of(new PredicatedStateProvider.Rule(BlockPredicate.not(BlockPredicate.allOf(BlockPredicate.solid(Direction.UP.getNormal()), BlockPredicate.matchesFluid(Fluids.WATER, Direction.UP.getNormal()))), BlockStateProvider.simple(Blocks.GRASS_BLOCK)))),
            BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, WBBlocks.MUD.get())),
            UniformInt.of(2, 6),
            2
    ));
    public static final Holder<PlacedFeature> DISK_GRASS                                                        = place("disk_grass", DISK_GRASS_CONFIG, CountPlacement.of(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlock(WBBlocks.MUD.get(), Vec3i.ZERO)), BiomeFilter.biome());

    // Deep Dark

    public static final Holder<ConfiguredFeature<SculkPatchConfiguration, ?>> SCULK_PATCH_DEEP_DARK_CONFIG      = config("sculk_patch_deep_dark", WBFeatures.SCULK_PATCH.get(), new SculkPatchConfiguration(10, 32, 64, 0, 1, ConstantInt.of(0), 0.5F));
    public static final Holder<PlacedFeature> SCULK_PATCH_DEEP_DARK                                             = place("sculk_patch_deep_dark", SCULK_PATCH_DEEP_DARK_CONFIG, CountPlacement.of(ConstantInt.of(256)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());

    public static final Holder<ConfiguredFeature<SculkPatchConfiguration, ?>> SCULK_PATCH_ANCIENT_CITY_CONFIG   = config("sculk_patch_ancient_city", WBFeatures.SCULK_PATCH.get(), new SculkPatchConfiguration(10, 32, 64, 0, 1, UniformInt.of(1, 3), 0.5F));
    public static final Holder<PlacedFeature> SCULK_PATCH_ANCIENT_CITY                                          = place("sculk_patch_ancient_city", SCULK_PATCH_ANCIENT_CITY_CONFIG);

    public static final Holder<ConfiguredFeature<GlowLichenConfiguration, ?>> SCULK_VEIN_CONFIG                 = config("sculk_vein", WBFeatures.SCULK_GROWTH.get(), new GlowLichenConfiguration(20, true, true, true, 1.0F, HolderSet.direct(Block::builtInRegistryHolder, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.DRIPSTONE_BLOCK, Blocks.CALCITE, Blocks.TUFF, Blocks.DEEPSLATE)));
    public static final Holder<PlacedFeature> SCULK_VEIN                                                        = place("sculk_vein", SCULK_VEIN_CONFIG, CountPlacement.of(UniformInt.of(204, 250)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome());

    // Registry

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<FC, ?>> config(String key, F feature, FC configuration) {
        return BuiltinRegistries.registerExact(BuiltinRegistries.CONFIGURED_FEATURE, WildBackport.MOD_ID + ":" + key, new ConfiguredFeature<>(feature, configuration));
    }

    public static Holder<PlacedFeature> place(String key, Holder<? extends ConfiguredFeature<?, ?>> feature, PlacementModifier... placements) {
        return BuiltinRegistries.registerExact(BuiltinRegistries.PLACED_FEATURE, WildBackport.MOD_ID + ":" + key, new PlacedFeature(Holder.hackyErase(feature), List.copyOf(List.of(placements))));
    }
}