package com.cursedcauldron.wildbackport.common;

import com.cursedcauldron.wildbackport.common.entities.Allay;
import com.cursedcauldron.wildbackport.common.entities.Frog;
import com.cursedcauldron.wildbackport.common.entities.Tadpole;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.events.StructureGeneration;
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntityTypes;
import com.cursedcauldron.wildbackport.common.registry.worldgen.WBWorldGeneration;
import com.cursedcauldron.wildbackport.core.api.MobRegistry;
import com.cursedcauldron.wildbackport.core.api.worldgen.BiomeModifier;

public class CommonSetup {
    /**
     * Runs features at initializing
     */
    public static void onCommon() {
        // Entity Attributes
        MobRegistry.registerAttributes(WBEntityTypes.ALLAY, Allay::createAttributes);
        MobRegistry.registerAttributes(WBEntityTypes.FROG, Frog::createAttributes);
        MobRegistry.registerAttributes(WBEntityTypes.TADPOLE, Tadpole::createAttributes);
        MobRegistry.registerAttributes(WBEntityTypes.WARDEN, Warden::createAttributes);

    }

    /**
     * Runs features post bootstrap
     */
    public static void onPostCommon() {
        WBWorldGeneration.bootstrap();
        BiomeModifier.setup();
        VanillaInteraction.setup();

        StructureGeneration.registerAllayCages();
    }
}