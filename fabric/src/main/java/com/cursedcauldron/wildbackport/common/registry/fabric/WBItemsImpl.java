package com.cursedcauldron.wildbackport.common.registry.fabric;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

//<>

public class WBItemsImpl {
    public static Supplier<Item> spawnEgg(Supplier<? extends EntityType<? extends Mob>> mob, int background, int highlight) {
        return () -> new SpawnEggItem(mob.get(), background, highlight, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    }
}