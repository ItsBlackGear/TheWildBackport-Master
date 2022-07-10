package com.cursedcauldron.wildbackport.client.particle;

import com.cursedcauldron.wildbackport.client.registry.WBParticleTypes;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

public record SculkChargeParticleOptions(float roll) implements ParticleOptions {
    public static final Codec<SculkChargeParticleOptions> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(Codec.FLOAT.fieldOf("roll").forGetter(options -> {
            return options.roll;
        })).apply(instance, SculkChargeParticleOptions::new);
    });

    public static final Deserializer<SculkChargeParticleOptions> DESERIALIZER = new Deserializer<>() {
        @Override
        public SculkChargeParticleOptions fromCommand(ParticleType<SculkChargeParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            return new SculkChargeParticleOptions(reader.readFloat());
        }

        @Override
        public SculkChargeParticleOptions fromNetwork(ParticleType<SculkChargeParticleOptions> type, FriendlyByteBuf buf) {
            return new SculkChargeParticleOptions(buf.readFloat());
        }
    };

    @Override
    public ParticleType<?> getType() {
        return WBParticleTypes.SCULK_CHARGE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.roll);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), this.roll);
    }
}