package com.cursedcauldron.wildbackport.common.entities.access;

public interface EntityExperience {
    void disableExpDrop();

    boolean isExpDropDisabled();

    int getExpToDrop();
}