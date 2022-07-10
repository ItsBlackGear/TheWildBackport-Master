package com.cursedcauldron.wildbackport.common.registry.forge;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

//<>

public class WBItemsImpl {
    public static Supplier<Item> spawnEgg(Supplier<? extends EntityType<? extends Mob>> mob, int background, int highlight) {
        return () -> new ForgeSpawnEggItem(mob, background, highlight, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    }
}
