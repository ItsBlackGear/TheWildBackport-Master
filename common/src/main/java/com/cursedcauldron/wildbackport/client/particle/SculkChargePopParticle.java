package com.cursedcauldron.wildbackport.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

//<>

@Environment(EnvType.CLIENT)
public class SculkChargePopParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public SculkChargePopParticle(ClientLevel level, double x, double y, double z, double xMotion, double yMotion, double zMotion, SpriteSet sprites) {
        super(level, x, y, z, xMotion, yMotion, zMotion);
        this.sprites = sprites;
        this.friction = 0.96F;
        this.scale(1.0F);
        this.hasPhysics = false;
        this.setSpriteFromAge(sprites);
    }

    @Override
    protected int getLightColor(float delta) {
        return 240;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
            SculkChargePopParticle particle = new SculkChargePopParticle(level, x, y, z, xMotion, yMotion, zMotion, this.sprites);
            particle.setAlpha(1.0F);
            particle.setParticleSpeed(xMotion, yMotion, zMotion);
            particle.setLifetime(level.getRandom().nextInt(4) + 6);
            return particle;
        }
    }
}