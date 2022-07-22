package com.cursedcauldron.wildbackport.common.registry;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.effects.EffectFactor;
import com.cursedcauldron.wildbackport.common.effects.FactorCalculationData;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import com.cursedcauldron.wildbackport.core.mixin.access.MobEffectAccessor;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.function.Supplier;

public class WBMobEffects {
    public static final CoreRegistry<MobEffect> EFFECTS = CoreRegistry.create(Registry.MOB_EFFECT, WildBackport.MOD_ID);

    public static final Supplier<MobEffect> DARKNESS = EFFECTS.register("darkness", () -> EffectFactor.of(MobEffectAccessor.createMobEffect(MobEffectCategory.HARMFUL, 2696993)).setFactorCalculationData(() -> new FactorCalculationData(22)));
}