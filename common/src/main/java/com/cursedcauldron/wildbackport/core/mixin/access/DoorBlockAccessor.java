package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DoorBlock.class)
public interface DoorBlockAccessor {
    @Invoker("<init>")
    static DoorBlock createDoorBlock(BlockBehaviour.Properties properties) {
        throw new UnsupportedOperationException();
    }
}
