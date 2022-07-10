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

public class GiveInventoryToLookTarget<E extends LivingEntity & InventoryCarrier> extends Behavior<E> {
    private final Function<LivingEntity, Optional<PositionTracker>> lookTarget;
    private final float speed;

    public GiveInventoryToLookTarget(Function<LivingEntity, Optional<PositionTracker>> lookTarget, float speed) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, WBMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS.get(), MemoryStatus.REGISTERED));
        this.lookTarget = lookTarget;
        this.speed = speed;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return this.hasItemAndTarget(entity);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, E entity, long time) {
        return this.hasItemAndTarget(entity);
    }

    @Override
    protected void start(ServerLevel level, E entity, long time) {
        this.lookTarget.apply(entity).ifPresent(target -> MobUtils.walkTowards(entity, target, this.speed, 3));
    }

    @Override
    protected void tick(ServerLevel level, E entity, long time) {
        Optional<PositionTracker> lookTarget = this.lookTarget.apply(entity);
        if (lookTarget.isPresent()) {
            PositionTracker target = lookTarget.get();
            double distance = target.currentPosition().distanceTo(entity.getEyePosition());
            if (distance < 3.0D) {
                ItemStack stack = entity.getInventory().removeItem(0, 1);
                if (!stack.isEmpty()) {
                    playThrowSound(entity, stack, offsetTarget(target));
                    if (entity instanceof Allay allay) {
                        AllayBrain.getLikedPlayer(allay).ifPresent(player -> this.triggerCriteria(target, stack, player));
                    }

                    entity.getBrain().setMemory(WBMemoryModules.ITEM_PICKUP_COOLDOWN_TICKS.get(), 60);
                }
            }
        }
    }

    private void triggerCriteria(PositionTracker target, ItemStack stack, ServerPlayer player) {
        BlockPos pos = target.currentBlockPosition().below();
//        WBCriteriaTriggers.ALLAY_DROP_ITEM_ON_BLOCK.trigger(player, pos, stack);
    }

    private boolean hasItemAndTarget(E entity) {
        return !entity.getInventory().isEmpty() && this.lookTarget.apply(entity).isPresent();
    }

    private static Vec3 offsetTarget(PositionTracker tracker) {
        return tracker.currentPosition().add(0.0D, 1.0D, 0.0D);
    }

    public static void playThrowSound(LivingEntity entity, ItemStack stack, Vec3 pos) {
        Vec3 velocity = new Vec3(0.2F, 0.3F, 0.2F);
        MobUtils.give(entity, stack, pos, velocity, 0.2F);
        Level level = entity.level;
        if (level.getGameTime() % 7L == 0L && level.random.nextDouble() < 0.9D) {
            float pitch = Util.getRandom(Allay.THROW_SOUND_PITCHES, level.getRandom());
            level.playSound(null, entity, WBSoundEvents.ALLAY_ITEM_THROW, SoundSource.NEUTRAL, 1.0F, pitch);
        }
    }
}