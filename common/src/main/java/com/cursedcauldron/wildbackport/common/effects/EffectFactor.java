package com.cursedcauldron.wildbackport.common.effects;

import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Optional;
import java.util.function.Supplier;

public interface EffectFactor {
    static EffectFactor of(MobEffect effect) {
        return (EffectFactor)effect;
    }

    MobEffect setFactorCalculationData(Supplier<FactorCalculationData> data);

    Supplier<FactorCalculationData> getFactorCalculationData();

    static Optional<FactorCalculationData> create(MobEffect effect) {
        return Optional.ofNullable(of(effect).getFactorCalculationData().get());
    }

    interface Instance {
        static Instance of(MobEffectInstance instance) {
            return (Instance)instance;
        }

        void setFactorCalculationData(Optional<FactorCalculationData> data);

        Optional<FactorCalculationData> getFactorCalculationData();
    }

    interface Network {
        static Network of(Packet<?> packet) {
            return (Network)packet;
        }

        FactorCalculationData getFactorCalculationData();
    }
}