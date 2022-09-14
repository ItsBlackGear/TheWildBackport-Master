package com.cursedcauldron.wildbackport.client;

import com.cursedcauldron.wildbackport.client.particle.SculkChargeParticle;
import com.cursedcauldron.wildbackport.client.particle.SculkChargePopParticle;
import com.cursedcauldron.wildbackport.client.particle.SculkSoulParticle;
import com.cursedcauldron.wildbackport.client.particle.ShriekParticle;
import com.cursedcauldron.wildbackport.client.particle.SonicBoomParticle;
import com.cursedcauldron.wildbackport.client.registry.WBParticleTypes;
import com.cursedcauldron.wildbackport.client.render.AllayRenderer;
import com.cursedcauldron.wildbackport.client.render.ChestBoatRenderer;
import com.cursedcauldron.wildbackport.client.render.FrogRenderer;
import com.cursedcauldron.wildbackport.client.render.TadpoleRenderer;
import com.cursedcauldron.wildbackport.client.render.WardenRenderer;
import com.cursedcauldron.wildbackport.client.render.model.AllayModel;
import com.cursedcauldron.wildbackport.client.render.model.ChestBoatModel;
import com.cursedcauldron.wildbackport.client.render.model.FrogModel;
import com.cursedcauldron.wildbackport.client.render.model.TadpoleModel;
import com.cursedcauldron.wildbackport.client.render.model.WardenModel;
import com.cursedcauldron.wildbackport.common.entities.access.Recovery;
import com.cursedcauldron.wildbackport.common.items.CompassItemPropertyFunction;
import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import com.cursedcauldron.wildbackport.common.registry.WBItems;
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntityTypes;
import com.cursedcauldron.wildbackport.core.api.ColorRegistry;
import com.cursedcauldron.wildbackport.core.api.ParticleRegistry;
import com.cursedcauldron.wildbackport.core.api.RenderRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.FoliageColor;

@Environment(EnvType.CLIENT)
public class ClientSetup {
    /**
     * Runs features at initializing
     */
    public static void onClient() {
        // Colors
        ColorRegistry.register((state, getter, pos, tint) -> (getter == null || pos == null) ? FoliageColor.getDefaultColor() : BiomeColors.getAverageFoliageColor(getter, pos), WBBlocks.MANGROVE_LEAVES);
        ColorRegistry.register((stack, tint) -> 9619016, WBBlocks.MANGROVE_LEAVES);

        // Entity Renderers
        RenderRegistry.setLayerDefinition(AllayRenderer.MODEL_LAYER, AllayModel::createBodyLayer);
        RenderRegistry.setEntityRender(WBEntityTypes.ALLAY, AllayRenderer::new);
        RenderRegistry.setLayerDefinition(WardenRenderer.MODEL_LAYER, WardenModel::createBodyLayer);
        RenderRegistry.setEntityRender(WBEntityTypes.WARDEN, WardenRenderer::new);
        RenderRegistry.setLayerDefinition(FrogRenderer.MODEL_LAYER, FrogModel::createBodyLayer);
        RenderRegistry.setEntityRender(WBEntityTypes.FROG, FrogRenderer::new);
        RenderRegistry.setLayerDefinition(TadpoleRenderer.MODEL_LAYER, TadpoleModel::createBodyLayer);
        RenderRegistry.setEntityRender(WBEntityTypes.TADPOLE, TadpoleRenderer::new);
        for (Boat.Type type : Boat.Type.values()) RenderRegistry.setLayerDefinition(ChestBoatModel.createChestBoat(type), () -> ChestBoatModel.createBodyModel(true));
        RenderRegistry.setEntityRender(WBEntityTypes.MANGROVE_BOAT, context -> new ChestBoatRenderer(context, false));
        RenderRegistry.setEntityRender(WBEntityTypes.CHEST_BOAT, context -> new ChestBoatRenderer(context, true));

        // Particle Renderers
        ParticleRegistry.create(WBParticleTypes.SCULK_SOUL, SculkSoulParticle.Provider::new);
        ParticleRegistry.create(WBParticleTypes.SCULK_CHARGE, SculkChargeParticle.Provider::new);
        ParticleRegistry.create(WBParticleTypes.SCULK_CHARGE_POP, SculkChargePopParticle.Provider::new);
        ParticleRegistry.create(WBParticleTypes.SHRIEK, ShriekParticle.Provider::new);
        ParticleRegistry.create(WBParticleTypes.SONIC_BOOM, SonicBoomParticle.Provider::new);
    }

    /**
     * Runs features post bootstrap
     */
    public static void onPostClient() {
        // Block Render Types
        RenderRegistry.setBlockRenderType(RenderType.cutout(),
                WBBlocks.SCULK_VEIN.get(),
                WBBlocks.SCULK_SHRIEKER.get(),
                WBBlocks.FROGSPAWN.get(),
                WBBlocks.MANGROVE_ROOTS.get(),
                WBBlocks.MANGROVE_TRAPDOOR.get(),
                WBBlocks.MANGROVE_PROPAGULE.get(),
                WBBlocks.POTTED_MANGROVE_PROPAGULE.get()
        );
    }
}