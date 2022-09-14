package com.cursedcauldron.wildbackport.core.api.forge;

import com.cursedcauldron.wildbackport.core.api.Environment;
import net.minecraftforge.fml.ModList;

public class EnvironmentImpl {
    public static Environment.Platform getPlatform() {
        return Environment.Platform.FORGE;
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}