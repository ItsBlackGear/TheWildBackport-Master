package com.cursedcauldron.wildbackport.core.api.fabric;

import com.cursedcauldron.wildbackport.core.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

public class EnvironmentImpl {
    public static Environment.Platform getPlatform() {
        return Environment.Platform.FABRIC;
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}