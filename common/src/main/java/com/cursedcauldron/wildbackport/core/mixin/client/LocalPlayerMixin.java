package com.cursedcauldron.wildbackport.core.mixin.client;

import com.cursedcauldron.wildbackport.common.registry.WBEnchantments;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//<>

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Shadow public Input input;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onInput(Lnet/minecraft/client/player/Input;)V", shift = At.Shift.BEFORE))
    private void wb$aiStep(CallbackInfo ci) {
        LocalPlayer player = LocalPlayer.class.cast(this);
        float swiftnessModifier = (float)EnchantmentHelper.getEnchantmentLevel(WBEnchantments.SWIFT_SNEAK.get(), player) * 0.15F;
        float slownessModifier = Mth.clamp(0.3F + swiftnessModifier, 0.0F, 1.0F);
        if (this.input instanceof KeyboardInput input && !player.isVisuallySwimming()) {
            input.forwardImpulse = input.up == input.down ? 0.0F : (input.up ? 1.0F : -1.0F);
            input.leftImpulse = input.left == input.right ? 0.0F : (input.left ? 1.0F : -1.0F);
            if (player.isCrouching()) {
                input.leftImpulse *= slownessModifier;
                input.forwardImpulse *= slownessModifier;
            }
        }
    }
}