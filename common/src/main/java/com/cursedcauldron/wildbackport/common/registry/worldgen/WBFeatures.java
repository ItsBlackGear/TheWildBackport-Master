package com.cursedcauldron.wildbackport.common.registry.worldgen;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.worldgen.features.RootedTreeConfig;
import com.cursedcauldron.wildbackport.common.worldgen.features.RootedTreeFeature;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.function.Supplier;

//<>

public class WBFeatures {
    public static final CoreRegistry<Feature<?>> FEATURES = CoreRegistry.create(Registry.FEATURE, WildBackport.MOD_ID);

    public static final Supplier<Feature<RootedTreeConfig>> TREE = FEATURES.register("tree", () -> new RootedTreeFeature(RootedTreeConfig.CODEC));
}