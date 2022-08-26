package com.cursedcauldron.wildbackport.common.entities.brain;

import com.cursedcauldron.wildbackport.common.entities.Allay;
import com.cursedcauldron.wildbackport.common.entities.brain.allay.FlyingRandomStroll;
import com.cursedcauldron.wildbackport.common.entities.brain.allay.GiveInventoryToLookTarget;
import com.cursedcauldron.wildbackport.common.entities.brain.allay.StayCloseToTarget;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.GoToWantedItem;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.RunSometimes;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;
import java.util.UUID;

//<>

public class AllayBrain {
    public static Brain<?> create(Brain<Allay> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void addCoreActivities(Brain<Allay> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(new Swim(0.8F), new AnimalPanic(2.5F), new LookAtTargetSink(45, 90), new MoveToTargetSink(), new CountDownCooldownTicks(WBMemoryModules.LIKED_NOTEBLOCK_COOLDOWN_TICKS.get()), new CountDownCooldownTicks(WBMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS.get())));
    }

    private static void addIdleActivities(Brain<Allay> brain) {
        brain.addActivityWithConditions(Activity.IDLE, ImmutableList.of(Pair.of(0, new GoToWantedItem<>(entity -> {
            return true;
        }, 1.75F, true, 32)), Pair.of(1, new GiveInventoryToLookTarget<>(AllayBrain::getLookTarget, 2.25F)), Pair.of(2, new StayCloseToTarget<>(AllayBrain::getLookTarget, 4, 16, 2.25F)), Pair.of(3, new RunSometimes<>(new SetEntityLookTarget(entity -> {
            return true;
        }, 6.0F), UniformInt.of(30, 60))), Pair.of(4, new RunOne<>(ImmutableList.of(Pair.of(new FlyingRandomStroll(1.0F), 2), Pair.of(new SetWalkTargetFromLookTarget(1.0F, 3), 2), Pair.of(new DoNothing(30, 60), 1))))), ImmutableSet.of());
    }

    public static void updateActivities(Allay allay) {
        allay.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    public static void rememberNoteBlock(LivingEntity entity, BlockPos pos) {
        Brain<?> brain = entity.getBrain();
        GlobalPos globalPos = GlobalPos.of(entity.getLevel().dimension(), pos);
        Optional<GlobalPos> noteblock = brain.getMemory(WBMemoryModules.LIKED_NOTEBLOCK.get());
        if (noteblock.isEmpty()) {
            brain.setMemory(WBMemoryModules.LIKED_NOTEBLOCK.get(), globalPos);
            brain.setMemory(WBMemoryModules.LIKED_NOTEBLOCK_COOLDOWN_TICKS.get(), 600);
        } else if (noteblock.get().equals(globalPos)) {
            brain.setMemory(WBMemoryModules.LIKED_NOTEBLOCK_COOLDOWN_TICKS.get(), 600);
        }
    }

    private static Optional<PositionTracker> getLookTarget(LivingEntity entity) {
        Brain<?> brain = entity.getBrain();
        Optional<GlobalPos> likedNoteBlock = brain.getMemory(WBMemoryModules.LIKED_NOTEBLOCK.get());
        if (likedNoteBlock.isPresent()) {
            GlobalPos pos = likedNoteBlock.get();
            if (shouldGoTowardsNoteBlock(entity, brain, pos)) return Optional.of(new BlockPosTracker(pos.pos().above()));
            brain.eraseMemory(WBMemoryModules.LIKED_NOTEBLOCK.get());
        }

        return getLikedLookTarget(entity);
    }

    private static boolean shouldGoTowardsNoteBlock(LivingEntity entity, Brain<?> brain, GlobalPos pos) {
        Optional<Integer> cooldownTicks = brain.getMemory(WBMemoryModules.LIKED_NOTEBLOCK_COOLDOWN_TICKS.get());
        Level level = entity.getLevel();
        return level.dimension() == pos.dimension() && level.getBlockState(pos.pos()).is(Blocks.NOTE_BLOCK) && cooldownTicks.isPresent();
    }

    private static Optional<PositionTracker> getLikedLookTarget(LivingEntity entity) {
        return getLikedPlayer(entity).map(player -> new EntityTracker(player, true));
    }

    public static Optional<ServerPlayer> getLikedPlayer(LivingEntity entity) {
        Level level = entity.getLevel();
        if (!level.isClientSide && level instanceof ServerLevel server) {
            Optional<UUID> likedPlayer = entity.getBrain().getMemory(WBMemoryModules.LIKED_PLAYER.get());
            if (likedPlayer.isPresent()) {
                if (server.getEntity(likedPlayer.get()) instanceof ServerPlayer player) {
                    if ((player.gameMode.isSurvival() || player.gameMode.isCreative()) && player.closerThan(entity, 64.0D)) return Optional.of(player);
                }

                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}