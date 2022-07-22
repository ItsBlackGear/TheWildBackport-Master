package com.cursedcauldron.wildbackport.core.mixin.network;

import com.cursedcauldron.wildbackport.common.effects.EffectFactor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow private ClientLevel level;
    @Shadow @Final private Minecraft minecraft;

    //TODO simplify
    @Inject(method = "handleUpdateMobEffect", at = @At("HEAD"), cancellable = true)
    private void wb$updateEffect(ClientboundUpdateMobEffectPacket packet, CallbackInfo ci) {
        PacketUtils.ensureRunningOnSameThread(packet, ClientPacketListener.class.cast(this), this.minecraft);
        Entity entity = this.level.getEntity(packet.getEntityId());
        if (entity instanceof LivingEntity living) {
            MobEffect effect = MobEffect.byId(packet.getEffectId() & 0xFF);
            if (effect != null) {
                MobEffectInstance instance = new MobEffectInstance(effect, packet.getEffectDurationTicks(), packet.getEffectAmplifier(), packet.isEffectAmbient(), packet.isEffectVisible(), packet.effectShowsIcon());
                instance.setNoCounter(packet.isSuperLongDuration());
                EffectFactor.Instance.of(instance).setFactorCalculationData(Optional.ofNullable(EffectFactor.Network.of(packet).getFactorCalculationData()));
                living.forceAddEffect(instance, null);
            }
        }

        ci.cancel();
    }
}