package com.cursedcauldron.wildbackport.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class SonicBoomParticle extends HugeExplosionParticle {
    public SonicBoomParticle(ClientLevel level, double x, double y, double z, double speed, SpriteSet sprites) {
        super(level, x, y, z, speed, sprites);
        this.lifetime = 16;
        this.quadSize = 1.5F;
        this.setSpriteFromAge(sprites);
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType particle, ClientLevel level, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
            return new SonicBoomParticle(level, x, y, z, xMotion, this.sprites);
        }
    }
}