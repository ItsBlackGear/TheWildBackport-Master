package com.cursedcauldron.wildbackport.common.entities.access.api;

import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

//<>

public enum BoatTypes {
    MANGROVE(Blocks.OAK_PLANKS, "mangrove");

    private final Block planks;
    private final String name;

    BoatTypes(Block planks, String name) {
        this.planks = planks;
        this.name = name;
    }

    public Block getPlanks() {
        return this.planks;
    }

    public String getName() {
        return this.name;
    }

    public Boat.Type get() {
        return Boat.Type.byName(this.getName());
    }
}