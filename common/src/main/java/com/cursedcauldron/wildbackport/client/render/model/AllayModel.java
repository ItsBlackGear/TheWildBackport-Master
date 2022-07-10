package com.cursedcauldron.wildbackport.client.render.model;

import com.cursedcauldron.wildbackport.client.animation.api.Animated;
import com.cursedcauldron.wildbackport.common.entities.Allay;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

//<>

public class AllayModel extends HierarchicalModel<Allay> implements ArmedModel {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart right_wing;
    private final ModelPart left_wing;

    public AllayModel(ModelPart part) {
        this.root = part.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.right_arm = this.body.getChild("right_arm");
        this.left_arm = this.body.getChild("left_arm");
        this.right_wing = this.body.getChild("right_wing");
        this.left_wing = this.body.getChild("left_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 23.5F, 0.0F));
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.99F, 0.0F));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 16).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, -4.0F, 0.0F));
        body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(23, 0).addBox(-0.75F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(-1.75F, 0.5F, 0.0F));
        body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(23, 6).addBox(-0.25F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(1.75F, 0.5F, 0.0F));
        body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.0F, 0.65F));
        body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, 0.65F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(Allay entity, float angle, float distance, float animationProgress, float yaw, float pitch) {
        this.root().getAllParts().forEach(Animated::resetPose);
        this.head.xRot = pitch * ((float)Math.PI / 180F);
        this.head.yRot = yaw * ((float)Math.PI / 180F);
        float f = animationProgress * 20.0F * ((float)Math.PI / 180F) + distance;
        float f1 = Mth.cos(f) * (float)Math.PI * 0.15F;
        float f2 = animationProgress - (float)entity.tickCount;
        float f3 = animationProgress * 9.0F * ((float)Math.PI / 180F);
        float f4 = Math.min(distance / 0.3F, 1.0F);
        float f5 = 1.0F - f4;
        float holdingItemAnimation = entity.getHoldingItemAnimationProgress(f2);
        this.right_wing.xRot = 0.43633232F;
        this.right_wing.yRot = -0.61086524F + f1;
        this.left_wing.xRot = 0.43633232F;
        this.left_wing.yRot = 0.61086524F - f1;
        float f7 = f4 * 0.6981317F;
        this.body.xRot = f7;
        float f8 = Mth.lerp(holdingItemAnimation, f7, Mth.lerp(f4, (-(float)Math.PI / 3F), (-(float)Math.PI / 4F)));
        this.root.y += (float)Math.cos(f3) * 0.25F * f5;
        this.right_arm.xRot = f8;
        this.left_arm.xRot = f8;
        float f9 = f5 * (1.0F - holdingItemAnimation);
        float f10 = 0.43633232F - Mth.cos(f3 + ((float)Math.PI * 1.5F)) * (float)Math.PI * 0.075F * f9;
        this.left_arm.zRot = -f10;
        this.right_arm.zRot = f10;
        this.right_arm.yRot = 0.27925268F * holdingItemAnimation;
        this.left_arm.yRot = -0.27925268F * holdingItemAnimation;
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int i, int j, float f, float g, float h, float k) {
        this.root.render(stack, consumer, i, j);
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack stack) {
        this.root.translateAndRotate(stack);
        this.body.translateAndRotate(stack);
        stack.translate(0.0D, -0.09375D, 0.09375D);
        stack.mulPose(Vector3f.XP.rotation(this.right_arm.xRot + 0.43633232F));
        stack.scale(0.7F, 0.7F, 0.7F);
        stack.translate(0.0625D, 0.0D, 0.0D);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}