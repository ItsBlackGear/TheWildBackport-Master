package com.cursedcauldron.wildbackport.common.tag;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.TagBuilder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class WBEntityTypeTags {
    public static final TagBuilder<EntityType<?>> TAGS = TagBuilder.create(Registry.ENTITY_TYPE, WildBackport.MOD_ID);

    public static final TagKey<EntityType<?>> FROG_FOOD = TAGS.create("frog_food");
}