package com.cursedcauldron.wildbackport.fabric;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.CommonSetup;
import net.fabricmc.api.ModInitializer;

public class WildBackportFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        WildBackport.bootstrap();
        CommonSetup.onCommon();
    }
}