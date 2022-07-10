package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TrapDoorBlock.class)
public interface TrapDoorBlockAccessor {
    @Invoker("<init>")
    static TrapDoorBlock createTrapDoorBlock(BlockBehaviour.Properties properties) {
        throw new UnsupportedOperationException();
    }
}
