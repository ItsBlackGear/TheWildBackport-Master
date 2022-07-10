package com.cursedcauldron.wildbackport.client.render;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.render.model.FrogModel;
import com.cursedcauldron.wildbackport.common.entities.Frog;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class FrogRenderer extends MobRenderer<Frog, FrogModel<Frog>> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(WildBackport.MOD_ID, "frog"), "main");

    private static final Map<Frog.Variant, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), hashMap -> {
        for (Frog.Variant variant : Frog.Variant.values()) {
            hashMap.put(variant, new ResourceLocation(WildBackport.MOD_ID, String.format("textures/entity/frog/%s_frog.png", variant.getName())));
        }
    });

    public FrogRenderer(EntityRendererProvider.Context context) {
        super(context, new FrogModel<>(context.bakeLayer(MODEL_LAYER)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(Frog frog) {
        return TEXTURES.get(frog.getVariant());
    }
}