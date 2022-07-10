package com.cursedcauldron.wildbackport.core.api;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * @author Trikzon & Andante
 */
public abstract class CoreRegistry<T> {
    protected final Registry<T> registry;
    protected final String modId;
    protected boolean isPresent;

    protected CoreRegistry(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
        this.isPresent = false;
    }

    @ExpectPlatform
    public static <T> CoreRegistry<T> create(Registry<T> key, String modId) {
        throw new AssertionError();
    }

    public abstract <E extends T> Supplier<E> register(String key, Supplier<E> entry);

    public void register() {
        if (this.isPresent) throw new IllegalArgumentException("Duplication of Registry: " + this.registry);
        this.isPresent = true;
        this.bootstrap();
    }

    public abstract void bootstrap();

    public static class DefaultRegistry<T> extends CoreRegistry<T> {
        public DefaultRegistry(Registry<T> registry, String modId) {
            super(registry, modId);
        }

        @Override
        public <E extends T> Supplier<E> register(String key, Supplier<E> entry) {
            E registry = Registry.register(this.registry, new ResourceLocation(this.modId, key), entry.get());
            return () -> registry;
        }

        @Override
        public void bootstrap() {}
    }
}