package com.cursedcauldron.wildbackport.client.render;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.render.model.AllayModel;
import com.cursedcauldron.wildbackport.common.entities.Allay;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class AllayRenderer extends MobRenderer<Allay, AllayModel> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(WildBackport.MOD_ID, "allay"), "main");
    private static final ResourceLocation TEXTURE = new ResourceLocation(WildBackport.MOD_ID, "textures/entity/allay/allay.png");

    public AllayRenderer(EntityRendererProvider.Context context) {
        super(context, new AllayModel(context.bakeLayer(MODEL_LAYER)), 0.4F);
        this.addLayer(new ItemInHandLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Allay entity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(Allay entity, BlockPos pos) {
        return 15;
    }
}