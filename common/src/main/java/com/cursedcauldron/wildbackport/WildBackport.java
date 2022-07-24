package com.cursedcauldron.wildbackport;

import com.cursedcauldron.wildbackport.client.registry.WBParticleTypes;
import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
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
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntities;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.cursedcauldron.wildbackport.common.registry.entity.WBSensorTypes;
import com.cursedcauldron.wildbackport.common.tag.WBBiomeTags;
import com.cursedcauldron.wildbackport.common.tag.WBBlockTags;
import com.cursedcauldron.wildbackport.common.tag.WBEntityTypeTags;
import com.cursedcauldron.wildbackport.common.tag.WBGameEventTags;
import com.cursedcauldron.wildbackport.common.tag.WBItemTags;
import com.mojang.logging.LogUtils;
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
        WBEntities.ENTITIES.register();
        WBGameEvents.EVENTS.register();
        WBFeatures.FEATURES.register();
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
        WBBlockTags.TAGS.bootstrap();
        WBEntityTypeTags.TAGS.bootstrap();
        WBGameEventTags.TAGS.bootstrap();
        WBItemTags.TAGS.bootstrap();
    }
}