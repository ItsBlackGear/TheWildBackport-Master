package com.cursedcauldron.wildbackport.common.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record SculkPatchConfiguration(int chargeCount, int amountPerCharge, int spreadAttempts, int growthRounds, int spreadRounds, IntProvider extraRareGrowths, float catalystChance) implements FeatureConfiguration {
    public static final Codec<SculkPatchConfiguration> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(Codec.intRange(1, 32).fieldOf("charge_count").forGetter(config -> {
            return config.chargeCount;
        }), Codec.intRange(1, 500).fieldOf("amount_per_charge").forGetter(config -> {
            return config.amountPerCharge;
        }), Codec.intRange(1, 64).fieldOf("spread_attempts").forGetter(config -> {
            return config.spreadAttempts;
        }), Codec.intRange(0, 8).fieldOf("growth_rounds").forGetter(config -> {
            return config.growthRounds;
        }), Codec.intRange(0, 8).fieldOf("spread_rounds").forGetter(config -> {
            return config.spreadRounds;
        }), IntProvider.CODEC.fieldOf("extra_rare_growths").forGetter(config -> {
            return config.extraRareGrowths;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("catalyst_chance").forGetter(config -> {
            return config.catalystChance;
        })).apply(instance, SculkPatchConfiguration::new);
    });
}