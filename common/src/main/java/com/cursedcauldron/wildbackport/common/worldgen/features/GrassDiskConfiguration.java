package com.cursedcauldron.wildbackport.common.worldgen.features;

import com.cursedcauldron.wildbackport.common.worldgen.PredicatedStateProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record GrassDiskConfiguration(PredicatedStateProvider stateProvider, BlockPredicate target, IntProvider radius, int halfHeight) implements FeatureConfiguration {
    public static final Codec<GrassDiskConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(PredicatedStateProvider.CODEC.fieldOf("state_provider").forGetter(GrassDiskConfiguration::stateProvider), BlockPredicate.CODEC.fieldOf("target").forGetter(GrassDiskConfiguration::target), IntProvider.codec(0, 8).fieldOf("radius").forGetter(GrassDiskConfiguration::radius), Codec.intRange(0, 4).fieldOf("half_height").forGetter(GrassDiskConfiguration::halfHeight)).apply(instance, GrassDiskConfiguration::new));
}