package com.cursedcauldron.wildbackport.client.render;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.render.model.TadpoleModel;
import com.cursedcauldron.wildbackport.common.entities.Tadpole;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TadpoleRenderer extends MobRenderer<Tadpole, TadpoleModel<Tadpole>> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(WildBackport.MOD_ID, "tadpole"), "main");

    private static final ResourceLocation TEXTURE = new ResourceLocation(WildBackport.MOD_ID, "textures/entity/tadpole/tadpole.png");

    public TadpoleRenderer(EntityRendererProvider.Context context) {
        super(context, new TadpoleModel<>(context.bakeLayer(MODEL_LAYER)), 0.14F);
    }

    @Override
    public ResourceLocation getTextureLocation(Tadpole entity) {
        return TEXTURE;
    }
}
