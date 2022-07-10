package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.world.level.block.WoodButtonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WoodButtonBlock.class)
public interface WoodButtonBlockAccessor {
    @Invoker("<init>")
    static WoodButtonBlock createWoodButtonBlock(BlockBehaviour.Properties properties) {
        throw new UnsupportedOperationException();
    }
}
