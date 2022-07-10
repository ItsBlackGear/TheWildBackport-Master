package com.cursedcauldron.wildbackport.core.api.forge;

import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Supplier;

public class CoreRegistryImpl<T extends IForgeRegistryEntry<T>> extends CoreRegistry<T> {
    private final DeferredRegister<T> registry;

    public CoreRegistryImpl(Registry<T> registry, String modId) {
        super(registry, modId);
        this.registry = DeferredRegister.create(registry.key(), modId);
    }

    @SuppressWarnings("all")
    public static <T> CoreRegistry<T> create(Registry<T> key, String modId) {
        return new CoreRegistryImpl(key, modId);
    }

    @Override
    public <E extends T> Supplier<E> register(String key, Supplier<E> entry) {
        return this.registry.register(key, entry);
    }

    @Override
    public void bootstrap() {
        IEventBus bus = EventBuses.getModEventBus(this.modId).orElseThrow(() -> new IllegalStateException("Attempted to register stuff before registering a Mod Event Bus for: " + this.modId));
        this.registry.register(bus);
    }
}