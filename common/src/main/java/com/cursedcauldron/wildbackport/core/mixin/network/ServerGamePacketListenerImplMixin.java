package com.cursedcauldron.wildbackport.core.mixin.network;

import com.cursedcauldron.wildbackport.common.entities.ChestBoat;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "handlePlayerCommand", at = @At("HEAD"))
    private void wb$handleInventory(ServerboundPlayerCommandPacket packet, CallbackInfo ci) {
        if (packet.getAction() == ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY) {
            if (this.player.getVehicle() instanceof ChestBoat boat) {
                boat.openInventory(this.player);
            }
        }
    }
}