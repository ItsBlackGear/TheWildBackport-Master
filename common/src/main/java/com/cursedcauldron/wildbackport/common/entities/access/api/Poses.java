package com.cursedcauldron.wildbackport.common.entities.access.api;

import net.minecraft.world.entity.Pose;

//<>

public enum Poses {
    ROARING,
    SNIFFING,
    EMERGING,
    DIGGING,
    CROAKING,
    USING_TONGUE;

    public Pose get() {
        return Pose.valueOf(this.name());
    }
}