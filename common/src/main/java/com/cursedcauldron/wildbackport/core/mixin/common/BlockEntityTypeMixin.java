package com.cursedcauldron.wildbackport.core.mixin.common;

import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void wb$isValid(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (BlockEntityType.SIGN.equals(this) && (state.getBlock() instanceof SignBlock || state.getBlock() instanceof WallSignBlock)) {
            cir.setReturnValue(true);
        }
    }
}