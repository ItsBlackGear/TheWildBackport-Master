package com.cursedcauldron.wildbackport.client.render;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.render.model.WardenModel;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

//<>

@Environment(EnvType.CLIENT)
public class WardenRenderer extends MobRenderer<Warden, WardenModel<Warden>> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(WildBackport.MOD_ID, "warden"), "main");

    private static final ResourceLocation TEXTURE = new ResourceLocation(WildBackport.MOD_ID, "textures/entity/warden/warden.png");
    private static final ResourceLocation BIOLUMINESCENT_LAYER_TEXTURE = new ResourceLocation(WildBackport.MOD_ID, "textures/entity/warden/warden_bioluminescent_layer.png");
    private static final ResourceLocation HEART_TEXTURE = new ResourceLocation(WildBackport.MOD_ID, "textures/entity/warden/warden_heart.png");
    private static final ResourceLocation PULSATING_SPOTS_1_TEXTURE = new ResourceLocation(WildBackport.MOD_ID, "textures/entity/warden/warden_pulsating_spots_1.png");
    private static final ResourceLocation PULSATING_SPOTS_2_TEXTURE = new ResourceLocation(WildBackport.MOD_ID, "textures/entity/warden/warden_pulsating_spots_2.png");

    public WardenRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WardenModel<>(ctx.bakeLayer(MODEL_LAYER)), 0.9F);
        this.addLayer(new WardenLayerRenderer<>(this, BIOLUMINESCENT_LAYER_TEXTURE, (entity, tickDelta, animationProgress) -> 1.0F, WardenModel::getHeadAndLimbs));
        this.addLayer(new WardenLayerRenderer<>(this, PULSATING_SPOTS_1_TEXTURE, (entity, tickDelta, animationProgress) -> Math.max(0.0F, Mth.cos(animationProgress * 0.045F) * 0.25F), WardenModel::getBodyHeadAndLimbs));
        this.addLayer(new WardenLayerRenderer<>(this, PULSATING_SPOTS_2_TEXTURE, (entity, tickDelta, animationProgress) -> Math.max(0.0F, Mth.cos(animationProgress * 0.045F + (float)Math.PI) * 0.25F), WardenModel::getBodyHeadAndLimbs));
        this.addLayer(new WardenLayerRenderer<>(this, TEXTURE, (entity, tickDelta, animationProgress) -> entity.getTendrilPitch(tickDelta), WardenModel::getTendrils));
        this.addLayer(new WardenLayerRenderer<>(this, HEART_TEXTURE, (entity, tickDelta, animationProgress) -> entity.getHeartPitch(tickDelta), WardenModel::getBody));
    }

    @Override
    public ResourceLocation getTextureLocation(Warden warden) {
        return TEXTURE;
    }
}