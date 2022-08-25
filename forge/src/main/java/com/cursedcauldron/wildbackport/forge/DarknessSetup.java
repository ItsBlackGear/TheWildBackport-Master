package com.cursedcauldron.wildbackport.forge;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.effects.EffectFactor;
import com.cursedcauldron.wildbackport.common.registry.WBMobEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = WildBackport.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DarknessSetup {
    @SubscribeEvent
    public static void darknessFogColor(EntityViewRenderEvent.FogColors event) {
        FogType type = event.getCamera().getFluidInCamera();
        Level level = event.getCamera().getEntity().getLevel();

        if (level.isClientSide() && level instanceof ClientLevel client) {

            double colorModifier = (event.getCamera().getPosition().y - (double)client.getMinBuildHeight()) * client.getLevelData().getClearColorScale();
            if (event.getCamera().getEntity() instanceof LivingEntity living && living.hasEffect(WBMobEffects.DARKNESS.get())) {
                MobEffectInstance effect = living.getEffect(WBMobEffects.DARKNESS.get());

                if (effect != null) {
                    EffectFactor.Instance instance = EffectFactor.Instance.of(effect);
                    if (instance.getFactorCalculationData().isPresent()) {
                        colorModifier = 1.0F - instance.getFactorCalculationData().get().lerp(living, (float)event.getPartialTicks());
                    } else {
                        colorModifier = 0.0D;
                    }
                }
            }

            if (colorModifier < 1.0D && type != FogType.LAVA) {
                if (colorModifier < 0.0D) {
                    colorModifier = 0.0D;
                }

                colorModifier *= colorModifier;
                event.setRed((float)((double)event.getRed() * colorModifier));
                event.setGreen((float)((double)event.getGreen() * colorModifier));
                event.setBlue((float)((double)event.getBlue() * colorModifier));
            }
        }
    }

    @SubscribeEvent
    public static void darknessFog(EntityViewRenderEvent.RenderFogEvent event) {
        FogType type = event.getCamera().getFluidInCamera();

        if (type != FogType.WATER) {
            if (event.getCamera().getEntity() instanceof LivingEntity living && living.hasEffect(WBMobEffects.DARKNESS.get())) {
                MobEffectInstance effect = living.getEffect(WBMobEffects.DARKNESS.get());

                if (effect != null) {
                    EffectFactor.Instance instance = EffectFactor.Instance.of(effect);
                    if (instance.getFactorCalculationData().isPresent()) {
                        float modifier = Mth.lerp(instance.getFactorCalculationData().get().lerp(living, (float)event.getPartialTicks()), event.getFarPlaneDistance(), 15.0F);
                        float start = event.getMode() == FogRenderer.FogMode.FOG_SKY ? 0.0F : modifier * 0.75F;

                        RenderSystem.setShaderFogStart(start);
                        RenderSystem.setShaderFogEnd(modifier);
                    }
                }
            }
        }
    }
}