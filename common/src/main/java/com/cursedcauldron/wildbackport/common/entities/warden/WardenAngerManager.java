package com.cursedcauldron.wildbackport.common.entities.warden;

import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.SerializableUUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//<>

public class WardenAngerManager {
    private int updateTimer = Mth.randomBetweenInclusive(new Random(), 0, 2);
    int primeAnger;
    private static final Codec<Pair<UUID, Integer>> SUSPECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(SerializableUUID.CODEC.fieldOf("uuid").forGetter(Pair::getFirst), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("anger").forGetter(Pair::getSecond)).apply(instance, Pair::of));
    private final Predicate<Entity> suspectPredicate;
    protected final ArrayList<Entity> suspects;
    private final SuspectComparator suspectComparator;
    protected final Object2IntMap<Entity> suspectsToAngerLevel;
    protected final Object2IntMap<UUID> suspectUuidsToAngerLevel;

    public static Codec<WardenAngerManager> codec(Predicate<Entity> validTarget) {
        return RecordCodecBuilder.create(instance -> instance.group(SUSPECT_CODEC.listOf().fieldOf("suspects").orElse(Collections.emptyList()).forGetter(WardenAngerManager::getSuspects)).apply(instance, suspects -> new WardenAngerManager(validTarget, suspects)));
    }

    public WardenAngerManager(Predicate<Entity> suspectPredicate, List<Pair<UUID, Integer>> suspectUuidsToAngerLevel) {
        this.suspectPredicate = suspectPredicate;
        this.suspects = new ArrayList<>();
        this.suspectComparator = new SuspectComparator(this);
        this.suspectsToAngerLevel = new Object2IntOpenHashMap<>();
        this.suspectUuidsToAngerLevel = new Object2IntOpenHashMap<>(suspectUuidsToAngerLevel.size());
        suspectUuidsToAngerLevel.forEach(suspect -> this.suspectUuidsToAngerLevel.put(suspect.getFirst(), suspect.getSecond()));
    }

    private List<Pair<UUID, Integer>> getSuspects() {
        return Streams.concat(this.suspects.stream().map(suspect -> Pair.of(suspect.getUUID(), this.suspectsToAngerLevel.getInt(suspect))), this.suspectUuidsToAngerLevel.object2IntEntrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getIntValue()))).collect(Collectors.toList());
    }

    public void tick(ServerLevel level, Predicate<Entity> suspectPredicate) {
        --this.updateTimer;
        if (this.updateTimer <= 0) {
            this.updateSuspectsMap(level);
            this.updateTimer = 2;
        }

        ObjectIterator<Object2IntMap.Entry<UUID>> uuidToAnger = this.suspectUuidsToAngerLevel.object2IntEntrySet().iterator();

        while(uuidToAnger.hasNext()) {
            Object2IntMap.Entry<UUID> entry = uuidToAnger.next();
            int anger = entry.getIntValue();
            if (anger <= 1) {
                uuidToAnger.remove();
            } else {
                entry.setValue(anger - 1);
            }
        }

        ObjectIterator<Object2IntMap.Entry<Entity>> suspectToAnger = this.suspectsToAngerLevel.object2IntEntrySet().iterator();

        while(suspectToAnger.hasNext()) {
            Object2IntMap.Entry<Entity> entry = suspectToAnger.next();
            int anger = entry.getIntValue();
            Entity entity = entry.getKey();
            Entity.RemovalReason reason = entity.getRemovalReason();
            if (anger > 1 && suspectPredicate.test(entity) && reason == null) {
                entry.setValue(anger - 1);
            } else {
                this.suspects.remove(entity);
                suspectToAnger.remove();
                if (anger > 1 && reason != null) {
                    switch (reason) {
                        case CHANGED_DIMENSION, UNLOADED_TO_CHUNK, UNLOADED_WITH_PLAYER -> this.suspectUuidsToAngerLevel.put(entity.getUUID(), anger - 1);
                    }
                }
            }
        }

        this.updatePrimeAnger();
    }

    private void updatePrimeAnger() {
        this.primeAnger = 0;
        this.suspects.sort(this.suspectComparator);
        if (this.suspects.size() == 1) this.primeAnger = this.suspectsToAngerLevel.getInt(this.suspects.get(0));
    }

    private void updateSuspectsMap(ServerLevel level) {
        ObjectIterator<Object2IntMap.Entry<UUID>> uuidsToAnger = this.suspectUuidsToAngerLevel.object2IntEntrySet().iterator();

        while(uuidsToAnger.hasNext()) {
            Object2IntMap.Entry<UUID> entry = uuidsToAnger.next();
            int anger = entry.getIntValue();
            Entity entity = level.getEntity(entry.getKey());
            if (entity != null) {
                this.suspectsToAngerLevel.put(entity, anger);
                this.suspects.add(entity);
                uuidsToAnger.remove();
            }
        }
    }

    public int increaseAngerAt(Entity entity, int amount) {
        boolean isTarget = !this.suspectsToAngerLevel.containsKey(entity);
        int angerLevel = this.suspectsToAngerLevel.computeInt(entity, (suspect, anger) -> Math.min(150, (anger == null ? 0 : anger) + amount));
        if (isTarget) {
            int modifier = this.suspectUuidsToAngerLevel.removeInt(entity.getUUID());
            this.suspectsToAngerLevel.put(entity, angerLevel += modifier);
            this.suspects.add(entity);
        }

        this.updatePrimeAnger();
        return angerLevel;
    }

    public void removeSuspect(Entity entity) {
        this.suspectsToAngerLevel.removeInt(entity);
        this.suspects.remove(entity);
        this.updatePrimeAnger();
    }

    @Nullable
    private Entity getPrimeSuspectInternal() {
        return this.suspects.stream().filter(this.suspectPredicate).findFirst().orElse(null);
    }

    public int getAngerFor(@Nullable Entity entity) {
        return entity == null ? this.primeAnger : this.suspectsToAngerLevel.getInt(entity);
    }

    public Optional<LivingEntity> getPrimeSuspect() {
        return Optional.ofNullable(this.getPrimeSuspectInternal()).filter(suspect -> suspect instanceof LivingEntity).map(suspect -> (LivingEntity)suspect);
    }

    protected record SuspectComparator(WardenAngerManager angerManagement) implements Comparator<Entity> {
        public int compare(Entity firstSuspect, Entity secondSuspect) {
            if (firstSuspect.equals(secondSuspect)) {
                return 0;
            } else {
                int angerToFirstSuspect = this.angerManagement.suspectsToAngerLevel.getOrDefault(firstSuspect, 0);
                int angerToSecondSuspect = this.angerManagement.suspectsToAngerLevel.getOrDefault(secondSuspect, 0);
                this.angerManagement.primeAnger = Math.max(this.angerManagement.primeAnger, Math.max(angerToFirstSuspect, angerToSecondSuspect));
                boolean isAngryWithFirstSuspect = Angriness.getForAnger(angerToFirstSuspect).isAngry();
                boolean isAngryWithSecondSuspect = Angriness.getForAnger(angerToSecondSuspect).isAngry();
                if (isAngryWithFirstSuspect != isAngryWithSecondSuspect) {
                    return isAngryWithFirstSuspect ? -1 : 1;
                } else {
                    boolean isFirstSuspectPlayer = firstSuspect instanceof Player;
                    boolean isSecondSuspectPlayer = secondSuspect instanceof Player;
                    if (isFirstSuspectPlayer != isSecondSuspectPlayer) {
                        return isFirstSuspectPlayer ? -1 : 1;
                    } else {
                        return Integer.compare(angerToSecondSuspect, angerToFirstSuspect);
                    }
                }
            }
        }
    }
}