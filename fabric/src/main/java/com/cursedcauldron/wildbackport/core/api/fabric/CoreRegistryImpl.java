package com.cursedcauldron.wildbackport.core.api.fabric;

import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import net.minecraft.core.Registry;

public class CoreRegistryImpl {
    public static <T> CoreRegistry<T> create(Registry<T> key, String modId) {
        return new CoreRegistry.DefaultRegistry<>(key, modId);
    }
}