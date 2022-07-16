package com.cursedcauldron.wildbackport.common.worldgen.placers;

import com.cursedcauldron.wildbackport.common.registry.worldgen.WBTrunkPlacers;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

//<>

public class UpwardBranchingTrunk extends TrunkPlacer {
    public static final Codec<UpwardBranchingTrunk> CODEC = RecordCodecBuilder.create(instance -> {
        return trunkPlacerParts(instance).and(instance.group(IntProvider.POSITIVE_CODEC.fieldOf("extra_branch_steps").forGetter(placer -> {
            return placer.extraBranchSteps;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("place_branch_per_log_probability").forGetter(placer -> {
            return placer.placeBranchPerLogProbability;
        }), IntProvider.NON_NEGATIVE_CODEC.fieldOf("extra_branch_length").forGetter(placer -> {
            return placer.extraBranchLength;
        }), RegistryCodecs.homogeneousList(Registry.BLOCK_REGISTRY).fieldOf("can_grow_through").forGetter(placer -> {
            return placer.canGrowThrough;
        }))).apply(instance, UpwardBranchingTrunk::new);
    });
    private final IntProvider extraBranchSteps;
    private final float placeBranchPerLogProbability;
    private final IntProvider extraBranchLength;
    public final HolderSet<Block> canGrowThrough;

    public UpwardBranchingTrunk(int baseHeight, int firstRandomHeight, int secondRandomHeight, IntProvider extraBranchSteps, float placeBranchPerLogProbability, IntProvider extraBranchLength, HolderSet<Block> canGrowThrough) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
        this.extraBranchSteps = extraBranchSteps;
        this.placeBranchPerLogProbability = placeBranchPerLogProbability;
        this.extraBranchLength = extraBranchLength;
        this.canGrowThrough = canGrowThrough;
    }

    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeConfiguration config) {
        List<FoliagePlacer.FoliageAttachment> attachments = Lists.newArrayList();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for(int i = 0; i < height; ++i) {
            int yOffset = startPos.getY() + i;
            if (placeLog(level, replacer, random, mutable.set(startPos.getX(), yOffset, startPos.getZ()), config) && i < height - 1 && random.nextFloat() < this.placeBranchPerLogProbability) {
                Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                int offset = this.extraBranchLength.sample(random);
                int length = Math.max(0, offset - this.extraBranchLength.sample(random) - 1);
                int steps = this.extraBranchSteps.sample(random);
                this.placeBranch(level, replacer, random, height, config, attachments, mutable, yOffset, direction, length, steps);
            }

            if (i == height - 1) attachments.add(new FoliagePlacer.FoliageAttachment(mutable.set(startPos.getX(), yOffset + 1, startPos.getZ()), 0, false));
        }

        return attachments;
    }

    private void placeBranch(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, TreeConfiguration config, List<FoliagePlacer.FoliageAttachment> attachments, BlockPos.MutableBlockPos mutable, int yOffset, Direction direction, int length, int steps) {
        int y = yOffset + length;
        int x = mutable.getX();
        int z = mutable.getZ();

        for(int l = length; l < height && steps > 0; --steps) {
            if (l >= 1) {
                int offset = yOffset + l;
                x += direction.getStepX();
                z += direction.getStepZ();
                y = offset;
                if (placeLog(level, replacer, random, mutable.set(x, offset, z), config)) y = offset + 1;

                attachments.add(new FoliagePlacer.FoliageAttachment(mutable.immutable(), 0, false));
            }

            ++l;
        }

        if (y - yOffset > 1) {
            BlockPos pos = new BlockPos(x, y, z);
            attachments.add(new FoliagePlacer.FoliageAttachment(pos, 0, false));
            attachments.add(new FoliagePlacer.FoliageAttachment(pos.below(2), 0, false));
        }
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return WBTrunkPlacers.UPWARDS_BRANCHING_TRUNK.get();
    }
}