package com.cursedcauldron.wildbackport.common.entities.warden;

import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.SerializableUUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//<>

public class WardenAngerManager {
    private int updateTimer = Mth.randomBetweenInclusive(new Random(System.nanoTime()), 0, 2);
    private static final Codec<Pair<UUID, Integer>> SUSPECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(SerializableUUID.CODEC.fieldOf("uuid").forGetter(Pair::getFirst), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("anger").forGetter(Pair::getSecond)).apply(instance, Pair::of));
    private final Predicate<Entity> validTarget;
    protected final ArrayList<Entity> suspects;
    private final SuspectComparator suspectComparator;
    protected final Object2IntMap<Entity> suspectsToAngerLevel;
    protected final Object2IntMap<UUID> suspectUuidsToAngerLevel;

    public static Codec<WardenAngerManager> createCodec(Predicate<Entity> validTarget) {
        return RecordCodecBuilder.create(instance -> instance.group(SUSPECT_CODEC.listOf().fieldOf("suspects").orElse(Collections.emptyList()).forGetter(WardenAngerManager::getSuspects)).apply(instance, suspects -> new WardenAngerManager(validTarget, suspects)));
    }

    public WardenAngerManager(Predicate<Entity> validTarget, List<Pair<UUID, Integer>> suspects) {
        this.validTarget = validTarget;
        this.suspects = new ArrayList<>();
        this.suspectComparator = new SuspectComparator(this);
        this.suspectsToAngerLevel = new Object2IntOpenHashMap<>();
        this.suspectUuidsToAngerLevel = new Object2IntOpenHashMap<>(suspects.size());
        suspects.forEach(pair -> this.suspectUuidsToAngerLevel.put(pair.getFirst(), pair.getSecond()));
    }

    private List<Pair<UUID, Integer>> getSuspects() {
        return Streams.concat(this.suspects.stream().map(suspect -> Pair.of(suspect.getUUID(), this.suspectsToAngerLevel.getInt(suspect))), this.suspectUuidsToAngerLevel.object2IntEntrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getIntValue()))).collect(Collectors.toList());
    }

    public void tick(ServerLevel world, Predicate<Entity> suspectPredicate) {
        --this.updateTimer;
        if (this.updateTimer <= 0) {
            this.updateSuspectsMap(world);
            this.updateTimer = 2;
        }

        Iterator<Object2IntMap.Entry<UUID>> uuidAngerLevels = this.suspectUuidsToAngerLevel.object2IntEntrySet().iterator();
        while (uuidAngerLevels.hasNext()) {
            Object2IntMap.Entry<UUID> entry = uuidAngerLevels.next();
            int level = entry.getIntValue();
            if (level <= 1) {
                uuidAngerLevels.remove();
            } else {
                entry.setValue(level - 1);
            }
        }

        Iterator<Object2IntMap.Entry<Entity>> angerLevels = this.suspectsToAngerLevel.object2IntEntrySet().iterator();
        while (angerLevels.hasNext()) {
            Object2IntMap.Entry<Entity> entry = angerLevels.next();
            int level = entry.getIntValue();
            Entity entity = entry.getKey();
            Entity.RemovalReason reason = entity.getRemovalReason();
            if (level > 1 && suspectPredicate.test(entity) && reason == null) {
                entry.setValue(level - 1);
            } else {
                this.suspects.remove(entity);
                angerLevels.remove();
                if (level > 1 && reason != null) {
                    switch (reason) {
                        case CHANGED_DIMENSION, UNLOADED_TO_CHUNK, UNLOADED_WITH_PLAYER -> this.suspectUuidsToAngerLevel.put(entity.getUUID(), level - 1);
                    }
                }
            }
        }

        this.suspects.sort(this.suspectComparator);
    }

    private void updateSuspectsMap(ServerLevel world) {
        Iterator<Object2IntMap.Entry<UUID>> angerLevels = this.suspectUuidsToAngerLevel.object2IntEntrySet().iterator();

        while (angerLevels.hasNext()) {
            Object2IntMap.Entry<UUID> entry = angerLevels.next();
            int level = entry.getIntValue();
            Entity entity = world.getEntity(entry.getKey());
            if (entity != null) {
                this.suspectsToAngerLevel.put(entity, level);
                this.suspects.add(entity);
                angerLevels.remove();
            }
        }

        this.suspects.sort(this.suspectComparator);
    }

    public int increaseAngerAt(Entity entity, int amount) {
        boolean canAffectAnger = !this.suspectsToAngerLevel.containsKey(entity);
        int angerValue = this.suspectsToAngerLevel.computeInt(entity, (suspect, anger) -> Math.min(150, (anger == null ? 0 : anger) + amount));
        if (canAffectAnger) {
            int anger = this.suspectUuidsToAngerLevel.removeInt(entity.getUUID());
            this.suspectsToAngerLevel.put(entity, angerValue += anger);
            this.suspects.add(entity);
        }

        this.suspects.sort(this.suspectComparator);
        return angerValue;
    }

    public void removeSuspect(Entity entity) {
        this.suspectsToAngerLevel.removeInt(entity);
        this.suspects.remove(entity);
    }

    @Nullable
    private Entity getSuspect() {
        return this.suspects.stream().filter(this.validTarget).findFirst().orElse(null);
    }

    public int getPrimeSuspectAnger() {
        return this.suspectsToAngerLevel.getInt(this.getSuspect());
    }

    public Optional<LivingEntity> getPrimeSuspect() {
        return Optional.ofNullable(this.getSuspect()).filter(suspect -> suspect instanceof LivingEntity).map(suspect -> (LivingEntity)suspect);
    }

    protected record SuspectComparator(WardenAngerManager angerManagement) implements Comparator<Entity> {
        @Override
        public int compare(Entity first, Entity second) {
            if (first.equals(second)) {
                return 0;
            } else {
                int firstAngerLevel = this.angerManagement.suspectsToAngerLevel.getOrDefault(first, 0);
                int secondAngerLevel = this.angerManagement.suspectsToAngerLevel.getOrDefault(second, 0);
                boolean angryTowardsFirst = Angriness.getForAnger(firstAngerLevel).isAngry();
                boolean angryTowardsSecond = Angriness.getForAnger(secondAngerLevel).isAngry();
                if (angryTowardsFirst != angryTowardsSecond) {
                    return  angryTowardsFirst ? -1 : 1;
                } else if (angryTowardsFirst && first instanceof Player != second instanceof Player) {
                    return first instanceof Player ? -1 : 1;
                } else {
                    return firstAngerLevel > secondAngerLevel ? -1 : 1;
                }
            }
        }
    }
}