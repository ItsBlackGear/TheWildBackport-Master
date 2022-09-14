package com.cursedcauldron.wildbackport;

import com.cursedcauldron.wildbackport.client.registry.WBParticleTypes;
import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.access.Recovery;
import com.cursedcauldron.wildbackport.common.items.CompassItemPropertyFunction;
import com.cursedcauldron.wildbackport.common.registry.Instruments;
import com.cursedcauldron.wildbackport.common.registry.WBBiomes;
import com.cursedcauldron.wildbackport.common.registry.WBBlockEntities;
import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import com.cursedcauldron.wildbackport.common.registry.WBEnchantments;
import com.cursedcauldron.wildbackport.common.registry.WBGameEvents;
import com.cursedcauldron.wildbackport.common.registry.WBGameRules;
import com.cursedcauldron.wildbackport.common.registry.WBItems;
import com.cursedcauldron.wildbackport.common.registry.WBMobEffects;
import com.cursedcauldron.wildbackport.common.registry.WBPositionSources;
import com.cursedcauldron.wildbackport.common.registry.worldgen.WBFeatures;
import com.cursedcauldron.wildbackport.common.registry.worldgen.RootPlacerType;
import com.cursedcauldron.wildbackport.common.registry.worldgen.WBTreeDecorators;
import com.cursedcauldron.wildbackport.common.registry.worldgen.WBTrunkPlacers;
import com.cursedcauldron.wildbackport.common.registry.entity.WBActivities;
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntityTypes;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.cursedcauldron.wildbackport.common.registry.entity.WBSensorTypes;
import com.cursedcauldron.wildbackport.common.tag.InstrumentTags;
import com.cursedcauldron.wildbackport.common.tag.WBBiomeTags;
import com.cursedcauldron.wildbackport.common.tag.WBBlockTags;
import com.cursedcauldron.wildbackport.common.tag.WBEntityTypeTags;
import com.cursedcauldron.wildbackport.common.tag.WBGameEventTags;
import com.cursedcauldron.wildbackport.common.tag.WBItemTags;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

//<>

public class WildBackport {
    public static final String MOD_ID = "wildbackport";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void bootstrap() {
        // Registries
        WBActivities.ACTIVITIES.register();
        WBBiomes.BIOMES.register();
        WBBlockEntities.BLOCKS.register();
        WBBlocks.BLOCKS.register();
        WBEnchantments.ENCHANTMENTS.register();
        WBEntityTypes.ENTITIES.register();
        WBGameEvents.EVENTS.register();
        WBFeatures.FEATURES.register();
        Instruments.INSTRUMENTS.register();
        WBItems.ITEMS.register();
        WBMemoryModules.MEMORIES.register();
        WBMobEffects.EFFECTS.register();
        WBParticleTypes.PARTICLES.register();
        WBPositionSources.SOURCES.register();
        RootPlacerType.PLACERS.register();
        WBSensorTypes.SENSORS.register();
        WBSoundEvents.SOUNDS.register();
        WBTreeDecorators.DECORATORS.register();
        WBTrunkPlacers.PLACERS.register();

        WBGameRules.setup();

        // Tags
        WBBiomeTags.TAGS.bootstrap();
        WBBlockTags.BUILDER.bootstrap();
        WBEntityTypeTags.TAGS.bootstrap();
        WBGameEventTags.TAGS.bootstrap();
        WBItemTags.TAGS.bootstrap();
        InstrumentTags.TAGS.bootstrap();

//        ItemProperties.register(WBItems.RECOVERY_COMPASS.get(), new ResourceLocation("angle"), new CompassItemPropertyFunction((level, stack, entity) -> {
//            return entity instanceof Player player ? Recovery.of(player).getLastDeathLocation().orElse(null) : null;
//        }));
    }
}