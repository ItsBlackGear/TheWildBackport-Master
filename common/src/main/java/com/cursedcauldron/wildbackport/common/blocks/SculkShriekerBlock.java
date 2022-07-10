package com.cursedcauldron.wildbackport.common.blocks;

import com.cursedcauldron.wildbackport.common.blocks.entity.SculkShriekerBlockEntity;
import com.cursedcauldron.wildbackport.common.registry.WBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

//<>

public class SculkShriekerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty SHRIEKING = BlockProperties.SHRIEKING;
    public static final BooleanProperty CAN_SUMMON = BlockProperties.CAN_SUMMON;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    public static final double TOP_Y = SHAPE.max(Direction.Axis.Y);

    public SculkShriekerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SHRIEKING, false).setValue(WATERLOGGED, false).setValue(CAN_SUMMON, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHRIEKING);
        builder.add(WATERLOGGED);
        builder.add(CAN_SUMMON);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level instanceof ServerLevel server) {
            ServerPlayer player = SculkShriekerBlockEntity.tryGetPlayer(entity);
            if (player != null) {
                server.getBlockEntity(pos, WBBlockEntities.SCULK_SHRIEKER.get()).ifPresent(shrieker -> shrieker.tryShriek(server, player));
            }
        }

        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (level instanceof ServerLevel server) {
            if (state.getValue(SHRIEKING) && !state.is(newState.getBlock())) {
                server.getBlockEntity(pos, WBBlockEntities.SCULK_SHRIEKER.get()).ifPresent(shrieker -> shrieker.tryRespond(server));
            }
        }

        super.onRemove(state, level, pos, newState, moving);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (state.getValue(SHRIEKING)) {
            level.setBlock(pos, state.setValue(SHRIEKING, false), 3);
            level.getBlockEntity(pos, WBBlockEntities.SCULK_SHRIEKER.get()).ifPresent(shrieker -> shrieker.tryRespond(level));
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Nullable @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SculkShriekerBlockEntity(pos, state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor level, BlockPos pos, BlockPos newPos) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, direction, newState, level, pos, newPos);
    }

    @Nullable @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) this.popExperience(level, pos, 5);
    }

    @Nullable @Override
    public <T extends BlockEntity> GameEventListener getListener(Level level, T type) {
        return type instanceof SculkShriekerBlockEntity shrieker ? shrieker.getListener() : null;
    }

    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return !level.isClientSide ? createTickerHelper(type, WBBlockEntities.SCULK_SHRIEKER.get(), (levelIn, posIn, stateIn, shrieker) -> shrieker.getListener().tick(levelIn)) : null;
    }
}