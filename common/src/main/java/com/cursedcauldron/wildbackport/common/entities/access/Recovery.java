package com.cursedcauldron.wildbackport.common.entities.access;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface Recovery {
    static Recovery of(Player player) {
        return (Recovery)player;
    }

    Optional<GlobalPos> getLastDeathLocation();

    void setLastDeathLocation(Optional<GlobalPos> location);
}