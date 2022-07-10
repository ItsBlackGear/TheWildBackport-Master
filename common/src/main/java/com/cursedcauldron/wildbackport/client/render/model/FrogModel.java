package com.cursedcauldron.wildbackport.client.render.model;

import com.cursedcauldron.wildbackport.client.animation.FrogAnimations;
import com.cursedcauldron.wildbackport.client.animation.api.Animated;
import com.cursedcauldron.wildbackport.client.animation.api.AnimatedModel;
import com.cursedcauldron.wildbackport.common.entities.Frog;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class FrogModel<T extends Frog> extends AnimatedModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart eyes;
    private final ModelPart tongue;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart croakingBody;

    public FrogModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
        this.eyes = this.head.getChild("eyes");
        this.tongue = this.body.getChild("tongue");
        this.leftArm = this.body.getChild("left_arm");
        this.rightArm = this.body.getChild("right_arm");
        this.leftLeg = this.root.getChild("left_leg");
        this.rightLeg = this.root.getChild("right_leg");
        this.croakingBody = this.body.getChild("croaking_body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition bone = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f));
        PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(3, 1).addBox(-3.5f, -2.0f, -8.0f, 7.0f, 3.0f, 9.0f).texOffs(23, 22).addBox(-3.5f, -1.0f, -8.0f, 7.0f, 0.0f, 9.0f), PartPose.offset(0.0f, -2.0f, 4.0f));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(23, 13).addBox(-3.5f, -1.0f, -7.0f, 7.0f, 0.0f, 9.0f).texOffs(0, 13).addBox(-3.5f, -2.0f, -7.0f, 7.0f, 3.0f, 9.0f), PartPose.offset(0.0f, -2.0f, -1.0f));
        PartDefinition eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create(), PartPose.offset(-0.5f, 0.0f, 2.0f));
        eyes.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, -1.0f, -1.5f, 3.0f, 2.0f, 3.0f), PartPose.offset(-1.5f, -3.0f, -6.5f));
        eyes.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(0, 5).addBox(-1.5f, -1.0f, -1.5f, 3.0f, 2.0f, 3.0f), PartPose.offset(2.5f, -3.0f, -6.5f));
        body.addOrReplaceChild("croaking_body", CubeListBuilder.create().texOffs(26, 5).addBox(-3.5f, -0.1f, -2.9f, 7.0f, 2.0f, 3.0f, new CubeDeformation(-0.1f)), PartPose.offset(0.0f, -1.0f, -5.0f));
        body.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(17, 13).addBox(-2.0f, 0.0f, -7.1f, 4.0f, 0.0f, 7.0f), PartPose.offset(0.0f, -1.01f, 1.0f));
        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 3.0f), PartPose.offset(4.0f, -1.0f, -6.5f));
        left_arm.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(18, 40).addBox(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), PartPose.offset(0.0f, 3.0f, -1.0f));
        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 38).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 3.0f), PartPose.offset(-4.0f, -1.0f, -6.5f));
        right_arm.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(2, 40).addBox(-4.0f, 0.01f, -5.0f, 8.0f, 0.0f, 8.0f), PartPose.offset(0.0f, 3.0f, 0.0f));
        PartDefinition left_leg = bone.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(14, 25).addBox(-1.0f, 0.0f, -2.0f, 3.0f, 3.0f, 4.0f), PartPose.offset(3.5f, -3.0f, 4.0f));
        left_leg.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(2, 32).addBox(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), PartPose.offset(2.0f, 3.0f, 0.0f));
        PartDefinition right_leg = bone.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0f, 0.0f, -2.0f, 3.0f, 3.0f, 4.0f), PartPose.offset(-3.5f, -3.0f, 4.0f));
        right_leg.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(18, 32).addBox(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), PartPose.offset(-2.0f, 3.0f, 0.0f));
        return LayerDefinition.create(mesh, 48, 48);
    }

    @Override
    public void setupAnim(T entity, float angle, float distance, float animationProgress, float yaw, float pitch) {
        this.root.getAllParts().forEach(Animated::resetPose);
        float speedMultiplier = Math.min((float)entity.getDeltaMovement().lengthSqr() * 200.0F, 8.0F);
        this.animate(entity.longJumpingAnimationState, FrogAnimations.LONG_JUMPING, animationProgress);
        this.animate(entity.croakingAnimationState, FrogAnimations.CROAKING, animationProgress);
        this.animate(entity.usingTongueAnimationState, FrogAnimations.USING_TONGUE, animationProgress);
        this.animate(entity.walkingAnimationState, FrogAnimations.WALKING, animationProgress, speedMultiplier);
        this.animate(entity.swimmingAnimationState, FrogAnimations.SWIMMING, animationProgress);
        this.animate(entity.idlingInWaterAnimationState, FrogAnimations.IDLING_IN_WATER, animationProgress);
        this.croakingBody.visible = entity.croakingAnimationState.isRunning();
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
