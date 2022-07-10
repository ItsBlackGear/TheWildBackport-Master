package com.cursedcauldron.wildbackport.common.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MobUtils {
    public static void addEffectToPlayersWithinDistance(ServerLevel level, @Nullable Entity entity, Vec3 position, double distance, MobEffectInstance instance, int duration) {
        MobEffect mobeffect = instance.getEffect();
        List<ServerPlayer> players = level.getPlayers(player -> player.gameMode.isSurvival() && (entity == null || !entity.isAlliedTo(player)) && position.closerThan(player.position(), distance) && (!player.hasEffect(mobeffect) || player.getEffect(mobeffect).getAmplifier() < instance.getAmplifier() || player.getEffect(mobeffect).getDuration() < duration));
        players.forEach(player -> player.addEffect(new MobEffectInstance(instance), entity));
    }

    public static boolean closerThan(Entity source, Entity entity, double xzRange, double yRange) {
        double x = entity.getX() - source.getX();
        double y = entity.getY() - source.getY();
        double z = entity.getZ() - source.getZ();
        return Mth.lengthSquared(x, z) < Mth.square(xzRange) && Mth.square(y) < Mth.square(yRange);
    }

    public static void walkTowards(LivingEntity entity, PositionTracker target, float speed, int closeEnough) {
        WalkTarget walkTarget = new WalkTarget(target, speed, closeEnough);
        entity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, target);
        entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walkTarget);
    }

    public static void give(LivingEntity entity, ItemStack stack, Vec3 target, Vec3 velocity, float yOffset) {
        double y = entity.getEyeY() - (double)yOffset;
        ItemEntity item = new ItemEntity(entity.level, entity.getX(), y, entity.getZ(), stack);
        item.setThrower(entity.getUUID());
        Vec3 distance = target.subtract(entity.position());
        distance = distance.normalize().multiply(velocity.x, velocity.y, velocity.z);
        item.setDeltaMovement(distance);
        item.setDefaultPickUpDelay();
        entity.level.addFreshEntity(item);
    }
}