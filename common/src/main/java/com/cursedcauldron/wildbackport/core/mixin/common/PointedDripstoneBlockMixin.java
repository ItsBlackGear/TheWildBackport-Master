package com.cursedcauldron.wildbackport.core.mixin.common;

import com.cursedcauldron.wildbackport.common.blocks.DrippingFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {
    @Inject(method = "maybeFillCauldron", at = @At("HEAD"), cancellable = true)
    private static void wb$maybeFillCauldron(BlockState state, ServerLevel level, BlockPos pos, float dripChance, CallbackInfo ci) {
        DrippingFluid.fillCauldron(state, level, pos, ci);
    }
}