package com.cursedcauldron.wildbackport.core.api.fabric;

import com.cursedcauldron.wildbackport.core.api.ParticleRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

public class ParticleRegistryImpl {
    public static <T extends ParticleOptions, P extends ParticleType<T>> void create(Supplier<P> type, ParticleProvider<T> provider) {
        ParticleFactoryRegistry.getInstance().register(type.get(), provider);
    }

    public static <T extends ParticleOptions, P extends ParticleType<T>> void create(Supplier<P> type, ParticleRegistry.Factory<T> factory) {
        ParticleFactoryRegistry.getInstance().register(type.get(), factory::create);
    }
}