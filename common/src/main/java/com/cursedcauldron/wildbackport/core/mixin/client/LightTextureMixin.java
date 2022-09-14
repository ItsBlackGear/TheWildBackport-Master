package com.cursedcauldron.wildbackport.core.mixin.client;

import com.cursedcauldron.wildbackport.common.effects.EffectFactor;
import com.cursedcauldron.wildbackport.common.registry.WBMobEffects;
import com.cursedcauldron.wildbackport.core.api.Environment;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private boolean updateLightTexture;
    @Shadow private float blockLightRedFlicker;
    @Shadow protected abstract float getBrightness(Level level, int i);
    @Shadow @Final private DynamicTexture lightTexture;
    @Shadow @Final private NativeImage lightPixels;
    @Shadow @Final private GameRenderer renderer;

    private LocalPlayer getPlayer() {
        return this.minecraft.player;
    }

    private float getDarknessFactor(float delta) {
        MobEffectInstance instance = this.getPlayer().getEffect(WBMobEffects.DARKNESS.get());
        if (this.getPlayer().hasEffect(WBMobEffects.DARKNESS.get()) && instance != null && EffectFactor.Instance.of(instance).getFactorCalculationData().isPresent()) {
            return EffectFactor.Instance.of(instance).getFactorCalculationData().get().lerp(this.getPlayer(), delta);
        } else {
            return 0.0F;
        }
    }

    private float getDarkness(LivingEntity entity, float factor, float delta) {
        return Math.max(0.0F, Mth.cos(((float)entity.tickCount - delta) * (float) Math.PI * 0.025F) * 0.45F * factor);
    }

    //TODO simplify
    @Inject(method = "updateLightTexture(F)V", at = @At("HEAD"))
    private void updateLight(float delta, CallbackInfo ci) {
        if (Environment.isModLoaded("lod")) return;
        if (this.updateLightTexture) {
            this.updateLightTexture = false;
            this.minecraft.getProfiler().push("lightTex");
            ClientLevel level = this.minecraft.level;
            if (level != null) {
                float skyDarken = level.getSkyDarken(1.0F);
                float skyFlashTime = level.getSkyFlashTime() > 0 ? 1.0F : skyDarken * 0.95F + 0.05F;
                float darknessFactor = this.getDarknessFactor(delta);
                float darkness = this.getDarkness(this.getPlayer(), darknessFactor, delta);
                float waterVision = this.getPlayer().getWaterVision();
                float visionScale = this.getPlayer().hasEffect(MobEffects.NIGHT_VISION) ? GameRenderer.getNightVisionScale(this.getPlayer(), delta) : (waterVision > 0.0F && this.getPlayer().hasEffect(MobEffects.CONDUIT_POWER) ? waterVision : 0.0F);
                Vector3f vec3f = new Vector3f(skyDarken, skyDarken, 1.0F);
                vec3f.lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
                float blockLightFlicker = this.blockLightRedFlicker + 1.5F;
                Vector3f vec3f2 = new Vector3f();

                for (int skyLight = 0; skyLight < 16; ++skyLight) {
                    for (int blockLight = 0; blockLight < 16; ++blockLight) {
                        float skyBrightness = this.getBrightness(level, skyLight) * skyFlashTime;
                        float blockBrightness = this.getBrightness(level, blockLight) * blockLightFlicker;
                        float yLight = blockBrightness * ((blockBrightness * 0.6F + 0.4F) * 0.6F + 0.4F);
                        float xzLight = blockBrightness * (blockBrightness * blockBrightness * 0.6F + 0.4F);
                        vec3f2.set(blockBrightness, yLight, xzLight);
                        boolean forceLightmap = level.effects().forceBrightLightmap();

                        if (forceLightmap) {
                            vec3f2.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
                            vec3f2.clamp(0.0F, 1.0F);
                        } else {
                            Vector3f vec3f3 = vec3f.copy();
                            vec3f3.mul(skyBrightness);
                            vec3f2.add(vec3f3);
                            vec3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                            if (this.renderer.getDarkenWorldAmount(delta) > 0.0f) {
                                float darkenWorldAmount = this.renderer.getDarkenWorldAmount(delta);
                                Vector3f vec3f4 = vec3f2.copy();
                                vec3f4.mul(0.7F, 0.6F, 0.6F);
                                vec3f2.lerp(vec3f4, darkenWorldAmount);
                            }
                        }

                        if (visionScale > 0.0F) {
                            float modifier = Math.max(vec3f2.x(), Math.max(vec3f2.y(), vec3f2.z()));
                            if (modifier < 1.0F) {
                                float scale = 1.0F / modifier;
                                Vector3f vec3f3 = vec3f2.copy();
                                vec3f3.mul(scale);
                                vec3f2.lerp(vec3f3, visionScale);
                            }
                        }

                        if (!forceLightmap) {
                            if (darkness > 0.0F) {
                                vec3f2.add(-darkness, -darkness, -darkness);
                            }

                            vec3f2.clamp(0.0F, 1.0F);
                        }

                        float gamma = (float)this.minecraft.options.gamma;
                        Vector3f vec3f3 = vec3f2.copy();
                        vec3f3.map(this::notGamma);
                        vec3f2.lerp(vec3f3, Math.max(0.0F, gamma - darknessFactor));
                        vec3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                        vec3f2.clamp(0.0F, 1.0F);
                        vec3f2.mul(255.0F);
                        int x = (int)vec3f2.x();
                        int y = (int)vec3f2.y();
                        int z = (int)vec3f2.z();
                        this.lightPixels.setPixelRGBA(blockLight, skyLight, -16777216 | z << 16 | y << 8 | x);
                    }
                }

                this.lightTexture.upload();
                this.minecraft.getProfiler().pop();
            }
        }
    }

    private float notGamma(float f) {
        float g = 1.0f - f;
        return 1.0f - g * g * g * g;
    }
}