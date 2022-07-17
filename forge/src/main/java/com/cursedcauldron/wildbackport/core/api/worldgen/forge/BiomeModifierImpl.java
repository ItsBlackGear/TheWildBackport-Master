package com.cursedcauldron.wildbackport.core.api.worldgen.forge;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.worldgen.BiomeModifier;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WildBackport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BiomeModifierImpl {
    public static void setup() {}

    @SubscribeEvent
    public static void event(BiomeLoadingEvent event) {
        BiomeModifier.INSTANCE.register(new ForgeBiomeWriter().build(event));
    }
}