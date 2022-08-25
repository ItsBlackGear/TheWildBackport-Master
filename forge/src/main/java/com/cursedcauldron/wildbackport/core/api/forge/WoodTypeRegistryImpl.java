package com.cursedcauldron.wildbackport.core.api.forge;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.fml.loading.FMLLoader;

public class WoodTypeRegistryImpl {
    public static WoodType create(ResourceLocation location) {
        WoodType woodType = WoodType.register(WoodType.create(location.toString()));
        if (FMLLoader.getDist().isClient()) Sheets.addWoodType(woodType);
        return woodType;
    }
}