package com.cursedcauldron.wildbackport.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

@Environment(EnvType.CLIENT)
public class SculkChargeParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public SculkChargeParticle(ClientLevel level, double x, double y, double z, double xMotion, double yMotion, double zMotion, SpriteSet sprites) {
        super(level, x, y, z, xMotion, yMotion, zMotion);
        this.sprites = sprites;
        this.friction = 0.96F;
        this.scale(1.5F);
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

    public record Provider(SpriteSet sprites) implements ParticleProvider<SculkChargeParticleOptions> {
        @Override
        public Particle createParticle(SculkChargeParticleOptions options, ClientLevel level, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
            SculkChargeParticle particle = new SculkChargeParticle(level, x, y, z, xMotion, yMotion, zMotion, this.sprites);
            particle.setAlpha(1.0F);
            particle.setParticleSpeed(xMotion, yMotion, zMotion);
            particle.oRoll = particle.roll = options.roll();
            particle.setLifetime(level.getRandom().nextInt(12) + 8);
            return particle;
        }
    }
}