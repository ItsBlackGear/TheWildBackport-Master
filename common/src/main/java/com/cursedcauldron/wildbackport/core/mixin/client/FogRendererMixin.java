package com.cursedcauldron.wildbackport.core.mixin.client;

import com.cursedcauldron.wildbackport.common.effects.EffectFactor;
import com.cursedcauldron.wildbackport.common.registry.WBMobEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Shadow private static float fogRed;
    @Shadow private static float fogGreen;
    @Shadow private static float fogBlue;
    private static float fogPartialTicks;

    @Inject(method = "setupColor", at = @At("TAIL"))
    private static void applyDarknessColor(Camera camera, float partialTicks, ClientLevel level, int viewDistance, float skyDarkness, CallbackInfo ci) {
        FogType type = camera.getFluidInCamera();
        fogPartialTicks = partialTicks;

        double colorModifier = (camera.getPosition().y - (double)level.getMinBuildHeight()) * level.getLevelData().getClearColorScale();
        if (camera.getEntity() instanceof LivingEntity living && living.hasEffect(WBMobEffects.DARKNESS.get())) {
            MobEffectInstance effect = living.getEffect(WBMobEffects.DARKNESS.get());

            if (effect != null) {
                EffectFactor.Instance instance = EffectFactor.Instance.of(effect);
                colorModifier = instance.getFactorCalculationData().isPresent() ? 1.0F - instance.getFactorCalculationData().get().lerp(living, partialTicks) : 0.0D;
            }
        }

        if (colorModifier < 1.0D && type != FogType.LAVA) {
            if (colorModifier < 0.0D) {
                colorModifier = 0.0D;
            }

            colorModifier *= colorModifier;
            fogRed = (float)((double)fogRed * colorModifier);
            fogGreen = (float)((double)fogGreen * colorModifier);
            fogBlue = (float)((double)fogBlue * colorModifier);
        }

        if (skyDarkness > 0.0F) {
            fogRed = fogRed * (1.0F - skyDarkness) + fogRed * 0.7F * skyDarkness;
            fogGreen = fogGreen * (1.0F - skyDarkness) + fogGreen * 0.6F * skyDarkness;
            fogBlue = fogBlue * (1.0F - skyDarkness) + fogBlue * 0.6F * skyDarkness;
        }

        RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
    }

    @Inject(method = "setupFog", at = @At("TAIL"), remap = false)
    private static void applyDarknessFog(Camera camera, FogRenderer.FogMode mode, float viewDistance, boolean thickFog, CallbackInfo ci) {
        FogType fogtype = camera.getFluidInCamera();

        if (fogtype != FogType.WATER) {
            if (camera.getEntity() instanceof LivingEntity living && living.hasEffect(WBMobEffects.DARKNESS.get())) {
                MobEffectInstance effect = living.getEffect(WBMobEffects.DARKNESS.get());

                if (effect != null) {
                    EffectFactor.Instance instance = EffectFactor.Instance.of(effect);
                    if (instance.getFactorCalculationData().isPresent()) {
                        float modifier = Mth.lerp(instance.getFactorCalculationData().get().lerp(living, fogPartialTicks), viewDistance, 15.0F);
                        float start = mode == FogRenderer.FogMode.FOG_SKY ? 0.0F : modifier * 0.75F;

                        RenderSystem.setShaderFogStart(start);
                        RenderSystem.setShaderFogEnd(modifier);
                    }
                }
            }
        }
    }
}