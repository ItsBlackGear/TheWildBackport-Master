package com.cursedcauldron.wildbackport.common.events;

import com.cursedcauldron.wildbackport.core.mixin.access.StructureTemplatePoolAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;
import java.util.List;

//<>

public class StructureEvent {
    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(Registry.PROCESSOR_LIST_REGISTRY, new ResourceLocation("minecraft", "empty"));

    private static void addBuildingToPool(Registry<StructureTemplatePool> templatePoolRegistry,
                                          Registry<StructureProcessorList> processorListRegistry,
                                          ResourceLocation poolRL,
                                          String nbtPieceRL,
                                          int weight) {
        Holder<StructureProcessorList> emptyProcessorList = processorListRegistry.getHolderOrThrow(EMPTY_PROCESSOR_LIST_KEY);
        StructureTemplatePool pool = templatePoolRegistry.get(poolRL);
        if (pool == null) return;

        SinglePoolElement piece = SinglePoolElement.legacy(nbtPieceRL, emptyProcessorList).apply(StructureTemplatePool.Projection.RIGID);

        StructureTemplatePoolAccessor pools = (StructureTemplatePoolAccessor)pool;
        for (int i = 0; i < weight; i++) pools.getTemplates().add(piece);

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(pools.getRawTemplates());
        listOfPieceEntries.add(new Pair<>(piece, weight));
        pools.setRawTemplates(listOfPieceEntries);
    }

    public static void bootstrap() {
//            Registry<StructureTemplatePool> templatePoolRegistry = server.registryAccess().registry(Registry.TEMPLATE_POOL_REGISTRY).orElseThrow();
//            Registry<StructureProcessorList> processorListRegistry = server.registryAccess().registry(Registry.PROCESSOR_LIST_REGISTRY).orElseThrow();
//
//            addBuildingToPool(templatePoolRegistry, processorListRegistry, new ResourceLocation("minecraft:pillager_outpost/features"), "wildbackport:feature_cage_with_allays", 1);
    }
}