package com.cursedcauldron.wildbackport.forge;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WildBackport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RemappingSetup {
    @SubscribeEvent
    public static void missingBlockEntityMapping(RegistryEvent.MissingMappings<BlockEntityType<?>> event) {
        for (RegistryEvent.MissingMappings.Mapping<BlockEntityType<?>> mapping : event.getMappings(WildBackport.MOD_ID)) {
            ResourceLocation location = mapping.key;
            if (location != null) if (location.getPath().equals("wb_sign")) mapping.remap(BlockEntityType.SIGN);
        }
    }

    @SubscribeEvent
    public static void missingEntityMapping(RegistryEvent.MissingMappings<EntityType<?>> event) {
        for (RegistryEvent.MissingMappings.Mapping<EntityType<?>> mapping : event.getMappings(WildBackport.MOD_ID)) {
            ResourceLocation location = mapping.key;
            if (location != null) if (location.getPath().equals("wb_boat")) mapping.remap(WBEntityTypes.MANGROVE_BOAT.get());
        }
    }

    @SubscribeEvent
    public static void missingPositionSourceMapping(RegistryEvent.MissingMappings<FoliagePlacerType<?>> event) {
        for (RegistryEvent.MissingMappings.Mapping<FoliagePlacerType<?>> mapping : event.getMappings(WildBackport.MOD_ID)) {
            ResourceLocation location = mapping.key;
            if (location != null) if (location.getPath().equals("water_tree_foliage_placer")) mapping.remap(FoliagePlacerType.RANDOM_SPREAD_FOLIAGE_PLACER);
        }
    }
}