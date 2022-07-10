package com.cursedcauldron.wildbackport.common.entities.brain.allay;

import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.Allay;
import com.cursedcauldron.wildbackport.common.entities.brain.AllayBrain;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.cursedcauldron.wildbackport.common.utils.MobUtils;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.Function;

//<>

public class GoAndGiveItemsToTarget<E extends LivingEntity & InventoryCarrier> extends Behavior<E> {
    private final Function<LivingEntity, Optional<PositionTracker>> targetPosition;
    private final float speedModifier;

    public GoAndGiveItemsToTarget(Function<LivingEntity, Optional<PositionTracker>> targetPosition, float speedModifier) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, WBMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS.get(), MemoryStatus.REGISTERED));
        this.targetPosition = targetPosition;
        this.speedModifier = speedModifier;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return this.canThrowItemToTarget(entity);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, E entity, long time) {
        return this.canThrowItemToTarget(entity);
    }

    @Override
    protected void start(ServerLevel level, E entity, long time) {
        this.targetPosition.apply(entity).ifPresent(target -> MobUtils.setWalkAndLookTargetMemories(entity, target, this.speedModifier, 3));
    }

    @Override
    protected void tick(ServerLevel level, E entity, long time) {
        Optional<PositionTracker> optional = this.targetPosition.apply(entity);
        if (optional.isPresent()) {
            PositionTracker tracker = optional.get();
            double d0 = tracker.currentPosition().distanceTo(entity.getEyePosition());
            if (d0 < 3.0D) {
                ItemStack itemstack = entity.getInventory().removeItem(0, 1);
                if (!itemstack.isEmpty()) {
                    throwItem(entity, itemstack, getThrowPosition(tracker));
                    if (entity instanceof Allay allay) {
                        AllayBrain.getLikedPlayer(allay).ifPresent(p_217224_ -> this.triggerDropItemOnBlock(tracker, itemstack, p_217224_));
                    }

                    entity.getBrain().setMemory(WBMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS.get(), 60);
                }
            }

        }
    }

    private void triggerDropItemOnBlock(PositionTracker tracker, ItemStack stack, ServerPlayer player) {
        BlockPos pos = tracker.currentBlockPosition().below();
//        WBCriteriaTriggers.ALLAY_DROP_ITEM_ON_BLOCK.trigger(player, pos, stack);
    }

    private boolean canThrowItemToTarget(E entity) {
        return !entity.getInventory().isEmpty() && this.targetPosition.apply(entity).isPresent();
    }

    private static Vec3 getThrowPosition(PositionTracker tracker) {
        return tracker.currentPosition().add(0.0D, 1.0D, 0.0D);
    }

    public static void throwItem(LivingEntity entity, ItemStack stack, Vec3 position) {
        Vec3 vec3 = new Vec3(0.2F, 0.3F, 0.2F);
        MobUtils.throwItem(entity, stack, position, vec3, 0.2F);
        Level level = entity.level;
        if (level.getGameTime() % 7L == 0L && level.random.nextDouble() < 0.9D) {
            float pitch = Util.getRandom(Allay.SOUND_PITCHES, level.getRandom());
            level.playSound(null, entity, WBSoundEvents.ALLAY_ITEM_THROW, SoundSource.NEUTRAL, 1.0F, pitch);
        }
    }
}