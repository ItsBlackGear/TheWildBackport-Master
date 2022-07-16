package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Registry.class)
public interface RegistryAccessor {
    @Invoker
    static <T> Registry<T> callRegisterSimple(ResourceKey<? extends Registry<T>> resourceKey, Registry.RegistryBootstrap<T> registryBootstrap) {
        throw new UnsupportedOperationException();
    }
}
