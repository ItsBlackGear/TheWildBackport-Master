package com.cursedcauldron.wildbackport.common.worldgen.structure.processors;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.tag.WBBlockTags;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class WBProcessorLists {
    public static final Holder<StructureProcessorList> ANCIENT_CITY_START_DEGRADATION = create("ancient_city_start_degradation", ImmutableList.of(new RuleProcessor(ImmutableList.of(new ProcessorRule(new RandomBlockMatchTest(Blocks.DEEPSLATE_BRICKS, 0.3F), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState()), new ProcessorRule(new RandomBlockMatchTest(Blocks.DEEPSLATE_TILES, 0.3F), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_DEEPSLATE_TILES.defaultBlockState()), new ProcessorRule(new RandomBlockMatchTest(Blocks.SOUL_LANTERN, 0.05F), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()))), new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE)));
    public static final Holder<StructureProcessorList> ANCIENT_CITY_GENERIC_DEGRADATION = create("ancient_city_generic_degradation", ImmutableList.of(new BlockTagRotProcessor(WBBlockTags.ANCIENT_CITY_REPLACEABLE, 0.95F), new RuleProcessor(ImmutableList.of(new ProcessorRule(new RandomBlockMatchTest(Blocks.DEEPSLATE_BRICKS, 0.3F), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState()), new ProcessorRule(new RandomBlockMatchTest(Blocks.DEEPSLATE_TILES, 0.3F), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_DEEPSLATE_TILES.defaultBlockState()), new ProcessorRule(new RandomBlockMatchTest(Blocks.SOUL_LANTERN, 0.05F), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()))), new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE)));
    public static final Holder<StructureProcessorList> ANCIENT_CITY_WALLS_DEGRADATION = create("ancient_city_walls_degradation", ImmutableList.of(new BlockTagRotProcessor(WBBlockTags.ANCIENT_CITY_REPLACEABLE, 0.95F), new RuleProcessor(ImmutableList.of(new ProcessorRule(new RandomBlockMatchTest(Blocks.DEEPSLATE_BRICKS, 0.3F), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState()), new ProcessorRule(new RandomBlockMatchTest(Blocks.DEEPSLATE_TILES, 0.3F), AlwaysTrueTest.INSTANCE, Blocks.CRACKED_DEEPSLATE_TILES.defaultBlockState()), new ProcessorRule(new RandomBlockMatchTest(Blocks.DEEPSLATE_TILE_SLAB, 0.3F), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()), new ProcessorRule(new RandomBlockMatchTest(Blocks.SOUL_LANTERN, 0.05F), AlwaysTrueTest.INSTANCE, Blocks.AIR.defaultBlockState()))), new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE)));

    public static void bootstrap() {}

    private static Holder<StructureProcessorList> create(String key, ImmutableList<StructureProcessor> processors) {
        return BuiltinRegistries.register(BuiltinRegistries.PROCESSOR_LIST, new ResourceLocation(WildBackport.MOD_ID, key), new StructureProcessorList(processors));
    }
}