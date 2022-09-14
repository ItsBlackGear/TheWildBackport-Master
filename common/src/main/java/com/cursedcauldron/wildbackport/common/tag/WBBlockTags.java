package com.cursedcauldron.wildbackport.common.tag;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.TagBuilder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class WBBlockTags {
    public static final TagBuilder<Block> BUILDER = TagBuilder.create(Registry.BLOCK, WildBackport.MOD_ID);

    // Mangrove Swamp
    public static final TagKey<Block> CONVERTABLE_TO_MUD = BUILDER.create("convertable_to_mud");
    public static final TagKey<Block> FROG_PREFER_JUMP_TO = BUILDER.create("frog_prefer_jump_to");
    public static final TagKey<Block> FROGS_SPAWNABLE_ON = BUILDER.create("frogs_spawnable_on");
    public static final TagKey<Block> MANGROVE_LOGS_CAN_GROW_THROUGH = BUILDER.create("mangrove_logs_can_grow_through");
    public static final TagKey<Block> MANGROVE_ROOTS_CAN_GROW_THROUGH = BUILDER.create("mangrove_roots_can_grow_through");

    // Deep Dark
    public static final TagKey<Block> SCULK_REPLACEABLE = BUILDER.create("sculk_replaceable");
    public static final TagKey<Block> SCULK_REPLACEABLE_WORLD_GEN = BUILDER.create("sculk_replaceable_world_gen");
    public static final TagKey<Block> ANCIENT_CITY_REPLACEABLE = BUILDER.create("ancient_city_replaceable");

    // Compatibility
    public static final TagKey<Block> MUD = BUILDER.create("mud");
}