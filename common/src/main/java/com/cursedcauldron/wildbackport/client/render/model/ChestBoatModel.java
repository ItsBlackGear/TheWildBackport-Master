package com.cursedcauldron.wildbackport.client.render.model;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.entities.MangroveBoat;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

//<>

public class ChestBoatModel extends ListModel<MangroveBoat> {
    private final ModelPart leftPaddle;
    private final ModelPart rightPaddle;
    private final ModelPart waterPatch;
    private final ImmutableList<ModelPart> parts;

    public ChestBoatModel(ModelPart root, boolean chest) {
        this.leftPaddle = root.getChild("left_paddle");
        this.rightPaddle = root.getChild("right_paddle");
        this.waterPatch = root.getChild("water_patch");
        ImmutableList.Builder<ModelPart> builder = new ImmutableList.Builder<>();
        builder.add(root.getChild("bottom"), root.getChild("back"), root.getChild("front"), root.getChild("right"), root.getChild("left"), this.leftPaddle, this.rightPaddle);
        if (chest) {
            builder.add(root.getChild("chest_bottom"));
            builder.add(root.getChild("chest_lid"));
            builder.add(root.getChild("chest_lock"));
        }

        this.parts = builder.build();
    }

    public static LayerDefinition createBodyModel(boolean chested) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 3.0F, 1.0F, 1.5707964F, 0.0F, 0.0F));
        part.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 19).addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(-15.0F, 4.0F, 4.0F, 0.0F, 4.712389F, 0.0F));
        part.addOrReplaceChild("front", CubeListBuilder.create().texOffs(0, 27).addBox(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(15.0F, 4.0F, 0.0F, 0.0F, 1.5707964F, 0.0F));
        part.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 35).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 4.0F, -9.0F, 0.0F, 3.1415927F, 0.0F));
        part.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 43).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F), PartPose.offset(0.0F, 4.0F, 9.0F));
        if (chested) {
            part.addOrReplaceChild("chest_bottom", CubeListBuilder.create().texOffs(0, 76).addBox(0.0f, 0.0f, 0.0f, 12.0f, 8.0f, 12.0f), PartPose.offsetAndRotation(-2.0f, -5.0f, -6.0f, 0.0f, -1.5707964f, 0.0f));
            part.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(0, 59).addBox(0.0f, 0.0f, 0.0f, 12.0f, 4.0f, 12.0f), PartPose.offsetAndRotation(-2.0f, -9.0f, -6.0f, 0.0f, -1.5707964f, 0.0f));
            part.addOrReplaceChild("chest_lock", CubeListBuilder.create().texOffs(0, 59).addBox(0.0f, 0.0f, 0.0f, 2.0f, 4.0f, 1.0f), PartPose.offsetAndRotation(-1.0f, -6.0f, -1.0f, 0.0f, -1.5707964f, 0.0f));
        }

        part.addOrReplaceChild("left_paddle", CubeListBuilder.create().texOffs(62, 0).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F), PartPose.offsetAndRotation(3.0F, -5.0F, 9.0F, 0.0F, 0.0F, 0.19634955F));
        part.addOrReplaceChild("right_paddle", CubeListBuilder.create().texOffs(62, 20).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F), PartPose.offsetAndRotation(3.0F, -5.0F, -9.0F, 0.0F, 3.1415927F, 0.19634955F));
        part.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(0, 0).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, 1.5707964F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, chested ? 128 : 64);
    }

    @Override
    public void setupAnim(MangroveBoat boat, float angle, float distance, float animationProgress, float yaw, float pitch) {
        animatePaddle(boat, 0, this.leftPaddle, angle);
        animatePaddle(boat, 1, this.rightPaddle, angle);
    }

    public ImmutableList<ModelPart> parts() {
        return this.parts;
    }

    public ModelPart waterPatch() {
        return this.waterPatch;
    }

    private static void animatePaddle(MangroveBoat boat, int sigma, ModelPart part, float angle) {
        float time = boat.getRowingTime(sigma, angle);
        part.xRot = Mth.clampedLerp(-1.0471976F, -0.2617994F, (Mth.sin(-time) + 1.0F) / 2.0F);
        part.yRot = Mth.clampedLerp(-0.7853982F, 0.7853982F, (Mth.sin(-time + 1.0F) + 1.0F) / 2.0F);
        if (sigma == 1) part.yRot = (float)Math.PI - part.yRot;
    }

    public static ModelLayerLocation createChestBoat(Boat.Type type) {
        return new ModelLayerLocation(new ResourceLocation(WildBackport.MOD_ID, "chest_boat/" + type.getName()), "main");
    }
}