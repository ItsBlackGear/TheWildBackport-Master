package com.cursedcauldron.wildbackport.core.api.fabric;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
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

public class RenderRegistryImpl {
    public static void setBlockRenderType(RenderType type, Block... blocks) {
        BlockRenderLayerMap.INSTANCE.putBlocks(type, blocks);
    }

    public static <T extends Entity> void setEntityRender(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> provider) {
        EntityRendererRegistry.register(type.get(), provider);
    }

    public static void setLayerDefinition(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
        EntityModelLayerRegistry.registerModelLayer(layer, definition::get);
    }

    public static <T extends BlockEntity> void setBlockEntityRender(Supplier<? extends BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T> provider) {
        BlockEntityRendererRegistry.register(type.get(), provider);
    }
}
