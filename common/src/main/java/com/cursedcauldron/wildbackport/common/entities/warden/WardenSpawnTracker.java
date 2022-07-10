package com.cursedcauldron.wildbackport.common.entities.warden;

import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.entities.access.WardenTracker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

//<>

public class WardenSpawnTracker {
    public static final Codec<WardenSpawnTracker> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ticks_since_last_warning").orElse(0).forGetter(tracker -> {
            return tracker.ticksSinceLastWarning;
        }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("warning_level").orElse(0).forGetter(tracker -> {
            return tracker.warningLevel;
        }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("cooldown_ticks").orElse(0).forGetter(tracker -> {
            return tracker.cooldownTicks;
        })).apply(instance, WardenSpawnTracker::new);
    });

    private int ticksSinceLastWarning;
    private int warningLevel;
    private int cooldownTicks;

    public WardenSpawnTracker(int ticksSinceLastWarning, int warningLevel, int cooldownTicks) {
        this.ticksSinceLastWarning = ticksSinceLastWarning;
        this.warningLevel = warningLevel;
        this.cooldownTicks = cooldownTicks;
    }

    public void tick() {
        if (this.ticksSinceLastWarning >= 12000) {
            this.decreaseWarningCount();
            this.ticksSinceLastWarning = 0;
        } else {
            ++this.ticksSinceLastWarning;
        }

        if (this.cooldownTicks > 0) {
            --this.cooldownTicks;
        }
    }

    public void reset() {
        this.ticksSinceLastWarning = 0;
        this.warningLevel = 0;
        this.cooldownTicks = 0;
    }

    private boolean onCooldown() {
        return this.cooldownTicks > 0;
    }

    public static OptionalInt tryWarn(ServerLevel level, BlockPos pos, ServerPlayer player) {
        if (!hasNearbyWarden(level, pos)) {
            List<ServerPlayer> players = getNearbyPlayers(level, pos);
            if (!players.contains(player)) {
                players.add(player);
            }

            if (players.stream().anyMatch(playerIn -> ((WardenTracker)playerIn).getWardenSpawnTracker().onCooldown())) {
                return OptionalInt.empty();
            }

            Optional<WardenSpawnTracker> optional = players.stream().map(WardenTracker::getWardenSpawnTracker).max(Comparator.comparingInt(tracker -> tracker.warningLevel));
            WardenSpawnTracker tracker = optional.get();
            tracker.increaseWarningLevel();
            players.forEach(playerIn -> ((WardenTracker)playerIn).getWardenSpawnTracker().copyData(tracker));
            return OptionalInt.of(tracker.warningLevel);
        } else {
            return OptionalInt.empty();
        }
    }

    private static boolean hasNearbyWarden(ServerLevel level, BlockPos pos) {
        AABB box = AABB.ofSize(Vec3.atCenterOf(pos), 48.0D, 48.0D, 48.0D);
        return !level.getEntitiesOfClass(Warden.class, box).isEmpty();
    }

    private static List<ServerPlayer> getNearbyPlayers(ServerLevel level, BlockPos pos) {
        Vec3 center = Vec3.atCenterOf(pos);
        Predicate<ServerPlayer> predicate = player -> player.position().closerThan(center, 16.0D);
        return level.getPlayers(predicate.and(LivingEntity::isAlive).and(EntitySelector.NO_SPECTATORS));
    }

    private void increaseWarningLevel() {
        if (!this.onCooldown()) {
            this.ticksSinceLastWarning = 0;
            this.cooldownTicks = 200;
            this.setWarningLevel(this.getWarningLevel() + 1);
        }
    }

    private void decreaseWarningCount() {
        this.setWarningLevel(this.getWarningLevel() - 1);
    }

    public void setWarningLevel(int warningLevel) {
        this.warningLevel = Mth.clamp(warningLevel, 0, 4);
    }

    public int getWarningLevel() {
        return this.warningLevel;
    }

    private void copyData(WardenSpawnTracker tracker) {
        this.ticksSinceLastWarning = tracker.ticksSinceLastWarning;
        this.warningLevel = tracker.warningLevel;
        this.cooldownTicks = tracker.cooldownTicks;
    }

    public void readTag(CompoundTag tag) {
        if (tag.contains("ticksSinceLastWarning", 99)) {
            this.ticksSinceLastWarning = tag.getInt("ticksSinceLastWarning");
            this.warningLevel = tag.getInt("warningCount");
            this.cooldownTicks = tag.getInt("shriekerCooldownTicks");
        }
    }

    public void writeTag(CompoundTag tag) {
        tag.putInt("ticksSinceLastWarning", this.ticksSinceLastWarning);
        tag.putInt("warningCount", this.warningLevel);
        tag.putInt("shriekerCooldownTicks", this.cooldownTicks);
    }
}