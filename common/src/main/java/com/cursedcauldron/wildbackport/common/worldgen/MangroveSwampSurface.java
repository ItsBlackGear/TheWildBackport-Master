package com.cursedcauldron.wildbackport.common.worldgen;

import com.cursedcauldron.wildbackport.common.registry.WBBiomes;
import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class MangroveSwampSurface {
    public static final SurfaceRules.ConditionSource ABOVE_60 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(60), 0);
    public static final SurfaceRules.ConditionSource ABOVE_63 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0);

    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.RuleSource terrain = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(WBBiomes.MANGROVE_SWAMP), makeStateRule(WBBlocks.MUD.get())));
        SurfaceRules.RuleSource generation = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(WBBiomes.MANGROVE_SWAMP), SurfaceRules.ifTrue(ABOVE_60, SurfaceRules.ifTrue(SurfaceRules.not(ABOVE_63), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0F), makeStateRule(Blocks.WATER))))))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(terrain))), SurfaceRules.ifTrue(SurfaceRules.waterStartCheck(-6, -1), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, terrain))));
        return SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), generation));
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}