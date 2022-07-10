package com.cursedcauldron.wildbackport.core.mixin.common;

import com.cursedcauldron.wildbackport.common.registry.WBGameEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoteBlock.class)
public class NoteBlockMixin {
    @Inject(method = "playNote", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;blockEvent(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;II)V", shift = At.Shift.AFTER))
    private void wb$playNote(Level level, BlockPos pos, CallbackInfo ci) {
        level.gameEvent(WBGameEvents.NOTE_BLOCK_PLAY.get(), pos);
    }
}