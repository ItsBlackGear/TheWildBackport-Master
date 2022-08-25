package com.cursedcauldron.wildbackport.common.tag;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.TagBuilder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class WBBlockTags {
    public static final TagBuilder<Block> TAGS = TagBuilder.create(Registry.BLOCK, WildBackport.MOD_ID);

    // Mangrove Swamp
    public static final TagKey<Block> CONVERTABLE_TO_MUD                = TAGS.create("convertable_to_mud");
    public static final TagKey<Block> FROG_PREFER_JUMP_TO               = TAGS.create("frog_prefer_jump_to");
    //TODO
    public static final TagKey<Block> FROGS_SPAWNABLE_ON                = TAGS.create("frogs_spawnable_on");
    public static final TagKey<Block> MANGROVE_LOGS_CAN_GROW_THROUGH    = TAGS.create("mangrove_logs_can_grow_through");
    public static final TagKey<Block> MANGROVE_ROOTS_CAN_GROW_THROUGH   = TAGS.create("mangrove_roots_can_grow_through");

    // Deep Dark
    public static final TagKey<Block> SCULK_REPLACEABLE                 = TAGS.create("sculk_replaceable");
    public static final TagKey<Block> SCULK_REPLACEABLE_WORLD_GEN       = TAGS.create("sculk_replaceable_world_gen");

    // Compatibility
    public static final TagKey<Block> MUD                               = TAGS.create("mud");
}