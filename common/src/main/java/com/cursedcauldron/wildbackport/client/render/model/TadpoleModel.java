package com.cursedcauldron.wildbackport.client.render.model;

import com.cursedcauldron.wildbackport.common.entities.Tadpole;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class TadpoleModel<T extends Tadpole> extends AgeableListModel<T> {
    private final ModelPart root;
    private final ModelPart tail;

    public TadpoleModel(ModelPart root) {
        super(true, 8.0F, 3.35F);
        this.root = root;
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 3.0F), PartPose.offset(0.0F, 22.0F, -3.0F));
        root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, 0.0F, 0.0F, 2.0F, 7.0F), PartPose.offset(0.0F, 22.0F, 0.0F));
        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.root);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.tail);
    }

    @Override
    public void setupAnim(T entity, float angle, float distance, float animationProgress, float yaw, float pitch) {
        float angles = entity.isInWater() ? 1.0F : 1.5F;
        this.tail.yRot = -angles * 0.25F * Mth.sin(0.3F * animationProgress);
    }

}