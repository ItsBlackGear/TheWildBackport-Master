package com.cursedcauldron.wildbackport.client.render;

import com.cursedcauldron.wildbackport.client.render.model.Drawable;
import com.cursedcauldron.wildbackport.client.render.model.WardenModel;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.core.mixin.access.RenderStateShardAccessor;
import com.cursedcauldron.wildbackport.core.mixin.access.RenderTypeAccessor;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.BiFunction;

//<>

@Environment(EnvType.CLIENT)
public class WardenLayerRenderer<T extends Warden, M extends WardenModel<T>> extends RenderLayer<T, M> {
    public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize((location, create) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder().setShaderState(RenderStateShardAccessor.getRENDERTYPE_EYES_SHADER()).setTextureState(new RenderStateShard.TextureStateShard(location, false, false)).setTransparencyState(RenderStateShardAccessor.getTRANSLUCENT_TRANSPARENCY()).setCullState(RenderStateShardAccessor.getNO_CULL()).setWriteMaskState(RenderStateShardAccessor.getCOLOR_WRITE()).setOverlayState(RenderStateShardAccessor.getOVERLAY()).createCompositeState(create);
        return RenderTypeAccessor.callCreate("entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, compositeState);
    });

    private final ResourceLocation texture;
    private final AnimationAngleAdjuster<T> animationAngleAdjuster;
    private final ModelPartVisibility<T, M> modelPartVisibility;

    public WardenLayerRenderer(RenderLayerParent<T, M> ctx, ResourceLocation texture, AnimationAngleAdjuster<T> animationAngleAdjuster, ModelPartVisibility<T, M> modelPartVisibility) {
        super(ctx);
        this.texture = texture;
        this.animationAngleAdjuster = animationAngleAdjuster;
        this.modelPartVisibility = modelPartVisibility;
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource source, int light, T entity, float angle, float distance, float tickDelta, float animationProgress, float yaw, float pitch) {
        if (!entity.isInvisible()) {
            this.updateModelPartVisibility();
            VertexConsumer consumer = source.getBuffer(ENTITY_TRANSLUCENT_EMISSIVE.apply(this.texture, true));
            this.getParentModel().renderToBuffer(stack, consumer, light, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, this.animationAngleAdjuster.apply(entity, tickDelta, animationProgress));
            this.unhideAllModelParts();
        }
    }

    private void updateModelPartVisibility() {
        List<ModelPart> parts = this.modelPartVisibility.getPartsToDraw(this.getParentModel());
        this.getParentModel().root().getAllParts().forEach(part -> Drawable.of(part).setSkipDraw(true));
        parts.forEach(part -> Drawable.of(part).setSkipDraw(false));
    }

    private void unhideAllModelParts() {
        this.getParentModel().root().getAllParts().forEach(part -> Drawable.of(part).setSkipDraw(false));
    }

    public interface AnimationAngleAdjuster<T extends Warden> {
        float apply(T entity, float tickDelta, float animationProgress);
    }

    public interface ModelPartVisibility<T extends Warden, M extends EntityModel<T>> {
        List<ModelPart> getPartsToDraw(M parts);
    }
}