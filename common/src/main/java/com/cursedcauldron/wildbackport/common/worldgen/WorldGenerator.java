package com.cursedcauldron.wildbackport.common.worldgen;

import com.cursedcauldron.wildbackport.common.registry.WBBiomes;
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntities;
import com.cursedcauldron.wildbackport.common.registry.worldgen.WBWorldGeneration;
import com.cursedcauldron.wildbackport.core.api.worldgen.BiomeModifier;
import com.cursedcauldron.wildbackport.core.api.worldgen.BiomeWriter;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.GenerationStep;

//<>

public class WorldGenerator {
    public static void setup() {
        BiomeModifier.add(WorldGenerator::mangroveSwamp, WBBiomes.MANGROVE_SWAMP);
    }

    public static void mangroveSwamp(BiomeWriter writer) {
        writer.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, WBWorldGeneration.DISK_GRASS);

        writer.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, WBWorldGeneration.TREES_MANGROVE);
//        writer.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL);
//        writer.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH);
//        writer.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_WATERLILY);

        writer.addSpawn(MobCategory.MONSTER, EntityType.SLIME, 1, 1, 1);
        writer.addSpawn(MobCategory.CREATURE, WBEntities.FROG.get(), 10, 2, 5);
        writer.addSpawn(MobCategory.WATER_AMBIENT, EntityType.TROPICAL_FISH, 25, 8, 8);
    }
}