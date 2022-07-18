package com.cursedcauldron.wildbackport.common.worldgen.placers;

import com.cursedcauldron.wildbackport.common.registry.worldgen.RootPlacerType;
import com.cursedcauldron.wildbackport.common.worldgen.decorator.LayerRootDecorator;
import com.cursedcauldron.wildbackport.common.worldgen.decorator.MangroveRootPlacement;
import com.cursedcauldron.wildbackport.common.worldgen.features.RootedTreeConfig;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;

public class MangroveRootPlacer extends RootPlacer {
    public static final Codec<MangroveRootPlacer> CODEC = RecordCodecBuilder.create(instance -> {
        return codec(instance).and(MangroveRootPlacement.CODEC.fieldOf("mangrove_root_placement").forGetter(placer -> {
            return placer.mangroveRootPlacement;
        })).apply(instance, MangroveRootPlacer::new);
    });
    private final MangroveRootPlacement mangroveRootPlacement;

    public MangroveRootPlacer(IntProvider trunkOffsetY, BlockStateProvider rootProvider, Optional<LayerRootDecorator> aboveRootPlacement, MangroveRootPlacement mangroveRootPlacement) {
        super(trunkOffsetY, rootProvider, aboveRootPlacement);
        this.mangroveRootPlacement = mangroveRootPlacement;
    }

    @Override
    public boolean generate(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, BlockPos origin, RootedTreeConfig config) {
        ArrayList<BlockPos> positions = Lists.newArrayList();
        BlockPos.MutableBlockPos mutable = pos.mutable();

        while(mutable.getY() < origin.getY()) {
            if (!this.canGrowThrough(level, mutable)) return false;

            mutable.move(Direction.UP);
        }

        positions.add(origin.below());
        for(Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos position = origin.relative(direction);
            ArrayList<BlockPos> offshootPositions = Lists.newArrayList();
            if (!this.canGrow(level, random, position, direction, origin, offshootPositions, 0)) return false;

            positions.addAll(offshootPositions);
            positions.add(origin.relative(direction));
        }

        for(BlockPos position : positions) this.placeRoots(level, replacer, random, position, config);

        return true;
    }

    private boolean canGrow(LevelSimulatedReader level, Random random, BlockPos pos, Direction direction, BlockPos origin, List<BlockPos> offshootPositions, int rootLength) {
        int length = this.mangroveRootPlacement.maxRootLength();
        if (rootLength != length && offshootPositions.size() <= length) {
            for(BlockPos position : this.getOffshootPositions(pos, direction, random, origin)) {
                if (this.canGrowThrough(level, position)) {
                    offshootPositions.add(position);
                    if (!this.canGrow(level, random, position, direction, origin, offshootPositions, rootLength + 1)) return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    protected List<BlockPos> getOffshootPositions(BlockPos pos, Direction direction, Random random, BlockPos origin) {
        BlockPos below = pos.below();
        BlockPos offset = pos.relative(direction);
        int distance = pos.distManhattan(origin);
        int rootWidth = this.mangroveRootPlacement.maxRootWidth();
        float chance = this.mangroveRootPlacement.randomSkewChance();
        if (distance > rootWidth - 3 && distance <= rootWidth) {
            return random.nextFloat() < chance ? List.of(below, offset.below()) : List.of(below);
        } else if (distance > rootWidth) {
            return List.of(below);
        } else if (random.nextFloat() < chance) {
            return List.of(below);
        } else {
            return random.nextBoolean() ? List.of(offset) : List.of(below);
        }
    }

    @Override
    protected boolean canGrowThrough(LevelSimulatedReader level, BlockPos pos) {
        return super.canGrowThrough(level, pos) || level.isStateAtPosition(pos, state -> state.is(this.mangroveRootPlacement.canGrowThrough()));
    }

    @Override
    protected void placeRoots(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos pos, RootedTreeConfig config) {
        if (level.isStateAtPosition(pos, state -> state.is(this.mangroveRootPlacement.muddyRootsIn()))) {
            BlockState state = this.mangroveRootPlacement.muddyRootsProvider().getState(random, pos);
            replacer.accept(pos, this.applyWaterlogging(level, pos, state));
        } else {
            super.placeRoots(level, replacer, random, pos, config);
        }
    }

    @Override
    protected RootPlacerType<?> getType() {
        return RootPlacerType.MANGROVE_ROOT_PLACER.get();
    }
}