package com.cursedcauldron.wildbackport.common.registry.worldgen;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.mixin.access.RegistryAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

//<>

public class WBRegistries {
    public static final Pair<ResourceKey<Registry<RootPlacerType<?>>>, Registry<RootPlacerType<?>>> ROOT_PLACER_TYPES = create("worldgen/root_placer_type", registry -> RootPlacerType.MANGROVE_ROOT_PLACER.get());

    private static <T> Pair<ResourceKey<Registry<T>>, Registry<T>> create(String key, Registry.RegistryBootstrap<T> bootstrap) {
        ResourceKey<Registry<T>> resource = ResourceKey.createRegistryKey(new ResourceLocation(WildBackport.MOD_ID, key));
        Registry<T> registry = RegistryAccessor.callRegisterSimple(resource, bootstrap);
        return Pair.of(resource, registry);
    }
}