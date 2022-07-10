package com.cursedcauldron.wildbackport.forge;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.ClientSetup;
import com.cursedcauldron.wildbackport.common.CommonSetup;
import com.cursedcauldron.wildbackport.core.api.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WildBackport.MOD_ID)
public class WildBackportForge {
    public WildBackportForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(WildBackport.MOD_ID, bus);
        bus.<FMLCommonSetupEvent>addListener(event -> CommonSetup.onPostClient());
        bus.<FMLClientSetupEvent>addListener(event -> ClientSetup.onPostClient());

        WildBackport.bootstrap();
        CommonSetup.onCommon();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientSetup::onClient);
    }
}