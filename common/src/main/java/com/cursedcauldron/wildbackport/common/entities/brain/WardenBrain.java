package com.cursedcauldron.wildbackport.common.entities.brain;

import com.cursedcauldron.wildbackport.common.entities.brain.warden.ForgetAttackTargetTask;
import com.cursedcauldron.wildbackport.common.entities.brain.warden.Digging;
import com.cursedcauldron.wildbackport.common.entities.brain.warden.Dismount;
import com.cursedcauldron.wildbackport.common.entities.brain.warden.Emerging;
import com.cursedcauldron.wildbackport.common.entities.brain.warden.GoToTargetLocation;
import com.cursedcauldron.wildbackport.common.entities.brain.warden.Roar;
import com.cursedcauldron.wildbackport.common.entities.brain.warden.SetRoarTarget;
import com.cursedcauldron.wildbackport.common.entities.brain.warden.SetWardenLookTarget;
import com.cursedcauldron.wildbackport.common.entities.brain.warden.Sniffing;
import com.cursedcauldron.wildbackport.common.entities.brain.warden.SonicBoom;
import com.cursedcauldron.wildbackport.common.entities.brain.warden.TryToSniff;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.registry.entity.WBActivities;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.cursedcauldron.wildbackport.common.registry.entity.WBSensorTypes;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import java.util.List;

//<>

public class WardenBrain {
    private static final int DIGGING_DURATION   = Mth.ceil(100.0F);
    public static final int EMERGE_DURATION     = Mth.ceil(133.59999F);
    public static final int ROAR_DURATION       = Mth.ceil(84.0F);
    private static final int SNIFFING_DURATION  = Mth.ceil(83.2F);
    private static final List<SensorType<? extends Sensor<? super Warden>>> SENSORS = List.of(SensorType.NEAREST_PLAYERS, WBSensorTypes.WARDEN_ENTITY_SENSOR.get());
    private static final List<MemoryModuleType<?>> MEMORIES = List.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_ATTACKABLE, WBMemoryModules.ROAR_TARGET.get(), WBMemoryModules.DISTURBANCE_LOCATION.get(), WBMemoryModules.RECENT_PROJECTILE.get(), WBMemoryModules.IS_SNIFFING.get(), WBMemoryModules.IS_EMERGING.get(), WBMemoryModules.ROAR_SOUND_DELAY.get(), WBMemoryModules.DIG_COOLDOWN.get(), WBMemoryModules.ROAR_SOUND_COOLDOWN.get(), WBMemoryModules.SNIFF_COOLDOWN.get(), WBMemoryModules.TOUCH_COOLDOWN.get(), WBMemoryModules.VIBRATION_COOLDOWN.get(), WBMemoryModules.SONIC_BOOM_COOLDOWN.get(), WBMemoryModules.SONIC_BOOM_SOUND_COOLDOWN.get(), WBMemoryModules.SONIC_BOOM_SOUND_DELAY.get());
    private static final Behavior<Warden> DIG_COOLDOWN_SETTER = new Behavior<>(ImmutableMap.of(WBMemoryModules.DIG_COOLDOWN.get(), MemoryStatus.REGISTERED)) {
        @Override protected void start(ServerLevel level, Warden warden, long time) {
            setDigCooldown(warden);
        }
    };

    public static void updateActivity(Warden warden) {
        warden.getBrain().setActiveActivityToFirstValid(ImmutableList.of(WBActivities.EMERGE.get(), WBActivities.DIG.get(), WBActivities.ROAR.get(), Activity.FIGHT, WBActivities.INVESTIGATE.get(), WBActivities.SNIFF.get(), Activity.IDLE));
    }

    public static Brain<?> makeBrain(Warden warden, Dynamic<?> dynamic) {
        Brain.Provider<Warden> provider = Brain.provider(MEMORIES, SENSORS);
        Brain<Warden> brain = provider.makeBrain(dynamic);
        initCoreActivity(brain);
        initEmergeActivity(brain);
        initDiggingActivity(brain);
        initIdleActivity(brain);
        initRoarActivity(brain);
        initFightActivity(warden, brain);
        initInvestigateActivity(brain);
        initSniffingActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<Warden> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(new Swim(0.8F), new SetWardenLookTarget(), new LookAtTargetSink(45, 90), new MoveToTargetSink()));
    }

    private static void initEmergeActivity(Brain<Warden> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(WBActivities.EMERGE.get(), 5, ImmutableList.of(new Emerging<>(EMERGE_DURATION)), WBMemoryModules.IS_EMERGING.get());
    }

    private static void initDiggingActivity(Brain<Warden> brain) {
        brain.addActivityWithConditions(WBActivities.DIG.get(), ImmutableList.of(Pair.of(0, new Dismount()), Pair.of(1, new Digging<>(DIGGING_DURATION))), ImmutableSet.of(Pair.of(WBMemoryModules.ROAR_TARGET.get(), MemoryStatus.VALUE_ABSENT), Pair.of(WBMemoryModules.DIG_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT)));
    }

    private static void initIdleActivity(Brain<Warden> brain) {
        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(new SetRoarTarget<>(Warden::getPrimeSuspect), new TryToSniff(), new RunOne<>(ImmutableMap.of(WBMemoryModules.IS_SNIFFING.get(), MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(new RandomStroll(0.5F), 2), Pair.of(new DoNothing(30, 60), 1)))));
    }

    private static void initInvestigateActivity(Brain<Warden> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(WBActivities.INVESTIGATE.get(), 5, ImmutableList.of(new SetRoarTarget<>(Warden::getPrimeSuspect), new GoToTargetLocation<>(WBMemoryModules.DISTURBANCE_LOCATION.get(), 2, 0.7F)), WBMemoryModules.DISTURBANCE_LOCATION.get());
    }

    private static void initSniffingActivity(Brain<Warden> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(WBActivities.SNIFF.get(), 5, ImmutableList.of(new SetRoarTarget<>(Warden::getPrimeSuspect), new Sniffing<>(SNIFFING_DURATION)), WBMemoryModules.IS_SNIFFING.get());
    }

    private static void initRoarActivity(Brain<Warden> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(WBActivities.ROAR.get(), 10, ImmutableList.of(new Roar()), WBMemoryModules.ROAR_TARGET.get());
    }

    private static void initFightActivity(Warden warden, Brain<Warden> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(DIG_COOLDOWN_SETTER, new ForgetAttackTargetTask<>(target -> {
            return !warden.getAngriness().isAngry() || !warden.isValidTarget(target);
        }, WardenBrain::onTargetInvalid, false), new SetEntityLookTarget(target -> {
            return isTarget(warden, target);
        }, (float)warden.getAttributeValue(Attributes.FOLLOW_RANGE)), new SetWalkTargetFromAttackTargetIfTargetOutOfReach(1.2F), new SonicBoom(), new MeleeAttack(18)), MemoryModuleType.ATTACK_TARGET);
    }

    private static boolean isTarget(Warden warden, LivingEntity entity) {
        return warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter(target -> target == entity).isPresent();
    }

    private static void onTargetInvalid(Warden warden, LivingEntity entity) {
        if (!warden.isValidTarget(entity)) warden.removeSuspect(entity);
        setDigCooldown(warden);
    }

    public static void setDigCooldown(LivingEntity entity) {
        if (entity.getBrain().hasMemoryValue(WBMemoryModules.DIG_COOLDOWN.get())) entity.getBrain().setMemoryWithExpiry(WBMemoryModules.DIG_COOLDOWN.get(), Unit.INSTANCE, 1200L);
    }

    public static void setDisturbanceLocation(Warden warden, BlockPos pos) {
        if (warden.level.getWorldBorder().isWithinBounds(pos) && warden.getPrimeSuspect().isEmpty() && warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()) {
            setDigCooldown(warden);
            warden.getBrain().setMemoryWithExpiry(WBMemoryModules.SNIFF_COOLDOWN.get(), Unit.INSTANCE, 100L);
            warden.getBrain().setMemoryWithExpiry(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos), 100L);
            warden.getBrain().setMemoryWithExpiry(WBMemoryModules.DISTURBANCE_LOCATION.get(), pos, 100L);
            warden.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
    }
}