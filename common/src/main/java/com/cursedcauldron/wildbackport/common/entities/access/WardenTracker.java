package com.cursedcauldron.wildbackport.common.entities.access;

import com.cursedcauldron.wildbackport.common.entities.warden.WardenSpawnTracker;
import net.minecraft.world.entity.player.Player;

public interface WardenTracker {
    WardenSpawnTracker getWardenSpawnTracker();

    static WardenSpawnTracker getWardenSpawnTracker(Player player) {
        return of(player).getWardenSpawnTracker();
    }

    static WardenTracker of(Player player) {
        return (WardenTracker)player;
    }
}