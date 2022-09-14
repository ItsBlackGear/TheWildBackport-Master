package com.cursedcauldron.wildbackport.common.worldgen.structure;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.worldgen.structure.pools.AncientCityStructurePools;
import com.cursedcauldron.wildbackport.common.worldgen.structure.processors.WBProcessorLists;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class AncientCityStructurePieces {
    public static final Holder<StructureTemplatePool> START = Pools.register(new StructureTemplatePool(new ResourceLocation(WildBackport.MOD_ID, "ancient_city/city_center"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("ancient_city/city_center/city_center_1", WBProcessorLists.ANCIENT_CITY_START_DEGRADATION), 1), Pair.of(StructurePoolElement.single("ancient_city/city_center/city_center_2", WBProcessorLists.ANCIENT_CITY_START_DEGRADATION), 1), Pair.of(StructurePoolElement.single("ancient_city/city_center/city_center_3", WBProcessorLists.ANCIENT_CITY_START_DEGRADATION), 1)), StructureTemplatePool.Projection.RIGID));

    public static void bootstrap() {
        AncientCityStructurePools.bootstrap();
    }
}