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

public record ShriekParticleOptions(int delay) implements ParticleOptions {
    public static final Codec<ShriekParticleOptions> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(Codec.INT.fieldOf("delay").forGetter(options -> {
            return options.delay;
        })).apply(instance, ShriekParticleOptions::new);
    });

    public static final ParticleOptions.Deserializer<ShriekParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public ShriekParticleOptions fromCommand(ParticleType<ShriekParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new ShriekParticleOptions(reader.readInt());
        }

        @Override
        public ShriekParticleOptions fromNetwork(ParticleType<ShriekParticleOptions> type, FriendlyByteBuf buf) {
            return new ShriekParticleOptions(buf.readVarInt());
        }
    };

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeVarInt(this.delay);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %d", Registry.PARTICLE_TYPE.getId(this.getType()), this.delay);
    }

    @Override
    public ParticleType<?> getType() {
        return WBParticleTypes.SHRIEK.get();
    }

    public int getDelay() {
        return this.delay;
    }
}