package com.cursedcauldron.wildbackport.core.api.forge;

import com.cursedcauldron.wildbackport.core.api.Environment;

public class EnvironmentImpl {
    public static Environment.Platform getPlatform() {
        return Environment.Platform.FORGE;
    }
}