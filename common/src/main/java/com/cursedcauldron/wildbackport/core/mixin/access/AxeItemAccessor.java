package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AxeItem.class)
public interface AxeItemAccessor {
    @Accessor
    static Map<Block, Block> getSTRIPPABLES() {
        throw new UnsupportedOperationException();
    }

    @Mutable
    @Accessor
    static void setSTRIPPABLES(Map<Block, Block> STRIPPABLES) {
        throw new UnsupportedOperationException();
    }
}
