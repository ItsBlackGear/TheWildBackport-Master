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
    private final Predicate<LivingEntity> stopAttackingWhen;
    private final BiConsumer<E, LivingEntity> onTargetErased;
    private final boolean canGrowTiredOfTryingToReachTarget;

    public ForgetAttackTarget(Predicate<LivingEntity> stopAttackingWhen, BiConsumer<E, LivingEntity> onTargetEased, boolean canGrowTiredOfTryingToReachTarget) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED));
        this.stopAttackingWhen = stopAttackingWhen;
        this.onTargetErased = onTargetEased;
        this.canGrowTiredOfTryingToReachTarget = canGrowTiredOfTryingToReachTarget;
    }

    @Override
    protected void start(ServerLevel level, E entity, long time) {
        LivingEntity target = this.getAttackTarget(entity);
        if (!entity.canAttack(target)) {
            this.clearAttackTarget(entity);
        } else if (this.canGrowTiredOfTryingToReachTarget && isTiredOfTryingToReachTarget(entity)) {
            this.clearAttackTarget(entity);
        } else if (this.isCurrentTargetDeadOrRemoved(entity)) {
            this.clearAttackTarget(entity);
        } else if (this.isCurrentTargetInDifferentLevel(entity)) {
            this.clearAttackTarget(entity);
        } else if (this.stopAttackingWhen.test(this.getAttackTarget(entity))) {
            this.clearAttackTarget(entity);
        }
    }

    private boolean isCurrentTargetInDifferentLevel(E entity) {
        return this.getAttackTarget(entity).level != entity.level;
    }

    private LivingEntity getAttackTarget(E entity) {
        return entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    private static <E extends LivingEntity> boolean isTiredOfTryingToReachTarget(E entity) {
        Optional<Long> time = entity.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        return time.isPresent() && entity.level.getGameTime() - time.get() > 200L;
    }

    private boolean isCurrentTargetDeadOrRemoved(E entity) {
        Optional<LivingEntity> target = entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        return target.isPresent() && !target.get().isAlive();
    }

    protected void clearAttackTarget(E entity) {
        this.onTargetErased.accept(entity, this.getAttackTarget(entity));
        entity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
    }
}