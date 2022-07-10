package com.cursedcauldron.wildbackport.common.blocks;

import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.worldgen.VeinGrower;
import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import com.cursedcauldron.wildbackport.common.tag.WBBlockTags;
import com.cursedcauldron.wildbackport.common.utils.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

//<>

public class SculkVeinBlock extends MultifaceBlock implements SculkSpreadable, SimpleWaterloggedBlock {
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public final VeinGrower allGrowTypeGrower = new VeinGrower(new SculkVeinGrowChecker(VeinGrower.GROW_TYPES));
    private final VeinGrower samePositionOnlyGrower = new VeinGrower(new SculkVeinGrowChecker(VeinGrower.GrowType.SAME_POSITION));

    public SculkVeinBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    public VeinGrower getAllGrowTypeGrower() {
        return this.allGrowTypeGrower;
    }


    public VeinGrower getSamePositionOnlyGrower() {
        return this.samePositionOnlyGrower;
    }

    public static boolean place(LevelAccessor level, BlockPos pos, BlockState state, Collection<Direction> directions) {
        boolean canPlace = false;
        BlockState veinState = WBBlocks.SCULK_VEIN.get().defaultBlockState();

        for (Direction direction : directions) {
            BlockPos blockPos = pos.relative(direction);
            if (canGrowOn(level, direction, blockPos, level.getBlockState(blockPos))) {
                veinState = veinState.setValue(getFaceProperty(direction), true);
                canPlace = true;
            }
        }

        if (!canPlace) {
            return false;
        } else {
            if (!state.getFluidState().isEmpty()) {
                veinState = veinState.setValue(WATERLOGGED, true);
            }

            level.setBlock(pos, veinState, 3);
            return true;
        }
    }

    @Override
    public void spreadAtSamePosition(LevelAccessor level, BlockState state, BlockPos pos, Random random) {
        if (state.is(this)) {
            for (Direction direction : DIRECTIONS) {
                BooleanProperty property = getFaceProperty(direction);
                if (state.getValue(property) && level.getBlockState(pos.relative(direction)).is(WBBlocks.SCULK.get())) {
                    state = state.setValue(property, false);
                }
            }

            if (!hasAnyFace(state)) {
                FluidState fluid = level.getFluidState(pos);
                state = (fluid.isEmpty() ? Blocks.AIR : Blocks.WATER).defaultBlockState();
            }

            level.setBlock(pos, state, 3);
            SculkSpreadable.super.spreadAtSamePosition(level, state, pos, random);
        }
    }

    @Override
    public int spread(SculkSpreadManager.Cursor cursor, LevelAccessor level, BlockPos pos, Random random, SculkSpreadManager spreadManager, boolean shouldConvert) {
        if (shouldConvert && this.convertToBlock(spreadManager, level, cursor.getPos(), random)) {
            return cursor.getCharge() - 1;
        } else {
            return random.nextInt(spreadManager.getSpreadChance()) == 0 ? Mth.floor((float)cursor.getCharge() * 0.5F) : cursor.getCharge();
        }
    }

    private boolean convertToBlock(SculkSpreadManager spreadManager, LevelAccessor level, BlockPos pos, Random random) {
        BlockState state = level.getBlockState(pos);
        TagKey<Block> replaceable = spreadManager.getReplaceableBlocks();

        for (Direction direction : DirectionUtils.shuffle(random)) {
            if (hasFace(state, direction)) {
                BlockPos blockPos = pos.relative(direction);
                BlockState blockState = level.getBlockState(blockPos);
                if (blockState.is(replaceable)) {
                    BlockState sculk = WBBlocks.SCULK.get().defaultBlockState();
                    level.setBlock(blockPos, sculk, 3);
                    Block.pushEntitiesUp(blockState, sculk, (ServerLevel)level, pos);
                    level.playSound(null, blockPos, WBSoundEvents.BLOCK_SCULK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);
                    this.allGrowTypeGrower.grow(sculk, level, blockPos, spreadManager.isWorldGen());
                    Direction opposite = direction.getOpposite();

                    for (Direction towards : DIRECTIONS) {
                        if (towards != opposite) {
                            BlockPos targetPos = blockPos.relative(towards);
                            BlockState targetState = level.getBlockState(targetPos);
                            if (targetState.is(this)) {
                                this.spreadAtSamePosition(level, targetState, targetPos, random);
                            }
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public static boolean veinCoversSculkReplaceable(LevelAccessor level, BlockState state, BlockPos pos) {
        if (state.is(WBBlocks.SCULK_VEIN.get())) {
            for (Direction direction : DIRECTIONS) {
                if (hasFace(state, direction) && level.getBlockState(pos.relative(direction)).is(WBBlockTags.SCULK_REPLACEABLE)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean canGrowWithDirection(BlockGetter getter, BlockState state, BlockPos pos, Direction direction) {
        if (this.isFaceSupported(direction) && (!state.is(this) || !hasFace(state, direction))) {
            BlockPos blockPos = pos.relative(direction);
            return canGrowOn(getter, direction, blockPos, getter.getBlockState(blockPos));
        } else {
            return false;
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.getItemInHand().is(WBBlocks.SCULK_VEIN.get().asItem()) || super.canBeReplaced(state, context);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState p_60584_) {
        return PushReaction.DESTROY;
    }

    public static byte directionsToFlag(Collection<Direction> directions) {
        byte flag = 0;

        for (Direction direction : directions) {
            flag = (byte)(flag | 1 << direction.ordinal());
        }

        return flag;
    }

    public static Set<Direction> collectDirections(BlockState state) {
        if (!(state.getBlock() instanceof MultifaceBlock)) {
            return Set.of();
        } else {
            Set<Direction> directions = EnumSet.noneOf(Direction.class);

            for (Direction direction : DIRECTIONS) {
                if (hasFace(state, direction)) {
                    directions.add(direction);
                }
            }

            return directions;
        }
    }

    public static boolean canGrowOn(BlockGetter getter, Direction direction, BlockPos pos, BlockState state) {
        return Block.isFaceFull(state.getBlockSupportShape(getter, pos), direction.getOpposite()) || Block.isFaceFull(state.getCollisionShape(getter, pos), direction.getOpposite());
    }

    public static boolean hasFace(BlockState state, Direction direction) {
        BooleanProperty booleanProperty = MultifaceBlock.getFaceProperty(direction);
        return state.hasProperty(booleanProperty) && state.getValue(booleanProperty);
    }

    class SculkVeinGrowChecker extends VeinGrower.VeinGrowChecker {
        private final VeinGrower.GrowType[] growTypes;

        public SculkVeinGrowChecker(VeinGrower.GrowType... growTypes) {
            super(SculkVeinBlock.this);
            this.growTypes = growTypes;
        }

        @Override
        public boolean canGrow(BlockGetter getter, BlockPos pos, BlockPos growPos, Direction direction, BlockState state) {
            BlockPos blockPos;
            BlockState blockState = getter.getBlockState(growPos.relative(direction));
            boolean flag = blockState.is(WBBlocks.SCULK.get()) || blockState.is(WBBlocks.SCULK_CATALYST.get()) || blockState.is(Blocks.MOVING_PISTON);
            if (flag) {
                return false;
            }
            if (pos.distManhattan(growPos) == 2 && getter.getBlockState(blockPos = pos.relative(direction.getOpposite())).isFaceSturdy(getter, blockPos, direction)) {
                return false;
            }
            FluidState fluidState = state.getFluidState();
            if (!fluidState.isEmpty() && !fluidState.is(Fluids.WATER)) {
                return false;
            }
            return state.getMaterial().isReplaceable() || super.canGrow(getter, pos, growPos, direction, state);
        }

        @Override
        public VeinGrower.GrowType[] getGrowTypes() {
            return this.growTypes;
        }

        @Override
        public boolean canGrow(BlockState state) {
            return !state.is(WBBlocks.SCULK_VEIN.get());
        }
    }
}