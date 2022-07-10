package com.cursedcauldron.wildbackport.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class ShriekParticle extends TextureSheetParticle {
    private static final Vector3f AXIS = Util.make(new Vector3f(0.5F, 0.5F, 0.5F), Vector3f::normalize);
    private static final Vector3f OFFSET = new Vector3f(-1.0F, -1.0F, 0.0F);
    private int delay;

    protected ShriekParticle(ClientLevel level, double x, double y, double z, int delay) {
        super(level, x, y, z);
        this.quadSize = 0.85F;
        this.delay = delay;
        this.lifetime = 30;
        this.gravity = 0.0F;
        this.xd = 0.0D;
        this.yd = 0.1D;
        this.zd = 0.0D;
    }

    @Override
    public float getQuadSize(float tickDelta) {
        return this.quadSize * Mth.clamp(((float)this.age + tickDelta) / (float)this.lifetime * 0.75F, 0.0F, 1.0F);
    }

    @Override
    public void render(VertexConsumer vertex, Camera camera, float tickDelta) {
        if (this.delay > 0) return;
        this.alpha = 1.0F - Mth.clamp(((float)this.age + tickDelta) / (float)this.lifetime * 0.75F, 0.0F, 1.0F);
        this.render(vertex, camera, tickDelta, quaternion -> {
            quaternion.mul(Vector3f.YP.rotation(0.0F));
            quaternion.mul(Vector3f.XP.rotation(-1.0472F));
        });
        this.render(vertex, camera, tickDelta, quaternion -> {
            quaternion.mul(Vector3f.YP.rotation((float)(-Math.PI)));
            quaternion.mul(Vector3f.XP.rotation(1.0472F));
        });
    }

    private void render(VertexConsumer vertex, Camera camera, float tickDelta, Consumer<Quaternion> consumer) {
        Vec3 pos = camera.getPosition();
        float x = (float)(Mth.lerp(tickDelta, this.xo, this.x) - pos.x());
        float y = (float)(Mth.lerp(tickDelta, this.yo, this.y) - pos.y());
        float z = (float)(Mth.lerp(tickDelta, this.zo, this.z) - pos.z());
        Quaternion quaternion = new Quaternion(AXIS, 0.0F, true);
        consumer.accept(quaternion);
        OFFSET.transform(quaternion);
        Vector3f[] vectors = new Vector3f[] {new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float quadSize = this.getQuadSize(tickDelta);

        for (int i = 0; i < 4; i++) {
            Vector3f vector = vectors[i];
            vector.transform(quaternion);
            vector.mul(quadSize);
            vector.add(x, y, z);
        }

        int light = this.getLightColor(tickDelta);
        this.addVertex(vertex, vectors[0], this.getU1(), this.getV1(), light);
        this.addVertex(vertex, vectors[1], this.getU1(), this.getV0(), light);
        this.addVertex(vertex, vectors[2], this.getU0(), this.getV0(), light);
        this.addVertex(vertex, vectors[3], this.getU0(), this.getV1(), light);

    }

    private void addVertex(VertexConsumer vertex, Vector3f vector, float u, float v, int light) {
        vertex.vertex(vector.x(), vector.y(), vector.z()).uv(u, v).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
    }

    @Override
    protected int getLightColor(float tint) {
        return 240;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        if (this.delay > 0) {
            --this.delay;
            return;
        }
        super.tick();
    }

    public record Provider(SpriteSet spriteSet) implements ParticleProvider<ShriekParticleOptions> {
        @Override
        public Particle createParticle(ShriekParticleOptions options, ClientLevel level, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
            ShriekParticle particle = new ShriekParticle(level, x, y, z, options.delay());
            particle.pickSprite(this.spriteSet);
            particle.setAlpha(1.0F);
            return particle;
        }
    }
}