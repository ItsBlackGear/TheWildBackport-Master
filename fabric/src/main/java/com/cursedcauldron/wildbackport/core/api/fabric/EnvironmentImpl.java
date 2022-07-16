package com.cursedcauldron.wildbackport.core.api.fabric;

import com.cursedcauldron.wildbackport.core.api.Environment;

public class EnvironmentImpl {
    public static Environment.Platform getPlatform() {
        return Environment.Platform.FABRIC;
    }
}