package com.cursedcauldron.wildbackport.common.tag;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.TagRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class WBEntityTypeTags {
    public static final TagRegistry<EntityType<?>> TAGS = TagRegistry.create(Registry.ENTITY_TYPE, WildBackport.MOD_ID);

    public static final TagKey<EntityType<?>> FROG_FOOD = TAGS.create("frog_food");
}