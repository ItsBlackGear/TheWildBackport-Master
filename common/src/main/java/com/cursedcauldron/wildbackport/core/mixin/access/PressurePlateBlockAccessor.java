package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PressurePlateBlock.class)
public interface PressurePlateBlockAccessor {
    @Invoker("<init>")
    static PressurePlateBlock createPressurePlateBlock(PressurePlateBlock.Sensitivity sensitivity, BlockBehaviour.Properties properties) {
        throw new UnsupportedOperationException();
    }
}
