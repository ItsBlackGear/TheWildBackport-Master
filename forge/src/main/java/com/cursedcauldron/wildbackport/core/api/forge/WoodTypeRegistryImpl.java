package com.cursedcauldron.wildbackport.core.api.forge;

import com.cursedcauldron.wildbackport.core.mixin.access.WoodTypeAccessor;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.fml.loading.FMLLoader;

public class WoodTypeRegistryImpl {
    public static WoodType create(ResourceLocation location) {
        WoodType woodType = WoodTypeAccessor.callRegister(WoodTypeAccessor.createWoodType(location.toString()));
        if (FMLLoader.getDist().isClient()) Sheets.addWoodType(woodType);
        return woodType;
    }
}