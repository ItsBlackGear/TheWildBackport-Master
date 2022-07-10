package com.cursedcauldron.wildbackport.common.entities.brain.frog;

import com.cursedcauldron.wildbackport.common.entities.Frog;
import com.cursedcauldron.wildbackport.common.entities.access.api.Poses;
import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.pathfinder.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//<>

public class FrogEat extends Behavior<Frog> {
    private int eatTick;
    private int moveToTargetTick;
    private final SoundEvent tongueSound;
    private final SoundEvent eatSound;
    private Phase phase = Phase.DONE;

    public FrogEat(SoundEvent tongueSound, SoundEvent eatSound) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), 100);
        this.tongueSound = tongueSound;
        this.eatSound = eatSound;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Frog frog) {
        LivingEntity target = frog.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        boolean flag = this.canMoveToTarget(frog, target);
        if (!flag) {
            frog.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            this.cantReachTarget(frog, target);
        }

        return flag && frog.getPose() != Poses.CROAKING.get() && Frog.isValidFrogFood(target);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Frog frog, long time) {
        return frog.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.phase != Phase.DONE;
    }

    @Override
    protected void start(ServerLevel level, Frog frog, long time) {
        LivingEntity entity = frog.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        BehaviorUtils.lookAtEntity(frog, entity);
        frog.setFrogTarget(entity);
        frog.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(entity.position(), 2.0F, 0));
        this.moveToTargetTick = 10;
        this.phase = Phase.MOVE_TO_TARGET;
    }

    @Override
    protected void stop(ServerLevel level, Frog frog, long time) {
        frog.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        frog.clearFrogTarget();
        frog.setPose(Pose.STANDING);
    }

    private void eat(ServerLevel level, Frog frog) {
        level.playSound(null, frog, this.eatSound, SoundSource.NEUTRAL, 2.0F, 1.0F);
        Optional<Entity> target = frog.getFrogTarget();
        if (target.isPresent()) {
            Entity entity = target.get();
            if (entity.isAlive()) {
                frog.doHurtTarget(entity);
                if (!entity.isAlive()) {
                    entity.spawnAtLocation(dropStack(frog, entity));
                    entity.remove(Entity.RemovalReason.KILLED);
                }
            }
        }
    }

    //Change this to loot table
    private static ItemStack dropStack(Frog frog, Entity entity) {
        if (entity instanceof MagmaCube) {
            return new ItemStack(switch (frog.getVariant()) {
                case TEMPERATE -> WBBlocks.OCHRE_FROGLIGHT.get().asItem();
                case WARM -> WBBlocks.PEARLESCENT_FROGLIGHT.get().asItem();
                case COLD -> WBBlocks.VERDANT_FROGLIGHT.get().asItem();
            });
        } else {
            return new ItemStack(Items.SLIME_BALL);
        }
    }

    @Override
    protected void tick(ServerLevel level, Frog frog, long time) {
        LivingEntity target = frog.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        frog.setFrogTarget(target);
        switch (this.phase) {
            case MOVE_TO_TARGET:
                if (target.distanceTo(frog) < 1.75F) {
                    level.playSound(null, frog, this.tongueSound, SoundSource.NEUTRAL, 2.0F, 1.0F);
                    frog.setPose(Poses.USING_TONGUE.get());
                    target.setDeltaMovement(target.position().vectorTo(frog.position()).normalize().scale(0.75D));
                    this.eatTick = 0;
                    this.phase = Phase.CATCH_ANIMATION;
                } else if (this.moveToTargetTick <= 0) {
                    frog.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(target.position(), 2.0F, 0));
                    this.moveToTargetTick = 10;
                } else {
                    --this.moveToTargetTick;
                }
                break;
            case CATCH_ANIMATION:
                if (this.eatTick++ >= 6) {
                    this.phase = Phase.EAT_ANIMATION;
                    this.eat(level, frog);
                }
                break;
            case EAT_ANIMATION:
                if (this.eatTick >= 10) {
                    this.phase = Phase.DONE;
                } else {
                    ++this.eatTick;
                }
            case DONE:
        }
    }

    private boolean canMoveToTarget(Frog frog, LivingEntity entity) {
        Path path = frog.getNavigation().createPath(entity, 0);
        return path != null && path.getDistToTarget() < 1.75F;
    }

    private void cantReachTarget(Frog frog, LivingEntity entity) {
        List<UUID> targets = frog.getBrain().getMemory(WBMemoryModules.UNREACHABLE_TONGUE_TARGETS.get()).orElseGet(ArrayList::new);
        boolean notTargeting = !targets.contains(entity.getUUID());
        if (targets.size() == 5 && notTargeting) targets.remove(0);

        if (notTargeting) targets.add(entity.getUUID());

        frog.getBrain().setMemoryWithExpiry(WBMemoryModules.UNREACHABLE_TONGUE_TARGETS.get(), targets, 100L);
    }

    enum Phase {
        MOVE_TO_TARGET,
        CATCH_ANIMATION,
        EAT_ANIMATION,
        DONE
    }
}