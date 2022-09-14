package com.cursedcauldron.wildbackport.core.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class RegistryBuilder {
    private final String modId;

    public RegistryBuilder(String modId) {
        this.modId = modId;
    }

    public static RegistryBuilder create(String modId) {
        return new RegistryBuilder(modId);
    }

    public <T> SampleRegistry<T> create(String key, Registry.RegistryBootstrap<T> bootstrap) {
        ResourceKey<Registry<T>> resource = ResourceKey.createRegistryKey(new ResourceLocation(this.modId, key));
        return new SampleRegistry<>(resource, Registry.registerSimple(resource, bootstrap));
    }
}