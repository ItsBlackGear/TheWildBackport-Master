package com.cursedcauldron.wildbackport.core.mixin.common;

import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.GoToWantedItem;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GoToWantedItem.class)
public class GoToWantedItemMixin<E extends LivingEntity> {
    @Inject(method = "checkExtraStartConditions", at = @At("TAIL"), cancellable = true)
    private void wb$checkExtraConditions(ServerLevel level, E entity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!this.isInPickupCooldown(entity) && cir.getReturnValue());
    }

    private boolean isInPickupCooldown(E entity) {
        return entity.getBrain().checkMemory(WBMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS.get(), MemoryStatus.VALUE_PRESENT);
    }
}