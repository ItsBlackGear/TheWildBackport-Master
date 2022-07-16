package com.cursedcauldron.wildbackport.common.registry.worldgen;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.worldgen.decorator.AttachedToLeavesDecorator;
import com.cursedcauldron.wildbackport.common.worldgen.decorator.WeightedLeaveVineDecorator;
import com.cursedcauldron.wildbackport.common.worldgen.placers.UpwardBranchingTrunk;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import com.cursedcauldron.wildbackport.core.mixin.access.TreeDecoratorTypeAccessor;
import com.cursedcauldron.wildbackport.core.mixin.access.TrunkPlacerTypeAccessor;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.function.Supplier;

//<>

public class WBTreeDecorators {
    public static final CoreRegistry<TreeDecoratorType<?>> DECORATORS = CoreRegistry.create(Registry.TREE_DECORATOR_TYPES, WildBackport.MOD_ID);

    public static final Supplier<TreeDecoratorType<WeightedLeaveVineDecorator>> WEIGHTED_LEAVE_VINE = DECORATORS.register("leave_vine", () -> TreeDecoratorTypeAccessor.createTreeDecoratorType(WeightedLeaveVineDecorator.CODEC));
    public static final Supplier<TreeDecoratorType<AttachedToLeavesDecorator>> ATTACHED_TO_LEAVES   = DECORATORS.register("attached_to_leaves", () -> TreeDecoratorTypeAccessor.createTreeDecoratorType(AttachedToLeavesDecorator.CODEC));
}