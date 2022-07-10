package com.cursedcauldron.wildbackport.core.api.forge;

import com.cursedcauldron.wildbackport.WildBackport;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

//<>

@Mod.EventBusSubscriber(modid = WildBackport.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RenderRegistryImpl {
    private static final Set<Consumer<EntityRenderersEvent.RegisterRenderers>> RENDERERS = ConcurrentHashMap.newKeySet();
    private static final Set<Consumer<EntityRenderersEvent.RegisterLayerDefinitions>> LAYER_DEFINITIONS = ConcurrentHashMap.newKeySet();

    public static void setBlockRenderType(RenderType type, Block... blocks) {
        for (Block block : blocks) {
            ItemBlockRenderTypes.setRenderLayer(block, type);
        }
    }

    @SubscribeEvent
    public static void event(EntityRenderersEvent.RegisterRenderers event) {
        RENDERERS.forEach(consumer -> consumer.accept(event));
    }

    @SubscribeEvent
    public static void event(EntityRenderersEvent.RegisterLayerDefinitions event) {
        LAYER_DEFINITIONS.forEach(consumer -> consumer.accept(event));
    }

    public static <T extends Entity> void setEntityRender(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> provider) {
        RENDERERS.add(event -> event.registerEntityRenderer(type.get(), provider));
    }

    public static void setLayerDefinition(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
        LAYER_DEFINITIONS.add(event -> event.registerLayerDefinition(layer, definition));
    }

    public static <T extends BlockEntity> void setBlockEntityRender(Supplier<? extends BlockEntityType<? extends T>> type, BlockEntityRendererProvider<T> provider) {
        RENDERERS.add(event -> event.registerBlockEntityRenderer(type.get(), provider));
    }
}