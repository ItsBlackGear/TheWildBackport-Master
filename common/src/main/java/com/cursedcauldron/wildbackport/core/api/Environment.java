package com.cursedcauldron.wildbackport.core.api;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class Environment {
    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new AssertionError();
    }

    public static boolean isForge() {
        return getPlatform() == Platform.FORGE;
    }

    public static boolean isFabric() {
        return getPlatform() == Platform.FABRIC;
    }

    @ExpectPlatform
    public static Platform getPlatform() {
        throw new AssertionError();
    }

    public enum Platform {
        FORGE,
        FABRIC
    }
}