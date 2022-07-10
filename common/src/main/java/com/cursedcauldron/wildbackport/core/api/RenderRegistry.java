package com.cursedcauldron.wildbackport.core.api;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

//<>

public class RenderRegistry {
    @ExpectPlatform
    public static void setBlockRenderType(RenderType type, Block... blocks) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntity> void setBlockEntityRender(Supplier<? extends BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T> provider) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Entity> void setEntityRender(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> provider) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void setLayerDefinition(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
        throw new AssertionError();
    }
}