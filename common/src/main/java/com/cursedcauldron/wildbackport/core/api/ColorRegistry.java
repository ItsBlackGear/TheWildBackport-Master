package com.cursedcauldron.wildbackport.core.api;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

//<>

public class ColorRegistry {
    @SafeVarargs
    @ExpectPlatform
    public static void register(ItemColor itemColor, Supplier<? extends ItemLike>... items) {
        throw new AssertionError();
    }

    @SafeVarargs
    @ExpectPlatform
    public static void register(BlockColor blockColor, Supplier<? extends Block>... blocks) {
        throw new AssertionError();
    }
}