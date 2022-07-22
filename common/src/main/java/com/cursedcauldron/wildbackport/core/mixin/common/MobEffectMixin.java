package com.cursedcauldron.wildbackport.core.mixin.common;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.effects.EffectFactor;
import com.cursedcauldron.wildbackport.common.effects.FactorCalculationData;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(MobEffect.class)
public class MobEffectMixin implements EffectFactor {
    private Supplier<FactorCalculationData> factorCalculationData = () -> null;

    @Override
    public MobEffect setFactorCalculationData(Supplier<FactorCalculationData> data) {
        this.factorCalculationData = data;
        return MobEffect.class.cast(this);
    }

    @Override
    public Supplier<FactorCalculationData> getFactorCalculationData() {
        return this.factorCalculationData;
    }

    @Mixin(MobEffectInstance.class)
    public static class MobEffectInstanceMixin implements EffectFactor.Instance {
        @Shadow @Final private MobEffect effect;
        @Shadow private int duration;
        private Optional<FactorCalculationData> factorCalculationData;

        @Inject(method = "<init>(Lnet/minecraft/world/effect/MobEffect;IIZZZLnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("TAIL"))
        private void wb$create(MobEffect effect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, MobEffectInstance hiddenEffect, CallbackInfo ci) {
            this.setFactorCalculationData(EffectFactor.create(effect));
        }

        @Inject(method = "<init>(Lnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("TAIL"))
        private void wb$create(MobEffectInstance instance, CallbackInfo ci) {
            this.setFactorCalculationData(EffectFactor.create(this.effect));
        }

        @Override
        public void setFactorCalculationData(Optional<FactorCalculationData> data) {
            this.factorCalculationData = data;
        }

        @Override
        public Optional<FactorCalculationData> getFactorCalculationData() {
            return this.factorCalculationData;
        }

        @Inject(method = "update", at = @At("HEAD"), cancellable = true)
        private void wb$update(MobEffectInstance instance, CallbackInfoReturnable<Boolean> cir) {
            int i = instance.getDuration();
//            int i = this.duration;
            if (i != this.duration) {
                this.factorCalculationData.ifPresent(data -> data.effectChangedTimestamp += this.duration - i);
                cir.setReturnValue(true);
            }
        }

        @Inject(method = "tick", at = @At("HEAD"))
        private void wb$tick(LivingEntity entity, Runnable runnable, CallbackInfoReturnable<Boolean> cir) {
            this.factorCalculationData.ifPresent(data -> data.update(MobEffectInstance.class.cast(this)));
        }

        @Inject(method = "writeDetailsTo", at = @At("TAIL"))
        private void wb$write(CompoundTag tag, CallbackInfo ci) {
            this.factorCalculationData.flatMap(instance -> FactorCalculationData.CODEC.encodeStart(NbtOps.INSTANCE, instance).resultOrPartial(WildBackport.LOGGER::error)).ifPresent(data -> tag.put("FactorCalculationData", data));
        }

        @Inject(method = "loadSpecifiedEffect", at = @At("TAIL"))
        private static void wb$load(MobEffect effect, CompoundTag tag, CallbackInfoReturnable<MobEffectInstance> cir) {
            Optional<FactorCalculationData> data = tag.contains("FactorCalculationData", 10) ? FactorCalculationData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, tag.getCompound("FactorCalculationData"))).resultOrPartial(WildBackport.LOGGER::error) : Optional.empty();
            EffectFactor.Instance.of(cir.getReturnValue()).setFactorCalculationData(data);
        }
    }
}