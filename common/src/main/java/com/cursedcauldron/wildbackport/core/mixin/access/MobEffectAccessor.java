package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MobEffect.class)
public interface MobEffectAccessor {
    @Invoker("<init>")
    static MobEffect createMobEffect(MobEffectCategory mobEffectCategory, int i) {
        throw new UnsupportedOperationException();
    }
}
