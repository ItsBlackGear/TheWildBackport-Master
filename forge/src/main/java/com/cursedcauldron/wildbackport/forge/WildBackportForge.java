package com.cursedcauldron.wildbackport.forge;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.ClientSetup;
import com.cursedcauldron.wildbackport.common.CommonSetup;
import com.cursedcauldron.wildbackport.common.effects.EffectFactor;
import com.cursedcauldron.wildbackport.common.registry.WBBiomes;
import com.cursedcauldron.wildbackport.common.registry.WBMobEffects;
import com.cursedcauldron.wildbackport.common.worldgen.MangroveSwampSurface;
import com.cursedcauldron.wildbackport.core.api.forge.EventBuses;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

import java.util.List;
import java.util.function.Consumer;

@Mod(WildBackport.MOD_ID)
public class WildBackportForge {
    public WildBackportForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(WildBackport.MOD_ID, bus);
        bus.<FMLCommonSetupEvent>addListener(event -> CommonSetup.onPostCommon());
        bus.<FMLClientSetupEvent>addListener(event -> ClientSetup.onPostClient());

        bus.addListener(this::terrablenderSetup);

        MinecraftForge.EVENT_BUS.addListener(this::darknessFog);
        MinecraftForge.EVENT_BUS.addListener(this::darknessFogColor);

        WildBackport.bootstrap();
        CommonSetup.onCommon();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientSetup::onClient);
    }

    private void terrablenderSetup(final FMLCommonSetupEvent event) {
        Regions.register(new Region(new ResourceLocation(WildBackport.MOD_ID, "overworld"), RegionType.OVERWORLD, 2) {
            @Override public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
                this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
                    builder.replaceBiome(Biomes.SWAMP, WBBiomes.MANGROVE_SWAMP);
                    List<Climate.ParameterPoint> points = new ParameterUtils.ParameterPointListBuilder().erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1)).depth(Climate.Parameter.point(1.1F)).build();
                    points.forEach(point -> mapper.accept(Pair.of(point, WBBiomes.DEEP_DARK)));
                });
            }
        });
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, WildBackport.MOD_ID, MangroveSwampSurface.makeRules());
    }

    private void darknessFogColor(EntityViewRenderEvent.FogColors event) {
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

//        RenderSystem.clearColor(event.getRed(), event.getGreen(), event.getBlue(), 0.0F);
    }

    private void darknessFog(EntityViewRenderEvent.RenderFogEvent event) {
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