package com.cursedcauldron.wildbackport.common.tag;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.TagRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

public class WBItemTags {
    public static final TagRegistry<Item> TAGS = TagRegistry.create(Registry.ITEM, WildBackport.MOD_ID);
}