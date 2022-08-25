package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WoodType.class)
public interface WoodTypeAccessor {
    @Invoker
    static WoodType callRegister(WoodType woodType) {
        throw new UnsupportedOperationException();
    }
}