package com.cursedcauldron.wildbackport.client.render;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.render.model.ChestBoatModel;
import com.cursedcauldron.wildbackport.common.entities.MangroveBoat;
import com.cursedcauldron.wildbackport.common.entities.access.api.BoatTypes;
import com.google.common.collect.ImmutableMap;
import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.Map;
import java.util.stream.Stream;

//<>

public class ChestBoatRenderer extends EntityRenderer<MangroveBoat> {
    private final Map<Boat.Type, Pair<ResourceLocation, ChestBoatModel>> boatResources;

    public ChestBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context);
        this.shadowRadius = 0.8F;
        this.boatResources = Stream.of(Boat.Type.values()).collect(ImmutableMap.toImmutableMap(type -> type, type -> Pair.of(getTexture(type, chest), this.createModel(context, type, chest))));
    }

    private ChestBoatModel createModel(EntityRendererProvider.Context context, Boat.Type type, boolean chest) {
        ModelLayerLocation layer = chest ? ChestBoatModel.createChestBoat(type) : ModelLayers.createBoatModelName(type);
        return new ChestBoatModel(context.bakeLayer(layer), chest);
    }

    private static ResourceLocation getTexture(Boat.Type type, boolean chested) {
        if (chested) {
            return new ResourceLocation(WildBackport.MOD_ID, "textures/entity/chest_boat/" + type.getName() + ".png");
        } else {
            return new ResourceLocation(type == BoatTypes.MANGROVE.get() ? WildBackport.MOD_ID: "minecraft", "textures/entity/boat/" + type.getName() + ".png");
        }
    }

    @Override
    public void render(MangroveBoat boat, float yaw, float angle, PoseStack stack, MultiBufferSource buffer, int light) {
        stack.pushPose();
        stack.translate(0.0D, 0.375D, 0.0D);
        stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - yaw));
        float hurtTilt = (float)boat.getHurtTime() - angle;
        float damageTilt = boat.getDamage() - angle;
        if (damageTilt < 0.0F) damageTilt = 0.0F;

        if (hurtTilt > 0.0F) stack.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(hurtTilt) * hurtTilt * damageTilt / 10.0F * (float)boat.getHurtDir()));

        float bubbleTilt = boat.getBubbleAngle(angle);
        if (!Mth.equal(bubbleTilt, 0.0F)) stack.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), boat.getBubbleAngle(angle), true));

        Pair<ResourceLocation, ChestBoatModel> resource = this.boatResources.get(boat.getBoatType());
        ResourceLocation location = resource.first;
        ChestBoatModel model = resource.second;
        stack.scale(-1.0F, -1.0F, 1.0F);
        stack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
        model.setupAnim(boat, angle, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer render = buffer.getBuffer(model.renderType(location));
        model.renderToBuffer(stack, render, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (!boat.isUnderWater()) {
            VertexConsumer waterRender = buffer.getBuffer(RenderType.waterMask());
            model.waterPatch().render(stack, waterRender, light, OverlayTexture.NO_OVERLAY);
        }

        stack.popPose();
        super.render(boat, yaw, angle, stack, buffer, light);
    }

    @Override
    public ResourceLocation getTextureLocation(MangroveBoat boat) {
        return this.boatResources.get(boat.getBoatType()).first;
    }
}