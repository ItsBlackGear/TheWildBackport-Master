package com.cursedcauldron.wildbackport.core.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public record SampleRegistry<T>(ResourceKey<Registry<T>> key, Registry<T> registry) {}