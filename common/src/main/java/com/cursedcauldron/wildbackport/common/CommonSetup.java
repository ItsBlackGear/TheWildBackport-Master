package com.cursedcauldron.wildbackport.common;

import com.cursedcauldron.wildbackport.common.entities.Allay;
import com.cursedcauldron.wildbackport.common.entities.Frog;
import com.cursedcauldron.wildbackport.common.entities.Tadpole;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntities;
import com.cursedcauldron.wildbackport.common.registry.worldgen.WBWorldGeneration;
import com.cursedcauldron.wildbackport.common.tag.WBBlockTags;
import com.cursedcauldron.wildbackport.core.api.MobRegistry;
import com.cursedcauldron.wildbackport.core.api.event.Interactions;
import com.cursedcauldron.wildbackport.core.api.worldgen.BiomeModifier;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class CommonSetup {
    /**
     * Runs features at initializing
     */
    public static void onCommon() {
        // Entity Attributes
        MobRegistry.registerAttributes(WBEntities.ALLAY, Allay::createAttributes);
        MobRegistry.registerAttributes(WBEntities.FROG, Frog::createAttributes);
        MobRegistry.registerAttributes(WBEntities.TADPOLE, Tadpole::createAttributes);
        MobRegistry.registerAttributes(WBEntities.WARDEN, Warden::createAttributes);

    }

    /**
     * Runs features post bootstrap
     */
    public static void onPostCommon() {
        WBWorldGeneration.bootstrap();
        BiomeModifier.setup();
        VanillaInteraction.setup();
    }
}