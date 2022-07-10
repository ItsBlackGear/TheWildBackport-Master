package com.cursedcauldron.wildbackport.core.mixin.common;

import com.cursedcauldron.wildbackport.common.entities.Allay;
import net.minecraft.world.entity.PathfinderMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PathfinderMob.class)
public class PathfinderMobMixin {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tickLeash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;enableControlFlag(Lnet/minecraft/world/entity/ai/goal/Goal$Flag;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void wb$tickLeash(CallbackInfo ci) {
        if (PathfinderMob.class.cast(this) instanceof Allay) ci.cancel();
    }
}