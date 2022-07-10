package com.cursedcauldron.wildbackport.common.registry;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.function.Supplier;

//<>

public class WBGameEvents {
    public static final CoreRegistry<GameEvent> EVENTS = CoreRegistry.create(Registry.GAME_EVENT, WildBackport.MOD_ID);

    public static final Supplier<GameEvent> NOTE_BLOCK_PLAY                 = create("note_block_play");
    public static final Supplier<GameEvent> SCULK_SENSOR_TENDRILS_CLICKING  = create("sculk_sensor_tendrils_clicking");
    public static final Supplier<GameEvent> ENTITY_DIE                      = create("entity_die");
    public static final Supplier<GameEvent> SHRIEK                          = create("shriek", 32);
    public static final Supplier<GameEvent> INSTRUMENT_PLAY                 = create("instrument_play");

    private static Supplier<GameEvent> create(String key) {
        return create(key, 16);
    }

    private static Supplier<GameEvent> create(String key, int maxDistance) {
        return EVENTS.register(key, () -> new GameEvent(key, maxDistance));
    }
}