package com.cursedcauldron.wildbackport.common.blocks;

import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

//<>

public class MangrovePropaguleBlock extends SaplingBlock implements SimpleWaterloggedBlock {
    private static final VoxelShape[] SHAPES = new VoxelShape[]{Block.box(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.box(7.0D, 10.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.box(7.0D, 7.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.box(7.0D, 3.0D, 7.0D, 9.0D, 16.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
    public static final IntegerProperty AGE = BlockProperties.AGE_4;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;

    public MangrovePropaguleBlock(Properties properties) {
        super(new MangroveTreeGrower(0.85F), properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0).setValue(AGE, 0).setValue(WATERLOGGED, false).setValue(HANGING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE, AGE, WATERLOGGED, HANGING);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter getter, BlockPos pos) {
        return super.mayPlaceOn(state, getter, pos) || state.is(Blocks.CLAY);
    }

    @Override @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return super.getStateForPlacement(context).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER).setValue(AGE, 4);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(getter, pos);
        VoxelShape shape = !state.getValue(HANGING) ? SHAPES[4] : SHAPES[state.getValue(AGE)];
        return shape.move(offset.x, offset.y, offset.z);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        return isHanging(state) ? reader.getBlockState(pos.above()).is(WBBlocks.MANGROVE_LEAVES.get()) : super.canSurvive(state, reader, pos);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor accessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) accessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(accessor));
        return direction == Direction.UP && !state.canSurvive(accessor, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, accessor, pos, neighborPos);
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (!isHanging(state)) {
            if (random.nextInt(7) == 0) this.advanceTree(level, pos, state, random);
        } else {
            if (!ageAtMax(state)) level.setBlock(pos, state.cycle(AGE), 2);
        }
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter getter, BlockPos pos, BlockState state, boolean flag) {
        return !isHanging(state) || !ageAtMax(state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
        return isHanging(state) ? !ageAtMax(state) : super.isBonemealSuccess(level, random, pos, state);
    }

    @Override
    public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
        if (isHanging(state) && !ageAtMax(state)) {
            level.setBlock(pos, state.cycle(AGE), 2);
        } else {
            super.performBonemeal(level, random, pos, state);
        }
    }

    private static boolean isHanging(BlockState state) {
        return state.getValue(HANGING);
    }

    private static boolean ageAtMax(BlockState state) {
        return state.getValue(AGE) == 4;
    }

    public static BlockState createPropagule() {
        return createPropagule(0);
    }

    public static BlockState createPropagule(int age) {
        return WBBlocks.MANGROVE_PROPAGULE.get().defaultBlockState().setValue(HANGING, true).setValue(AGE, age);
    }
}