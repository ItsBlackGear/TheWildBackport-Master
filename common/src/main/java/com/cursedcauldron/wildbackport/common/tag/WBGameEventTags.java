package com.cursedcauldron.wildbackport.common.tag;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.TagBuilder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.gameevent.GameEvent;

public class WBGameEventTags {
    public static final TagBuilder<GameEvent> TAGS = TagBuilder.create(Registry.GAME_EVENT, WildBackport.MOD_ID);

    public static final TagKey<GameEvent> SHRIEKER_CAN_LISTEN   = TAGS.create("shrieker_can_listen");
    public static final TagKey<GameEvent> WARDEN_CAN_LISTEN     = TAGS.create("warden_can_listen");
    public static final TagKey<GameEvent> ALLAY_CAN_LISTEN      = TAGS.create("allay_can_listen");
}