package com.cursedcauldron.wildbackport.core.mixin.fabric.client;

import com.cursedcauldron.wildbackport.core.api.fabric.WoodTypeRegistryImpl;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheets.class)
public class SheetsMixin {
    @Inject(method = "createSignMaterial", at = @At("HEAD"), cancellable = true)
    private static void wb$createSign(WoodType woodType, CallbackInfoReturnable<Material> cir) {
        if (woodType instanceof WoodTypeRegistryImpl.WoodTypeImpl impl) {
            ResourceLocation location = impl.getLocation();
            cir.setReturnValue(new Material(Sheets.SIGN_SHEET, new ResourceLocation(location.getNamespace(), "entity/signs/" + location.getPath())));
        }
    }
}