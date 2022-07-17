package com.cursedcauldron.wildbackport.common.entities.brain;

import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.Frog;
import com.cursedcauldron.wildbackport.common.entities.brain.frog.BiasedLongJumpTask;
import com.cursedcauldron.wildbackport.common.entities.brain.frog.Croak;
import com.cursedcauldron.wildbackport.common.entities.brain.frog.FrogEat;
import com.cursedcauldron.wildbackport.common.entities.brain.frog.LayFrogSpawn;
import com.cursedcauldron.wildbackport.common.entities.brain.frog.WalkTowardsLand;
import com.cursedcauldron.wildbackport.common.entities.brain.frog.WalkTowardsWater;
import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import com.cursedcauldron.wildbackport.common.registry.entity.WBActivities;
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntities;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.cursedcauldron.wildbackport.common.tag.WBBlockTags;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.behavior.LongJumpMidJump;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunIf;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.RunSometimes;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.Random;

public class FrogBrain {
    private static final UniformInt LONG_JUMP_COOLDOWN_RANGE = UniformInt.of(100, 140);

    public static void coolDownLongJump(Frog frog, Random random) {
        frog.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, LONG_JUMP_COOLDOWN_RANGE.sample(random));
    }

    public static Brain<?> create(Brain<Frog> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        addSwimActivities(brain);
        addLaySpawnActivities(brain);
        addTongueActivities(brain);
        addLongJumpActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void addCoreActivities(Brain<Frog> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(new AnimalPanic(2.0F), new LookAtTargetSink(45, 90), new MoveToTargetSink(), new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS)));
    }

    private static void addIdleActivities(Brain<Frog> brain) {
        brain.addActivityWithConditions(Activity.IDLE, ImmutableList.of(Pair.of(0, new RunSometimes<LivingEntity>(new SetEntityLookTarget(EntityType.PLAYER, 6.0F), UniformInt.of(30, 60))), Pair.of(0, new AnimalMakeLove(WBEntities.FROG.get(), 1.0F)), Pair.of(1, new FollowTemptation(entity -> 1.25F)), Pair.of(2, new StartAttacking<>(FrogBrain::isNotBreeding, frog -> frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))), Pair.of(3, new WalkTowardsLand(6, 1.0F)), Pair.of(4, new RunOne<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(new RandomStroll(1.0F), 1), Pair.of(new SetWalkTargetFromLookTarget(1.0F, 3), 1), Pair.of(new Croak(), 3), Pair.of(new RunIf<>(Entity::isOnGround, new DoNothing(5, 20)), 2))))), ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT), Pair.of(WBMemoryModules.IS_IN_WATER.get(), MemoryStatus.VALUE_ABSENT)));
    }

    private static void addSwimActivities(Brain<Frog> brain) {
        brain.addActivityWithConditions(WBActivities.SWIM.get(), ImmutableList.of(Pair.of(0, new RunSometimes<LivingEntity>(new SetEntityLookTarget(EntityType.PLAYER, 6.0F), UniformInt.of(30, 60))), Pair.of(1, new FollowTemptation(entity -> 1.25F)), Pair.of(2, new StartAttacking<>(FrogBrain::isNotBreeding, frog -> frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))), Pair.of(3, new WalkTowardsLand(8, 1.5F)), Pair.of(5, new GateBehavior<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableSet.of(), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.TRY_ALL, ImmutableList.of(Pair.of(new RandomStroll(0.75F), 1), Pair.of(new RandomStroll(1.0F, true), 1), Pair.of(new SetWalkTargetFromLookTarget(1.0F, 3), 1), Pair.of(new RunIf<>(Entity::isInWaterOrBubble, new DoNothing(30, 60)), 5))))), ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT), Pair.of(WBMemoryModules.IS_IN_WATER.get(), MemoryStatus.VALUE_PRESENT)));
    }

    private static void addLaySpawnActivities(Brain<Frog> brain) {
        brain.addActivityWithConditions(WBActivities.LAY_SPAWN.get(), ImmutableList.of(Pair.of(0, new RunSometimes<LivingEntity>(new SetEntityLookTarget(EntityType.PLAYER, 6.0F), UniformInt.of(30, 60))), Pair.of(1, new StartAttacking<>(FrogBrain::isNotBreeding, frog -> frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))), Pair.of(2, new WalkTowardsWater(8, 1.0F)), Pair.of(3, new LayFrogSpawn(WBBlocks.FROGSPAWN.get(), WBMemoryModules.IS_PREGNANT.get())), Pair.of(4, new RunOne<>(ImmutableList.of(Pair.of(new RandomStroll(1.0F), 2), Pair.of(new SetWalkTargetFromLookTarget(1.0F, 3), 1), Pair.of(new Croak(), 2), Pair.of(new RunIf<>(Entity::isOnGround, new DoNothing(5, 20)), 1))))), ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT), Pair.of(WBMemoryModules.IS_PREGNANT.get(), MemoryStatus.VALUE_PRESENT)));
    }

    private static void addLongJumpActivities(Brain<Frog> brain) {
        brain.addActivityWithConditions(Activity.LONG_JUMP, ImmutableList.of(Pair.of(0, new LongJumpMidJump(LONG_JUMP_COOLDOWN_RANGE, WBSoundEvents.FROG_STEP)), Pair.of(1, new BiasedLongJumpTask<>(LONG_JUMP_COOLDOWN_RANGE, 2, 4, 1.5F, frog -> WBSoundEvents.FROG_LONG_JUMP, WBBlockTags.FROG_PREFER_JUMP_TO, 0.5F, state -> state.is(Blocks.LILY_PAD)))), ImmutableSet.of(Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT), Pair.of(WBMemoryModules.IS_IN_WATER.get(), MemoryStatus.VALUE_ABSENT)));
    }

    private static void addTongueActivities(Brain<Frog> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(WBActivities.TONGUE.get(), 0, ImmutableList.of(new StopAttackingIfTargetInvalid<>(), new FrogEat(WBSoundEvents.FROG_TONGUE, WBSoundEvents.FROG_EAT)), MemoryModuleType.ATTACK_TARGET);
    }

    private static boolean isNotBreeding(Frog frog) {
        return !frog.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET);
    }

    public static void updateActivities(Frog frog) {
        frog.getBrain().setActiveActivityToFirstValid(ImmutableList.of(WBActivities.TONGUE.get(), WBActivities.LAY_SPAWN.get(), Activity.LONG_JUMP, WBActivities.SWIM.get(), Activity.IDLE));
    }

    public static Ingredient getTemptItems() {
        return Frog.FOOD;
    }
}