package com.cursedcauldron.wildbackport.common;

import com.cursedcauldron.wildbackport.common.entities.Allay;
import com.cursedcauldron.wildbackport.common.entities.Frog;
import com.cursedcauldron.wildbackport.common.entities.Tadpole;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.entities.access.Recovery;
import com.cursedcauldron.wildbackport.common.items.CompassItemPropertyFunction;
import com.cursedcauldron.wildbackport.common.registry.WBItems;
import com.cursedcauldron.wildbackport.common.worldgen.structure.StructureGeneration;
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntityTypes;
import com.cursedcauldron.wildbackport.common.registry.worldgen.WBWorldGeneration;
import com.cursedcauldron.wildbackport.core.api.MobRegistry;
import com.cursedcauldron.wildbackport.core.api.worldgen.BiomeModifier;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

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
        VanillaIntegration.setup();
        StructureGeneration.registerAllayCages();
    }
}