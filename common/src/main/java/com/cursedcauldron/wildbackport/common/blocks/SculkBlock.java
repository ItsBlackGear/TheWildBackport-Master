package com.cursedcauldron.wildbackport.common.blocks;

import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

import java.util.Random;

public class SculkBlock extends OreBlock implements SculkSpreadable {
    public SculkBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int spread(SculkSpreadManager.Cursor cursor, LevelAccessor level, BlockPos pos, Random random, SculkSpreadManager manager, boolean shouldConvert) {
        int charge = cursor.getCharge();
        if (charge != 0 && random.nextInt(manager.getSpreadChance()) == 0) {
            BlockPos blockPos = cursor.getPos();
            boolean inRange = blockPos.closerThan(pos, manager.getMaxDistance());
            if (!inRange && shouldNotDecay(level, blockPos)) {
                int chance = manager.getExtraBlockChance();
                if (random.nextInt(chance) < charge) {
                    BlockPos growthPos = blockPos.above();
                    BlockState state = this.getExtraBlockState(level, growthPos, random, manager.isWorldGen());
                    level.setBlock(growthPos, state, 3);
                    level.playSound(null, blockPos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }

                return Math.max(0, charge - chance);
            } else {
                return random.nextInt(manager.getDecayChance()) != 0 ? charge : charge - (inRange ? 1 : getDecay(manager, blockPos, pos, charge));
            }
        } else {
            return charge;
        }
    }

    private static int getDecay(SculkSpreadManager manager, BlockPos source, BlockPos target, int charge) {
        int maxDistance = manager.getMaxDistance();
        float range = Mth.square((float)Math.sqrt(source.distSqr(target)) - (float)maxDistance);
        int distance = Mth.square(24 - maxDistance);
        float spread = Math.min(1.0F, range / (float)distance);
        return Math.max(1, (int)((float)charge * spread * 0.5F));
    }

    private BlockState getExtraBlockState(LevelAccessor level, BlockPos pos, Random random, boolean isWorldGen) {
        BlockState state = random.nextInt(11) == 0 ? WBBlocks.SCULK_SHRIEKER.get().defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, isWorldGen) : Blocks.SCULK_SENSOR.defaultBlockState();
        return state.hasProperty(BlockStateProperties.WATERLOGGED) && !level.getFluidState(pos).isEmpty() ? state.setValue(BlockStateProperties.WATERLOGGED, true) : state;
    }

    private static boolean shouldNotDecay(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos.above());
        if (state.isAir() || state.is(Blocks.WATER) && state.getFluidState().is(Fluids.WATER)) {
            int chance = 0;

            for (BlockPos position : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 2, 4))) {
                BlockState growth = level.getBlockState(position);
                if (growth.is(Blocks.SCULK_SENSOR) || growth.is(WBBlocks.SCULK_SHRIEKER.get())) {
                    ++chance;
                }

                if (chance > 2) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) this.popExperience(level, pos, 1);
    }

    @Override
    public boolean shouldConvertToSpreadable() {
        return true;
    }
}