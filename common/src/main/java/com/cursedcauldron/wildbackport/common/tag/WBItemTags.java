package com.cursedcauldron.wildbackport.common.tag;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.TagRegistry;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class WBItemTags {
    public static final TagRegistry<Item> TAGS = TagRegistry.create(Registry.ITEM, WildBackport.MOD_ID);

    public static final TagKey<Item> CHEST_BOATS    = TAGS.create("chest_boats");
    public static final TagKey<Item> MANGROVE_LOGS  = TAGS.create("mangrove_logs");
}