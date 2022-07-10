package com.cursedcauldron.wildbackport.common.entities.brain.frog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class BiasedLongJumpTask<E extends Mob> extends FrogJumpToRandomPos<E> {
    private final TagKey<Block> preferredBlocks;
    private final float chance;
    private final List<FrogJumpToRandomPos.Target> targetCandidates = new ArrayList<>();
    private boolean priorityOnPreferred;

    public BiasedLongJumpTask(UniformInt cooldown, int yRange, int xzRange, float range, Function<E, SoundEvent> landingSound, TagKey<Block> preferredBlocks, float chance, Predicate<BlockState> landingBlocks) {
        super(cooldown, yRange, xzRange, range, landingSound, landingBlocks);
        this.preferredBlocks = preferredBlocks;
        this.chance = chance;
    }

    @Override
    protected void start(ServerLevel level, E entity, long time) {
        super.start(level, entity, time);
        this.targetCandidates.clear();
        this.priorityOnPreferred = entity.getRandom().nextFloat() < this.chance;
    }

    @Override
    protected Optional<FrogJumpToRandomPos.Target> jumpTarget(ServerLevel level) {
        if (!this.priorityOnPreferred) {
            return super.jumpTarget(level);
        } else {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

            while (!this.targets.isEmpty()) {
                Optional<FrogJumpToRandomPos.Target> jumpTarget = super.jumpTarget(level);
                if (jumpTarget.isPresent()) {
                    FrogJumpToRandomPos.Target target = jumpTarget.get();
                    if (level.getBlockState(mutable.setWithOffset(target.getPos(), Direction.DOWN)).is(this.preferredBlocks)) {
                        return jumpTarget;
                    }

                    this.targetCandidates.add(target);
                }
            }

            return !this.targetCandidates.isEmpty() ? Optional.of(this.targetCandidates.remove(0)) : Optional.empty();
        }
    }

    @Override
    protected boolean canLandOn(ServerLevel level, E entity, BlockPos pos) {
        return super.canLandOn(level, entity, pos) && this.cantLandInFluid(level, pos);
    }

    private boolean cantLandInFluid(ServerLevel level, BlockPos pos) {
        return level.getFluidState(pos).isEmpty() && level.getFluidState(pos.below()).isEmpty();
    }
}