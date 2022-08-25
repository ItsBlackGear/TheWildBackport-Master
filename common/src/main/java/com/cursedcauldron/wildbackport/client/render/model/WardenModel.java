package com.cursedcauldron.wildbackport.client.render.model;

import com.cursedcauldron.wildbackport.client.animation.WardenAnimations;
import com.cursedcauldron.wildbackport.client.animation.api.Animated;
import com.cursedcauldron.wildbackport.client.animation.api.AnimatedModel;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

import java.util.List;

//<>

public class WardenModel<T extends Warden> extends AnimatedModel<T> {
    private final ModelPart root;
    protected final ModelPart bone;
    protected final ModelPart body;
    protected final ModelPart head;
    protected final ModelPart rightTendril;
    protected final ModelPart leftTendril;
    protected final ModelPart leftLeg;
    protected final ModelPart leftArm;
    protected final ModelPart leftRibcage;
    protected final ModelPart rightArm;
    protected final ModelPart rightLeg;
    protected final ModelPart rightRibcage;
    private final List<ModelPart> tendrils;
    private final List<ModelPart> justBody;
    private final List<ModelPart> headAndLimbs;
    private final List<ModelPart> bodyHeadAndLimbs;

    public WardenModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
        this.bone = root.getChild("bone");
        this.body = this.bone.getChild("body");
        this.head = this.body.getChild("head");
        this.rightLeg = this.bone.getChild("right_leg");
        this.leftLeg = this.bone.getChild("left_leg");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightTendril = this.head.getChild("right_tendril");
        this.leftTendril = this.head.getChild("left_tendril");
        this.rightRibcage = this.body.getChild("right_ribcage");
        this.leftRibcage = this.body.getChild("left_ribcage");
        this.tendrils = ImmutableList.of(this.leftTendril, this.rightTendril);
        this.justBody = ImmutableList.of(this.body);
        this.headAndLimbs = ImmutableList.of(this.head, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg);
        this.bodyHeadAndLimbs = ImmutableList.of(this.body, this.head, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition bone = root.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f));
        PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0f, -13.0f, -4.0f, 18.0f, 21.0f, 11.0f), PartPose.offset(0.0f, -21.0f, 0.0f));
        body.addOrReplaceChild("right_ribcage", CubeListBuilder.create().texOffs(90, 11).addBox(-2.0f, -11.0f, -0.1f, 9.0f, 21.0f, 0.0f), PartPose.offset(-7.0f, -2.0f, -4.0f));
        body.addOrReplaceChild("left_ribcage", CubeListBuilder.create().texOffs(90, 11).mirror().addBox(-7.0f, -11.0f, -0.1f, 9.0f, 21.0f, 0.0f).mirror(false), PartPose.offset(7.0f, -2.0f, -4.0f));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 32).addBox(-8.0f, -16.0f, -5.0f, 16.0f, 16.0f, 10.0f), PartPose.offset(0.0f, -13.0f, 0.0f));
        head.addOrReplaceChild("right_tendril", CubeListBuilder.create().texOffs(52, 32).addBox(-16.0f, -13.0f, 0.0f, 16.0f, 16.0f, 0.0f), PartPose.offset(-8.0f, -12.0f, 0.0f));
        head.addOrReplaceChild("left_tendril", CubeListBuilder.create().texOffs(58, 0).addBox(0.0f, -13.0f, 0.0f, 16.0f, 16.0f, 0.0f), PartPose.offset(8.0f, -12.0f, 0.0f));
        body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(44, 50).addBox(-4.0f, 0.0f, -4.0f, 8.0f, 28.0f, 8.0f), PartPose.offset(-13.0f, -13.0f, 1.0f));
        body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 58).addBox(-4.0f, 0.0f, -4.0f, 8.0f, 28.0f, 8.0f), PartPose.offset(13.0f, -13.0f, 1.0f));
        bone.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(76, 48).addBox(-3.1f, 0.0f, -3.0f, 6.0f, 13.0f, 6.0f), PartPose.offset(-5.9f, -13.0f, 0.0f));
        bone.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(76, 76).addBox(-2.9f, 0.0f, -3.0f, 6.0f, 13.0f, 6.0f), PartPose.offset(5.9f, -13.0f, 0.0f));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float angle, float distance, float animationProgress, float yaw, float pitch) {
        this.root.getAllParts().forEach(Animated::resetToDefault);
        float tickDelta = animationProgress - (float)entity.tickCount;
        this.setHeadAngle(yaw, pitch);
        this.setLimbAngles(angle, distance);
        this.setHeadAndBodyAngles(animationProgress);
        this.setTendrilPitches(entity, animationProgress, tickDelta);
        this.animate(entity.attackingAnimationState, WardenAnimations.ATTACKING, animationProgress);
        this.animate(entity.sonicBoomAnimationState, WardenAnimations.SONIC_BOOM, animationProgress);
        this.animate(entity.diggingAnimationState, WardenAnimations.DIGGING, animationProgress);
        this.animate(entity.emergingAnimationState, WardenAnimations.EMERGING, animationProgress);
        this.animate(entity.roaringAnimationState, WardenAnimations.ROARING, animationProgress);
        this.animate(entity.sniffingAnimationState, WardenAnimations.SNIFFING, animationProgress);
    }

    private void setHeadAngle(float yaw, float pitch) {
        this.head.xRot = pitch * ((float)Math.PI / 180);
        this.head.yRot = yaw * ((float)Math.PI / 180);
    }

    private void setHeadAndBodyAngles(float animationProgress) {
        float angle = animationProgress * 0.1F;
        float cos = Mth.cos(angle);
        float sin = Mth.sin(angle);
        this.head.zRot += 0.06F * cos;
        this.head.xRot += 0.06F * sin;
        this.body.zRot += 0.025F * sin;
        this.body.xRot += 0.025F * cos;
    }

    private void setLimbAngles(float angle, float distance) {
        float roll = Math.min(0.5F, 3.0F * distance);
        float mod = angle * 0.8662F;
        float cos = Mth.cos(mod);
        float sin = Mth.sin(mod);
        float pitch = Math.min(0.35F, roll);
        this.head.zRot += 0.3F * sin * roll;
        this.head.xRot += 1.2F * Mth.cos(mod + 1.5707964F) * pitch;
        this.body.zRot = 0.1F * sin * roll;
        this.body.xRot = 1.0F * cos * pitch;
        this.leftLeg.xRot = 1.0F * cos * roll;
        this.rightLeg.xRot = 1.0F * Mth.cos(mod + (float)Math.PI) * roll;
        this.leftArm.xRot = -(0.8F * cos * roll);
        this.leftArm.zRot = 0.0F;
        this.rightArm.xRot = -(0.8F * sin * roll);
        this.rightArm.zRot = 0.0F;
        this.setArmPivots();
    }

    private void setArmPivots() {
        this.leftArm.yRot = 0.0F;
        this.leftArm.z = 1.0F;
        this.leftArm.x = 13.0F;
        this.leftArm.y = -13.0F;
        this.rightArm.yRot = 0.0F;
        this.rightArm.z = 1.0F;
        this.rightArm.x = -13.0F;
        this.rightArm.y = -13.0F;
    }

    private void setTendrilPitches(T warden, float animationProgress, float tickDelta) {
        float pitch = warden.getTendrilPitch(tickDelta) * (float)(Math.cos((double)animationProgress * 2.25D) * Math.PI * (double)0.1F);
        this.leftTendril.xRot = pitch;
        this.rightTendril.xRot = -pitch;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    public List<ModelPart> getTendrils() {
        return this.tendrils;
    }

    public List<ModelPart> getBody() {
        return this.justBody;
    }

    public List<ModelPart> getHeadAndLimbs() {
        return this.headAndLimbs;
    }

    public List<ModelPart> getBodyHeadAndLimbs() {
        return this.bodyHeadAndLimbs;
    }
}