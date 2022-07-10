package com.cursedcauldron.wildbackport.core.mixin.client;

import com.cursedcauldron.wildbackport.client.animation.api.Animated;
import com.cursedcauldron.wildbackport.client.render.model.Drawable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

//<>

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements Animated, Drawable {
    @Shadow
    public boolean visible;
    @Shadow @Final
    private List<ModelPart.Cube> cubes;
    @Shadow @Final private Map<String, ModelPart> children;
    @Shadow public abstract void translateAndRotate(PoseStack pose);

    private float xScale = 1.0F;
    private float yScale = 1.0F;
    private float zScale = 1.0F;
    private PartPose defaultPose = PartPose.ZERO;
    private boolean skipDraw;

    @Override
    public PartPose getDefaultPose() {
        return this.defaultPose;
    }

    @Override
    public void setDefaultPose(PartPose pose) {
        this.defaultPose = pose;
    }

    @Inject(method = "loadPose", at = @At("TAIL"))
    private void wb$load(PartPose pose, CallbackInfo ci) {
        this.xScale = 1.0F;
        this.yScale = 1.0F;
        this.zScale = 1.0F;
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void wb$copy(ModelPart part, CallbackInfo ci) {
        this.setXScale(((Animated)(Object)part).xScale());
        this.setYScale(((Animated)(Object)part).yScale());
        this.setZScale(((Animated)(Object)part).zScale());
    }

    @Inject(method = "translateAndRotate", at = @At("TAIL"))
    private void wb$moveAndScale(PoseStack stack, CallbackInfo ci) {
        if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
            stack.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", at = @At("HEAD"), cancellable = true)
    private void wb$render(PoseStack pose, VertexConsumer consumer, int light, int delta, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (this.skipDraw()) {
            if (this.visible) {
                if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                    pose.pushPose();
                    this.translateAndRotate(pose);

                    for (ModelPart part : this.children.values()) {
                        part.render(pose, consumer, light, delta, red, green, blue, alpha);
                    }

                    pose.popPose();
                }
            }
            ci.cancel();
        }
    }

    @Override
    public float xScale() {
        return this.xScale;
    }

    @Override
    public void setXScale(float x) {
        this.xScale = x;
    }

    @Override
    public void increaseXScale(float x) {
        this.xScale += x;
    }

    @Override
    public float yScale() {
        return this.yScale;
    }

    @Override
    public void setYScale(float y) {
        this.yScale = y;
    }

    @Override
    public void increaseYScale(float y) {
        this.yScale += y;
    }

    @Override
    public float zScale() {
        return this.zScale;
    }

    @Override
    public void setZScale(float z) {
        this.zScale = z;
    }

    @Override
    public void increaseZScale(float z) {
        this.zScale += z;
    }

    @Override
    public boolean skipDraw() {
        return this.skipDraw;
    }

    @Override
    public void setSkipDraw(boolean skipDraw) {
        this.skipDraw = skipDraw;
    }
}