package com.cursedcauldron.wildbackport.common;

import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import com.cursedcauldron.wildbackport.common.tag.WBBlockTags;
import com.cursedcauldron.wildbackport.core.api.event.Interactions;
import com.cursedcauldron.wildbackport.core.mixin.access.AxeItemAccessor;
import com.cursedcauldron.wildbackport.core.mixin.access.FireBlockAccessor;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class VanillaIntegration {
    public static void setup() {
        // Flammables
        addFlammable(WBBlocks.MANGROVE_LOG.get(), 5, 5);
        addFlammable(WBBlocks.MANGROVE_WOOD.get(), 5, 5);
        addFlammable(WBBlocks.STRIPPED_MANGROVE_LOG.get(), 5, 5);
        addFlammable(WBBlocks.STRIPPED_MANGROVE_WOOD.get(), 5, 5);
        addFlammable(WBBlocks.MANGROVE_PLANKS.get(), 5, 20);
        addFlammable(WBBlocks.MANGROVE_STAIRS.get(), 5, 20);
        addFlammable(WBBlocks.MANGROVE_SLAB.get(), 5, 20);
        addFlammable(WBBlocks.MANGROVE_FENCE.get(), 5, 20);
        addFlammable(WBBlocks.MANGROVE_FENCE_GATE.get(), 5, 20);
        addFlammable(WBBlocks.MANGROVE_ROOTS.get(), 5, 20);
        addFlammable(WBBlocks.MANGROVE_LEAVES.get(), 30, 60);

        // Compostables
        addCompostable(WBBlocks.MANGROVE_LEAVES.get(), 0.3F);
        addCompostable(WBBlocks.MANGROVE_PROPAGULE.get(), 0.3F);
        addCompostable(WBBlocks.MANGROVE_ROOTS.get(), 0.3F);

        // Strippables
        addStrippable(WBBlocks.MANGROVE_LOG.get(), WBBlocks.STRIPPED_MANGROVE_LOG.get());
        addStrippable(WBBlocks.MANGROVE_WOOD.get(), WBBlocks.STRIPPED_MANGROVE_WOOD.get());

        // Turning Dirt into Mud
        Interactions.addRightClick(context -> {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            Player player = context.getPlayer();
            ItemStack stack = context.getItemInHand();
            BlockState state = level.getBlockState(pos);
            if (player != null && context.getClickedFace() != Direction.DOWN && state.is(WBBlockTags.CONVERTABLE_TO_MUD) && PotionUtils.getPotion(stack) == Potions.WATER) {
                level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.setItemInHand(context.getHand(), ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));

                if (!level.isClientSide()) {
                    for (int i = 0; i < 5; i++) {
                        ((ServerLevel)level).sendParticles(ParticleTypes.SPLASH, (double)pos.getX() + level.random.nextDouble(), pos.getY() + 1, (double)pos.getZ() + level.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    }
                }

                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(GameEvent.FLUID_PLACE, pos);
                level.setBlockAndUpdate(pos, WBBlocks.MUD.get().defaultBlockState());
                return InteractionResult.sidedSuccess(level.isClientSide());
            } else {
                return InteractionResult.PASS;
            }
        });

        DispenserBlock.registerBehavior(Items.POTION, new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior behavior = new DefaultDispenseItemBehavior();

            @Override protected ItemStack execute(BlockSource source, ItemStack stack) {
                if (PotionUtils.getPotion(stack) != Potions.WATER) {
                    return this.behavior.dispense(source, stack);
                } else {
                    ServerLevel level = source.getLevel();
                    BlockPos sourcePos = source.getPos();
                    BlockPos dispenserPos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
                    if (!level.getBlockState(dispenserPos).is(WBBlockTags.CONVERTABLE_TO_MUD)) {
                        return this.behavior.dispense(source, stack);
                    } else {
                        if (!level.isClientSide()) {
                            for (int i = 0; i < 5; i++) {
                                level.sendParticles(ParticleTypes.SPLASH, (double)sourcePos.getX() + level.random.nextDouble(), sourcePos.getY() + 1, (double)sourcePos.getZ() + level.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                            }
                        }

                        level.playSound(null, sourcePos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.gameEvent(GameEvent.FLUID_PLACE, sourcePos);
                        level.setBlockAndUpdate(dispenserPos, WBBlocks.MUD.get().defaultBlockState());
                        return new ItemStack(Items.GLASS_BOTTLE);
                    }
                }
            }
        });
    }

    public static void addFlammable(Block block, int flameOdds, int burnOdds) {
        ((FireBlockAccessor)Blocks.FIRE).callSetFlammable(block, flameOdds, burnOdds);
    }

    public static void addCompostable(ItemLike item, float chance) {
        ComposterBlock.COMPOSTABLES.put(item.asItem(), chance);
    }

    public static void addStrippable(Block from, Block to) {
        AxeItemAccessor.setSTRIPPABLES(Maps.newHashMap(AxeItemAccessor.getSTRIPPABLES()));
        AxeItemAccessor.getSTRIPPABLES().put(from, to);
    }
}