package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ForgetAttackTarget<E extends Mob> extends Behavior<E> {
    private final Predicate<LivingEntity> alternativeCondition;
    private final BiConsumer<E, LivingEntity> forgetCallback;
    private final boolean shouldForgetIfTargetUnreachable;

    public ForgetAttackTarget(Predicate<LivingEntity> alternativeCondition, BiConsumer<E, LivingEntity> forgetCallback, boolean shouldForgetIfTargetUnreachable) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED));
        this.alternativeCondition = alternativeCondition;
        this.forgetCallback = forgetCallback;
        this.shouldForgetIfTargetUnreachable = shouldForgetIfTargetUnreachable;
    }

    @Override
    protected void start(ServerLevel level, E entity, long time) {
        LivingEntity target = this.getAttackTarget(entity);
        if (!entity.canAttack(target)) {
            this.forgetAttackTarget(entity);
        } else if (this.shouldForgetIfTargetUnreachable && cannotReachTarget(entity)) {
            this.forgetAttackTarget(entity);
        } else if (this.isAttackTargetDead(entity)) {
            this.forgetAttackTarget(entity);
        } else if (this.isAttackTargetInAnotherWorld(entity)) {
            this.forgetAttackTarget(entity);
        } else if (this.alternativeCondition.test(this.getAttackTarget(entity))) {
            this.forgetAttackTarget(entity);
        }
    }

    private boolean isAttackTargetInAnotherWorld(E entity) {
        return this.getAttackTarget(entity).level != entity.level;
    }

    private LivingEntity getAttackTarget(E entity) {
        return entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    private static <E extends LivingEntity> boolean cannotReachTarget(E entity) {
        Optional<Long> time = entity.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        return time.isPresent() && entity.level.getGameTime() - time.get() > 200L;
    }

    private boolean isAttackTargetDead(E entity) {
        Optional<LivingEntity> target = entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        return target.isPresent() && !target.get().isAlive();
    }

    protected void forgetAttackTarget(E entity) {
        this.forgetCallback.accept(entity, this.getAttackTarget(entity));
        entity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
    }
}