package com.cursedcauldron.wildbackport.forge;

import com.cursedcauldron.wildbackport.client.ClientSetup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class WildBackportForgeClient {
    public WildBackportForgeClient() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    public void setup(FMLClientSetupEvent event) {
        ClientSetup.onClient();
        ClientSetup.onPostClient();
    }
}
