package com.cursedcauldron.wildbackport.client.registry;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.particle.SculkChargeParticleOptions;
import com.cursedcauldron.wildbackport.client.particle.ShriekParticleOptions;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import com.cursedcauldron.wildbackport.core.mixin.access.SimpleParticleTypeAccessor;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Function;
import java.util.function.Supplier;

//<>

public class WBParticleTypes {
    public static final CoreRegistry<ParticleType<?>> PARTICLES = CoreRegistry.create(Registry.PARTICLE_TYPE, WildBackport.MOD_ID);

    public static final Supplier<SimpleParticleType> SCULK_SOUL                         = create("sculk_soul", false);
    public static final Supplier<ParticleType<SculkChargeParticleOptions>> SCULK_CHARGE = create("sculk_charge", SculkChargeParticleOptions.DESERIALIZER, type -> SculkChargeParticleOptions.CODEC, true);
    public static final Supplier<SimpleParticleType> SCULK_CHARGE_POP                   = create("sculk_charge_pop", true);
    public static final Supplier<ParticleType<ShriekParticleOptions>> SHRIEK            = create("shriek", ShriekParticleOptions.DESERIALIZER, type -> ShriekParticleOptions.CODEC, true);
    public static final Supplier<SimpleParticleType> SONIC_BOOM                         = create("sonic_boom", true);

    private static Supplier<SimpleParticleType> create(String key, boolean alwaysShow) {
        return PARTICLES.register(key, () -> SimpleParticleTypeAccessor.createSimpleParticleType(alwaysShow));
    }

    private static <T extends ParticleOptions> Supplier<ParticleType<T>> create(String key, ParticleOptions.Deserializer<T> deserializer, Function<ParticleType<T>, Codec<T>> function, boolean alwaysShow) {
        return PARTICLES.register(key, () -> new ParticleType<>(alwaysShow, deserializer) {
            @Override public Codec<T> codec() {
                return function.apply(this);
            }
        });
    }
}