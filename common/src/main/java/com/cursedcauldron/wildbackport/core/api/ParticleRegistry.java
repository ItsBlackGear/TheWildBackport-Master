package com.cursedcauldron.wildbackport.core.api;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ParticleRegistry {
    @ExpectPlatform
    public static <T extends ParticleOptions, P extends ParticleType<T>> void create(Supplier<P> type, ParticleProvider<T> provider) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends ParticleOptions, P extends ParticleType<T>> void create(Supplier<P> type, Factory<T> provider) {
        throw new AssertionError();
    }

    public interface Factory<T extends ParticleOptions> {
        @NotNull ParticleProvider<T> create(SpriteSet sprites);
    }
}