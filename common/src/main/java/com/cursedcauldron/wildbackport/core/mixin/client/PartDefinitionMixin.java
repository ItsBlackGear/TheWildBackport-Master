package com.cursedcauldron.wildbackport.core.mixin.client;

import com.cursedcauldron.wildbackport.client.animation.api.Animated;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PartDefinition.class)
public class PartDefinitionMixin {
    @Shadow @Final private PartPose partPose;

    @Inject(method = "bake", at = @At(value = "RETURN"), cancellable = true)
    private void wb$bake(int i, int j, CallbackInfoReturnable<ModelPart> cir) {
        ((Animated)(Object)cir.getReturnValue()).setDefaultPose(this.partPose);
        cir.setReturnValue(cir.getReturnValue());
    }
}