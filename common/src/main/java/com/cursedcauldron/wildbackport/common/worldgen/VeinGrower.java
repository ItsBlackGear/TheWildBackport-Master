package com.cursedcauldron.wildbackport.common.worldgen;

import com.cursedcauldron.wildbackport.common.blocks.SculkVeinBlock;
import com.cursedcauldron.wildbackport.common.utils.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class VeinGrower {
    public static final GrowType[] GROW_TYPES = new GrowType[]{GrowType.SAME_POSITION, GrowType.SAME_PLANE, GrowType.WRAP_AROUND};
    private final GrowChecker growChecker;

    public VeinGrower(MultifaceBlock lichen) {
        this(new VeinGrowChecker(lichen));
    }

    public VeinGrower(GrowChecker growChecker) {
        this.growChecker = growChecker;
    }

    public boolean canGrow(BlockState state, BlockGetter getter, BlockPos pos, Direction direction) {
        return DirectionUtils.stream().anyMatch(newDirection -> this.getGrowPos(state, getter, pos, direction, newDirection, this.growChecker::canGrow).isPresent());
    }

    public Optional<GrowPos> grow(BlockState state, LevelAccessor level, BlockPos pos, Random random) {
        return DirectionUtils.shuffle(random).stream().filter(direction -> this.growChecker.canGrow(state, direction)).map(direction -> this.grow(state, level, pos, direction, random, false)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }

    public long grow(BlockState state, LevelAccessor level, BlockPos pos, boolean postProcess) {
        return DirectionUtils.stream().filter(direction -> this.growChecker.canGrow(state, direction)).map(direction -> this.grow(state, level, pos, direction, postProcess)).reduce(0L, Long::sum);
    }

    public Optional<GrowPos> grow(BlockState state, LevelAccessor level, BlockPos pos, Direction direction, Random random, boolean postProcess) {
        return DirectionUtils.shuffle(random).stream().map(newDirection -> this.grow(state, level, pos, direction, newDirection, postProcess)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }

    private long grow(BlockState state, LevelAccessor level, BlockPos pos, Direction direction, boolean postProcess) {
        return DirectionUtils.stream().map(newDirection -> this.grow(state, level, pos, direction, newDirection, postProcess)).filter(Optional::isPresent).count();
    }

    public Optional<GrowPos> grow(BlockState state, LevelAccessor world, BlockPos pos, Direction oldDirection, Direction newDirection, boolean postProcess) {
        return this.getGrowPos(state, world, pos, oldDirection, newDirection, this.growChecker::canGrow).flatMap(growPos -> this.place(world, growPos, postProcess));
    }

    public Optional<GrowPos> getGrowPos(BlockState state, BlockGetter world, BlockPos pos, Direction oldDirection, Direction newDirection, GrowPosPredicate predicate) {
        if (newDirection.getAxis() == oldDirection.getAxis()) {
            return Optional.empty();
        }

        if (!(this.growChecker.canGrow(state) || this.growChecker.hasDirection(state, oldDirection) && !this.growChecker.hasDirection(state, newDirection))) {
            return Optional.empty();
        }

        for (GrowType growType : this.growChecker.getGrowTypes()) {
            GrowPos growPos = growType.getGrowPos(pos, newDirection, oldDirection);
            if (!predicate.test(world, pos, growPos)) continue;
            return Optional.of(growPos);
        }
        return Optional.empty();
    }

    public Optional<GrowPos> place(LevelAccessor world, GrowPos pos, boolean markForPostProcessing) {
        BlockState state = world.getBlockState(pos.pos());
        if (this.growChecker.place(world, pos, state, markForPostProcessing)) {
            return Optional.of(pos);
        }
        return Optional.empty();
    }

    public static class VeinGrowChecker implements GrowChecker {
        protected MultifaceBlock multifaceBlock;

        public VeinGrowChecker(MultifaceBlock multifaceBlock) {
            this.multifaceBlock = multifaceBlock;
        }

        @Override @Nullable
        public BlockState getStateWithDirection(BlockState state, BlockGetter getter, BlockPos pos, Direction face) {
            return this.multifaceBlock.getStateForPlacement(state, getter, pos, face);
        }

        protected boolean canGrow(BlockGetter getter, BlockPos pos, BlockPos growPos, Direction direction, BlockState state) {
            return state.isAir() || state.is(this.multifaceBlock) || state.is(Blocks.WATER) && state.getFluidState().isSource();
        }

        @Override
        public boolean canGrow(BlockGetter getter, BlockPos pos, GrowPos growPos) {
            BlockState state = getter.getBlockState(growPos.pos());
            return this.canGrow(getter, pos, growPos.pos(), growPos.face(), state) && ((SculkVeinBlock)this.multifaceBlock).canGrowWithDirection(getter, state, growPos.pos(), growPos.face());
        }
    }

    public interface GrowChecker {
        @Nullable BlockState getStateWithDirection(BlockState state, BlockGetter getter, BlockPos pos, Direction face);

        boolean canGrow(BlockGetter getter, BlockPos pos, GrowPos growPos);

        default GrowType[] getGrowTypes() {
            return GROW_TYPES;
        }

        default boolean hasDirection(BlockState state, Direction direction) {
            return SculkVeinBlock.hasFace(state, direction);
        }

        default boolean canGrow(BlockState state) {
            return false;
        }

        default boolean canGrow(BlockState state, Direction direction) {
            return this.canGrow(state) || this.hasDirection(state, direction);
        }

        default boolean place(LevelAccessor world, GrowPos growPos, BlockState state, boolean postProcess) {
            BlockState blockState = this.getStateWithDirection(state, world, growPos.pos(), growPos.face());
            if (blockState != null) {
                if (postProcess) {
                    world.getChunk(growPos.pos()).markPosForPostprocessing(growPos.pos());
                }
                return world.setBlock(growPos.pos(), blockState, 2);
            }
            return false;
        }
    }

    public interface GrowPosPredicate {
        boolean test(BlockGetter getter, BlockPos pos, GrowPos growPos);
    }

    public enum GrowType {
        SAME_POSITION {
            @Override
            public GrowPos getGrowPos(BlockPos pos, Direction newDirection, Direction oldDirection) {
                return new GrowPos(pos, newDirection);
            }
        },
        SAME_PLANE {
            @Override
            public GrowPos getGrowPos(BlockPos pos, Direction newDirection, Direction oldDirection) {
                return new GrowPos(pos.relative(newDirection), oldDirection);
            }
        },
        WRAP_AROUND {
            @Override
            public GrowPos getGrowPos(BlockPos pos, Direction newDirection, Direction oldDirection) {
                return new GrowPos(pos.relative(newDirection).relative(oldDirection), newDirection.getOpposite());
            }
        };

        public abstract GrowPos getGrowPos(BlockPos pos, Direction newDirection, Direction oldDirection);
    }

    public record GrowPos(BlockPos pos, Direction face) {}
}