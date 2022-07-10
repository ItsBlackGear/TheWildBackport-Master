package com.cursedcauldron.wildbackport.core.api.fabric;

import com.cursedcauldron.wildbackport.core.mixin.access.SheetsAccessor;
import com.cursedcauldron.wildbackport.core.mixin.access.WoodTypeAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

public class WoodTypeRegistryImpl {
    public static WoodType create(ResourceLocation location) {
        WoodType woodType = WoodTypeAccessor.callRegister(new WoodTypeImpl(location));
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) Sheets.SIGN_MATERIALS.put(woodType, SheetsAccessor.callCreateSignMaterial(woodType));
        return woodType;
    }

    public static class WoodTypeImpl extends WoodType {
        private final ResourceLocation location;

        private WoodTypeImpl(ResourceLocation location) {
            super(location.getPath());
            this.location = location;
        }

        public ResourceLocation getLocation() {
            return this.location;
        }
    }
}