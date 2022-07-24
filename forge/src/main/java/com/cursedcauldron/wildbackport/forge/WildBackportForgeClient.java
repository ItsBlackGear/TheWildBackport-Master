package com.cursedcauldron.wildbackport.forge;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.ClientSetup;
import com.cursedcauldron.wildbackport.common.effects.EffectFactor;
import com.cursedcauldron.wildbackport.common.registry.WBMobEffects;
import com.cursedcauldron.wildbackport.core.api.forge.EventBuses;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class WildBackportForgeClient {
    public WildBackportForgeClient() {
        IEventBus bus = EventBuses.getModEventBusOrThrow(WildBackport.MOD_ID);

        bus.addListener(this::setup);
        bus.addListener(this::darknessFog);
    }

    public void setup(FMLClientSetupEvent event) {
        ClientSetup.onClient();
        ClientSetup.onPostClient();
    }

    public void darknessFog(EntityViewRenderEvent.RenderFogEvent event) {
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
