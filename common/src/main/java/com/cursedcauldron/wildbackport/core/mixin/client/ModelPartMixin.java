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

    private float scaleX = 1.0F;
    private float scaleY = 1.0F;
    private float scaleZ = 1.0F;
    private PartPose defaultPose = PartPose.ZERO;
    private boolean skipDraw;

    @Override
    public PartPose resetToDefault() {
        return this.defaultPose;
    }

    @Override
    public void setDefault(PartPose pose) {
        this.defaultPose = pose;
    }

    @Inject(method = "loadPose", at = @At("TAIL"))
    private void wb$load(PartPose pose, CallbackInfo ci) {
        this.scaleX = 1.0F;
        this.scaleY = 1.0F;
        this.scaleZ = 1.0F;
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void wb$copy(ModelPart part, CallbackInfo ci) {
        this.scaleX(Animated.of(part).scaleX());
        this.scaleY(Animated.of(part).scaleY());
        this.scaleZ(Animated.of(part).scaleZ());
    }

    @Inject(method = "translateAndRotate", at = @At("TAIL"))
    private void wb$moveAndScale(PoseStack stack, CallbackInfo ci) {
        if (this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F) {
            stack.scale(this.scaleX, this.scaleY, this.scaleZ);
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", at = @At("HEAD"), cancellable = true)
    private void wb$render(PoseStack pose, VertexConsumer consumer, int light, int delta, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (this.skipDraw()) {
            if (this.visible) {
                if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                    pose.pushPose();
                    this.translateAndRotate(pose);

                    for (ModelPart part : this.children.values()) part.render(pose, consumer, light, delta, red, green, blue, alpha);

                    pose.popPose();
                }
            }

            ci.cancel();
        }
    }

    @Override
    public float scaleX() {
        return this.scaleX;
    }

    @Override
    public void scaleX(float x) {
        this.scaleX = x;
    }

    @Override
    public void scaleXTo(float x) {
        this.scaleX += x;
    }

    @Override
    public float scaleY() {
        return this.scaleY;
    }

    @Override
    public void scaleY(float y) {
        this.scaleY = y;
    }

    @Override
    public void scaleYTo(float y) {
        this.scaleY += y;
    }

    @Override
    public float scaleZ() {
        return this.scaleZ;
    }

    @Override
    public void scaleZ(float z) {
        this.scaleZ = z;
    }

    @Override
    public void scaleZTo(float z) {
        this.scaleZ += z;
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