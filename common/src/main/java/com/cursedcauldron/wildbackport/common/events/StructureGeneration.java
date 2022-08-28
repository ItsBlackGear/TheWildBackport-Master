package com.cursedcauldron.wildbackport.common.events;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.mixin.access.StructureTemplatePoolAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.PillagerOutpostPools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.List;

//<>

public class StructureGeneration {
    public static void registerAllayCages() {
        PillagerOutpostPools.bootstrap();
        StructureGeneration.addToPool(new ResourceLocation("pillager_outpost/features"), new ResourceLocation(WildBackport.MOD_ID, "pillager_outpost/feature_cage_with_allays"), 1);
    }

    private static void addToPool(ResourceLocation poolId, ResourceLocation pieceId, int weight) {
        StructureTemplatePool pool = BuiltinRegistries.TEMPLATE_POOL.get(poolId);
        if (pool == null) return;

        StructurePoolElement piece = StructurePoolElement.legacy(pieceId.toString(), ProcessorLists.EMPTY).apply(StructureTemplatePool.Projection.RIGID);
        List<StructurePoolElement> templates = ((StructureTemplatePoolAccessor)pool).getTemplates();
        List<Pair<StructurePoolElement, Integer>> rawTemplates = ((StructureTemplatePoolAccessor)pool).getRawTemplates();
        if (templates == null || rawTemplates == null) return;

        for (int i = 0; i < weight; i++) templates.add(piece);
        rawTemplates.add(Pair.of(piece, weight));
    }
}