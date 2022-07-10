package com.cursedcauldron.wildbackport.common.entities.warden;

import com.cursedcauldron.wildbackport.common.registry.WBPositionSources;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SerializableUUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class MobPositionSource implements PositionSource {
    public static final Codec<MobPositionSource> CODEC = RecordCodecBuilder.create(instance -> instance.group((SerializableUUID.CODEC.fieldOf("source_entity")).forGetter(MobPositionSource::getUuid), (Codec.FLOAT.fieldOf("y_offset")).orElse(0.0f).forGetter(entityPositionSource -> entityPositionSource.yOffset)).apply(instance, (uUID, float_) -> new MobPositionSource(Either.right(Either.left(uUID)), float_.floatValue())));
    private Either<Entity, Either<UUID, Integer>> source;
    final float yOffset;

    public MobPositionSource(Entity entity, float yOffset) {
        this(Either.left(entity), yOffset);
    }

    public MobPositionSource(Either<Entity, Either<UUID, Integer>> sourceEntityId, float yOffset) {
        this.source = sourceEntityId;
        this.yOffset = yOffset;
    }

    @Override
    public Optional<BlockPos> getPosition(Level world) {
        if (this.source.left().isEmpty()) {
            this.findEntityInWorld(world);
        }
        return this.source.left().map(entity -> entity.blockPosition().offset(0.0, this.yOffset, 0.0));
    }

    private void findEntityInWorld(Level world) {
        this.source.map(Optional::of, either -> Optional.ofNullable(either.map(uuid -> {
            Entity entity;
            if (world instanceof ServerLevel serverLevel) {
                entity = serverLevel.getEntity(uuid);
            } else {
                entity = null;
            }
            return entity;
        }, world::getEntity))).ifPresent(entity -> {
            this.source = Either.left(entity);
        });
    }

    @Override
    public PositionSourceType<?> getType() {
        return WBPositionSources.MOB.get();
    }

    private UUID getUuid() {
        return this.source.map(Entity::getUUID, either -> either.map(Function.identity(), integer -> {
            throw new RuntimeException("Unable to get entityId from uuid");
        }));
    }

    int getEntityId() {
        return this.source.map(Entity::getId, either -> either.map(uUID -> {
            throw new IllegalStateException("Unable to get entityId from uuid");
        }, Function.identity()));
    }

    public static class Type implements PositionSourceType<MobPositionSource> {
        @Override
        public MobPositionSource read(FriendlyByteBuf buf) {
            return new MobPositionSource(Either.right(Either.right(buf.readVarInt())), buf.readFloat());
        }

        @Override
        public void write(FriendlyByteBuf buf, MobPositionSource source) {
            buf.writeVarInt(source.getEntityId());
            buf.writeFloat(source.yOffset);
        }

        @Override
        public Codec<MobPositionSource> codec() {
            return CODEC;
        }
    }
}