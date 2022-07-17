package com.cursedcauldron.wildbackport.core.api.worldgen.forge;

import com.cursedcauldron.wildbackport.core.api.worldgen.BiomeWriter;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class ForgeBiomeWriter extends BiomeWriter {
    private BiomeLoadingEvent event;

    public BiomeWriter build(BiomeLoadingEvent event) {
        this.event = event;
        return this;
    }

    @Override
    public ResourceLocation name() {
        return this.event.getName();
    }

    @Override
    public void addFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature) {
        this.event.getGeneration().addFeature(step, feature);
    }

    @Override
    public void addSpawn(MobCategory category, EntityType<?> entityType, int weight, int minGroupSize, int maxGroupSize) {

    }
}