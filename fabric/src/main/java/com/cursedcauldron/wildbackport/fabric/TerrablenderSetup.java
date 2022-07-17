package com.cursedcauldron.wildbackport.fabric;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.registry.WBBiomes;
import com.cursedcauldron.wildbackport.common.worldgen.MangroveSwampSurface;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.TerraBlenderApi;

import java.util.List;
import java.util.function.Consumer;

public class TerrablenderSetup implements TerraBlenderApi {
    @Override
    public void onTerraBlenderInitialized() {
        Regions.register(new Region(new ResourceLocation(WildBackport.MOD_ID, "overworld"), RegionType.OVERWORLD, 2) {
            @Override public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
                this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
                    builder.replaceBiome(Biomes.SWAMP, WBBiomes.MANGROVE_SWAMP);
                    List<Climate.ParameterPoint> points = new ParameterUtils.ParameterPointListBuilder().erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1)).depth(Climate.Parameter.point(1.1F)).build();
                    points.forEach(point -> mapper.accept(Pair.of(point, WBBiomes.DEEP_DARK)));
                });
            }
        });
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, WildBackport.MOD_ID, MangroveSwampSurface.makeRules());
    }
}