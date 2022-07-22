package com.cursedcauldron.wildbackport.core.mixin.network;

import com.cursedcauldron.wildbackport.common.effects.EffectFactor;
import com.cursedcauldron.wildbackport.common.effects.FactorCalculationData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Mixin(ClientboundUpdateMobEffectPacket.class)
public class ClientboundUpdateMobEffectPacketMixin implements EffectFactor.Network {
    private FactorCalculationData factorCalculationData;

    @Inject(method = "<init>(ILnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("TAIL"))
    private void create(int id, MobEffectInstance instance, CallbackInfo ci) {
        this.factorCalculationData = EffectFactor.Instance.of(instance).getFactorCalculationData().orElse(null);
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
    private void create(FriendlyByteBuf buf, CallbackInfo ci) {
        this.factorCalculationData = this.readNullable(buf, buffer -> buffer.readWithCodec(FactorCalculationData.CODEC));
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void writeData(FriendlyByteBuf buf, CallbackInfo ci) {
        this.writeNullable(buf, this.factorCalculationData, (buffer, data) -> buffer.writeWithCodec(FactorCalculationData.CODEC, data));
    }

    @Override
    public FactorCalculationData getFactorCalculationData() {
        return this.factorCalculationData;
    }

    private <T> T readNullable(FriendlyByteBuf buf, Function<FriendlyByteBuf, T> consumer) {
        return buf.readBoolean() ? consumer.apply(buf) : null;
    }

    private <T> void writeNullable(FriendlyByteBuf buf, @Nullable T entry, BiConsumer<FriendlyByteBuf, T> consumer) {
        if (entry != null) {
            buf.writeBoolean(true);
            consumer.accept(buf, entry);
        } else {
            buf.writeBoolean(false);
        }
    }
}