package com.cursedcauldron.wildbackport.fabric;

import com.cursedcauldron.wildbackport.client.ClientSetup;
import net.fabricmc.api.ClientModInitializer;

public class WildBackportFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSetup.onClient();
        ClientSetup.onPostClient();
    }
}