package com.cursedcauldron.wildbackport.core.mixin.network;

import com.cursedcauldron.wildbackport.common.entities.ChestBoat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "isServerControlledInventory", at = @At("TAIL"), cancellable = true)
    private void wb$handleInventory(CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = this.minecraft.player;
        cir.setReturnValue(player != null && player.isPassenger() && player.getVehicle() instanceof ChestBoat || cir.getReturnValue());
    }
}