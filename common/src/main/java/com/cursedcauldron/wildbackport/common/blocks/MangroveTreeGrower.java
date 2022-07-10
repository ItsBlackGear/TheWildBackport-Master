package com.cursedcauldron.wildbackport.common.blocks;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class MangroveTreeGrower extends AbstractTreeGrower {
    private final float tallMangroveChance;

    public MangroveTreeGrower(float tallMangroveChance) {
        this.tallMangroveChance = tallMangroveChance;
    }

    @Override @Nullable
    protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random random, boolean bl) {
        return null;
    }
}