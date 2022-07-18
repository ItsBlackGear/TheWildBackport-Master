package com.cursedcauldron.wildbackport.common.worldgen.features;

import com.cursedcauldron.wildbackport.common.worldgen.placers.UpwardBranchingTrunk;
import com.cursedcauldron.wildbackport.core.mixin.access.TreeFeatureAccessor;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;

//<>

public class RootedTreeFeature extends Feature<RootedTreeConfig> {
    public RootedTreeFeature(Codec<RootedTreeConfig> codec) {
        super(codec);
    }

    private boolean generate(WorldGenLevel level, Random random, BlockPos pos, BiConsumer<BlockPos, BlockState> rootPlacerReplacer, BiConsumer<BlockPos, BlockState> trunkPlacerReplacer, BiConsumer<BlockPos, BlockState> foliagePlacerReplacer, RootedTreeConfig config) {
        int treeHeight = config.trunkPlacer.getTreeHeight(random);
        int foliageHeight = config.foliagePlacer.foliageHeight(random, treeHeight, config);
        int trunkLength = treeHeight - foliageHeight;
        int foliageRadius = config.foliagePlacer.foliageRadius(random, trunkLength);
        BlockPos origin = config.rootPlacer.map(rootPlacer -> rootPlacer.trunkOffset(pos, random)).orElse(pos);
        int minHeight = Math.min(pos.getY(), origin.getY());
        int maxHeight = Math.max(pos.getY(), origin.getY()) + treeHeight + 1;
        if (minHeight >= level.getMinBuildHeight() + 1 && maxHeight <= level.getMaxBuildHeight()) {
            OptionalInt clippedHeight = config.minimumSize.minClippedHeight();
            int topPosition = this.getTopPosition(level, treeHeight, origin, config);
            if (topPosition >= treeHeight || clippedHeight.isPresent() && topPosition >= clippedHeight.getAsInt()) {
                if (config.rootPlacer.isPresent() && !config.rootPlacer.get().generate(level, rootPlacerReplacer, random, pos, origin, config)) {
                    return false;
                } else {
                    List<FoliagePlacer.FoliageAttachment> foliage = config.trunkPlacer.placeTrunk(level, trunkPlacerReplacer, random, topPosition, origin, config);
                    foliage.forEach(node -> config.foliagePlacer.createFoliage(level, foliagePlacerReplacer, random, config, topPosition, node, foliageHeight, foliageRadius));
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private int getTopPosition(LevelSimulatedReader level, int height, BlockPos pos, RootedTreeConfig config) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int y = 0; y <= height + 1; ++y) {
            int size = config.minimumSize.getSizeAtHeight(height, y);

            for (int x = -size; x <= size; ++x) {
                for (int z = -size; z <= size; ++z) {
                    mutable.setWithOffset(pos, x, y, z);
                    boolean isValid = TreeFeature.validTreePos(level, pos) || level.isStateAtPosition(pos, state -> state.is(BlockTags.LOGS)) || (config.trunkPlacer instanceof UpwardBranchingTrunk trunk && level.isStateAtPosition(pos, state -> state.is(trunk.canGrowThrough)));
                    if (!isValid || (!config.ignoreVines && TreeFeatureAccessor.isVine(level, mutable))) return y - 2;
                }
            }
        }

        return height;
    }

    @Override
    public final boolean place(FeaturePlaceContext<RootedTreeConfig> context) {
        WorldGenLevel level = context.level();
        Random random = context.random();
        BlockPos pos = context.origin();
        RootedTreeConfig config = context.config();
        HashSet<BlockPos> rootPos = Sets.newHashSet();
        HashSet<BlockPos> trunkPos = Sets.newHashSet();
        HashSet<BlockPos> foliagePos = Sets.newHashSet();
        HashSet<BlockPos> decoratorPos = Sets.newHashSet();
        BiConsumer<BlockPos, BlockState> rootReplacer = (position, state) -> {
            rootPos.add(position.immutable());
            level.setBlock(position, state, 19);
        };
        BiConsumer<BlockPos, BlockState> trunkReplacer = (position, state) -> {
            trunkPos.add(position.immutable());
            level.setBlock(position, state, 19);
        };
        BiConsumer<BlockPos, BlockState> foliageReplacer = (position, state) -> {
            foliagePos.add(position.immutable());
            level.setBlock(position, state, 19);
        };
        BiConsumer<BlockPos, BlockState> decoratorReplacer = (position, state) -> {
            decoratorPos.add(position.immutable());
            level.setBlock(position, state, 19);
        };
        boolean generate = this.generate(level, random, pos, rootReplacer, trunkReplacer, foliageReplacer, config);
        if (!generate || trunkPos.isEmpty() && foliagePos.isEmpty()) return false;
        if (!config.decorators.isEmpty()) {
            ArrayList<BlockPos> rootPositions = Lists.newArrayList(rootPos);
            ArrayList<BlockPos> trunkPositions = Lists.newArrayList(trunkPos);
            ArrayList<BlockPos> foliagePositions = Lists.newArrayList(foliagePos);
            trunkPositions.sort(Comparator.comparingInt(Vec3i::getY));
            foliagePositions.sort(Comparator.comparingInt(Vec3i::getY));
            rootPositions.sort(Comparator.comparingInt(Vec3i::getY));
            config.decorators.forEach(treeDecorator -> treeDecorator.place(level, decoratorReplacer, random, trunkPositions, foliagePositions));
        }

        return BoundingBox.encapsulatingPositions(Iterables.concat(trunkPos, foliagePos, decoratorPos)).map(box -> {
            DiscreteVoxelShape shape = RootedTreeFeature.placeLogsAndLeaves(level, box, trunkPos, decoratorPos);
            StructureTemplate.updateShapeAtEdge(level, Block.UPDATE_ALL, shape, box.minX(), box.minY(), box.minZ());
            return true;
        }).orElse(false);
    }

    private static DiscreteVoxelShape placeLogsAndLeaves(LevelAccessor level, BoundingBox box, Set<BlockPos> trunkPositions, Set<BlockPos> decoratorPositions) {
        ArrayList<Set<BlockPos>> positions = Lists.newArrayList();
        BitSetDiscreteVoxelShape shape = new BitSetDiscreteVoxelShape(box.getXSpan(), box.getYSpan(), box.getZSpan());

        for (int tries = 0; tries < 6; ++tries) positions.add(Sets.newHashSet());

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (BlockPos pos : Lists.newArrayList(decoratorPositions)) if (box.isInside(pos)) shape.fill(pos.getX() - box.minX(), pos.getY() - box.minY(), pos.getZ() - box.minZ());

        for (BlockPos pos : Lists.newArrayList(trunkPositions)) {
            if (box.isInside(pos)) shape.fill(pos.getX() - box.minX(), pos.getY() - box.minY(), pos.getZ() - box.minZ());

            for (Direction direction : Direction.values()) {
                BlockState blockState;
                mutable.setWithOffset(pos, direction);
                if (trunkPositions.contains(mutable) || !(blockState = level.getBlockState(mutable)).hasProperty(BlockStateProperties.DISTANCE)) continue;
                positions.get(0).add(mutable.immutable());
                TreeFeatureAccessor.setBlockKnownShape(level, mutable, blockState.setValue(BlockStateProperties.DISTANCE, 1));
                if (!box.isInside(mutable)) continue;
                shape.fill(mutable.getX() - box.minX(), mutable.getY() - box.minY(), mutable.getZ() - box.minZ());
            }
        }

        for (int tries = 1; tries < 6; ++tries) {
            Set<BlockPos> trunkPos = positions.get(tries - 1);
            Set<BlockPos> foliagePos = positions.get(tries);
            for (BlockPos pos : trunkPos) {
                if (box.isInside(pos)) shape.fill(pos.getX() - box.minX(), pos.getY() - box.minY(), pos.getZ() - box.minZ());

                for (Direction direction : Direction.values()) {
                    mutable.setWithOffset(pos, direction);
                    BlockState state = level.getBlockState(mutable);
                    if (trunkPos.contains(mutable) || foliagePos.contains(mutable) || !state.hasProperty(BlockStateProperties.DISTANCE) || (state.getValue(BlockStateProperties.DISTANCE)) <= tries + 1) continue;
                    BlockState foliage = state.setValue(BlockStateProperties.DISTANCE, tries + 1);
                    TreeFeatureAccessor.setBlockKnownShape(level, mutable, foliage);
                    if (box.isInside(mutable)) (shape).fill(mutable.getX() - box.minX(), mutable.getY() - box.minY(), mutable.getZ() - box.minZ());
                    foliagePos.add(mutable.immutable());
                }
            }
        }
        return shape;
    }
}