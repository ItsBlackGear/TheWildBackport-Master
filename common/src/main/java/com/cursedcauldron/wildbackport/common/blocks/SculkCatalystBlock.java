package com.cursedcauldron.wildbackport.common.blocks;

import com.cursedcauldron.wildbackport.client.registry.WBParticleTypes;
import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.blocks.entity.SculkCatalystBlockEntity;
import com.cursedcauldron.wildbackport.common.registry.WBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

//<>

public class SculkCatalystBlock extends BaseEntityBlock {
    public static final BooleanProperty BLOOM = BlockProperties.BLOOM;

    public SculkCatalystBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BLOOM, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BLOOM);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (state.getValue(BLOOM)) level.setBlock(pos, state.setValue(BLOOM, false), 3);
    }

    public static void bloom(ServerLevel level, BlockPos pos, BlockState state, Random random) {
        level.setBlock(pos, state.setValue(BLOOM, true), 3);
        level.scheduleTick(pos, state.getBlock(), 8);
        level.sendParticles(WBParticleTypes.SCULK_SOUL.get(), (double)pos.getX() + 0.5D, (double)pos.getY() + 1.15D, (double)pos.getZ() + 0.5D, 2, 0.2D, 0.0D, 0.2D, 0.0D);
        level.playSound(null, pos, WBSoundEvents.BLOCK_SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 2.0F, 0.6F + random.nextFloat() * 0.4F);
    }

    @Nullable @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SculkCatalystBlockEntity(pos, state);
    }

    @Nullable @Override
    public <T extends BlockEntity> GameEventListener getListener(Level level, T type) {
        return type instanceof SculkCatalystBlockEntity catalyst ? catalyst : null;
    }

    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, WBBlockEntities.SCULK_CATALYST.get(), SculkCatalystBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack) {
        super.spawnAfterBreak(state, level, pos, stack);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            this.popExperience(level, pos, 20);
        }
    }
}