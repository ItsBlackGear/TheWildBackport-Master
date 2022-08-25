package com.cursedcauldron.wildbackport.core.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

//<>

public record TagBuilder<T>(Registry<T> registry, String modId) {
    public static <T> TagBuilder<T> create(Registry<T> key, String modId) {
        return new TagBuilder<>(key, modId);
    }

    public TagKey<T> create(String key) {
        return TagKey.create(this.registry.key(), new ResourceLocation(this.modId, key));
    }

    /**
     * apparently initializing them helps a lot
     */
    public void bootstrap() {}
}