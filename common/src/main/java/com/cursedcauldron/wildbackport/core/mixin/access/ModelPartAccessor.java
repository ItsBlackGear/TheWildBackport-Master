package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ModelPart.class)
public interface ModelPartAccessor {
    @Accessor
    Map<String, ModelPart> getChildren();
}
