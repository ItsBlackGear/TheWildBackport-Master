package com.cursedcauldron.wildbackport.fabric;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.CommonSetup;
import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;

public class WildBackportFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        WildBackport.bootstrap();
        CommonSetup.onCommon();
        CommonSetup.onPostCommon();

        //TODO: check why is not working by itself...
        CompostingChanceRegistry composter = CompostingChanceRegistry.INSTANCE;
        composter.add(WBBlocks.MANGROVE_LEAVES.get(), 0.3F);
        composter.add(WBBlocks.MANGROVE_ROOTS.get(), 0.3F);
        composter.add(WBBlocks.MANGROVE_PROPAGULE.get(), 0.3F);
    }
}