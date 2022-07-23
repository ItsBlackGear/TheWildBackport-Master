package com.cursedcauldron.wildbackport.common.entities.warden;

import com.cursedcauldron.wildbackport.client.registry.WBCriteriaTriggers;
import com.cursedcauldron.wildbackport.common.utils.PositionUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.SerializableUUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationPath;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

//<>

public class VibrationHandler implements GameEventListener {
    protected final PositionSource source;
    protected final int range;
    protected final VibrationConfig config;
    @Nullable protected Vibration event;
    protected float distance;
    protected int delay;

    public static Codec<VibrationHandler> codec(VibrationConfig config) {
        return RecordCodecBuilder.create(instance -> {
            return instance.group(PositionSource.CODEC.fieldOf("source").forGetter(listener -> {
                return listener.source;
            }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("range").forGetter(listener -> {
                return listener.range;
            }), Vibration.CODEC.optionalFieldOf("event").forGetter(listener -> {
                return Optional.ofNullable(listener.event);
            }), Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("event_distance").orElse(0.0F).forGetter(listener -> {
                return listener.distance;
            }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter(listener -> {
                return listener.delay;
            })).apply(instance, (source, range, event, distance, delay) -> {
                return new VibrationHandler(source, range, config, event.orElse(null), distance, delay);
            });
        });
    }

    public VibrationHandler(PositionSource source, int range, VibrationConfig config, @Nullable Vibration event, float distance, int delay) {
        this.source = source;
        this.range = range;
        this.config = config;
        this.event = event;
        this.distance = distance;
        this.delay = delay;
    }

    public VibrationHandler(PositionSource source, int range, VibrationConfig config) {
        this(source, range, config, null, 0.0F, 0);
    }

    public void tick(Level level) {
        if (level instanceof ServerLevel server) {
            if (this.event != null) {
                --this.delay;
                if (this.delay <= 0) {
                    this.delay = 0;
                    this.config.onSignalReceive(server, this, new BlockPos(this.event.pos), this.event.event, this.event.getEntity(server).orElse(null), this.event.getProjectileOwner(server).orElse(null), this.distance);
                    this.event = null;
                }
            }
        }
    }

    @Override
    public PositionSource getListenerSource() {
        return this.source;
    }

    @Override
    public int getListenerRadius() {
        return this.range;
    }

    @Override
    public boolean handleGameEvent(Level level, GameEvent event, @Nullable Entity entity, BlockPos pos) {
        if (this.event != null) {
            return  false;
        } else {
            Optional<BlockPos> optional = this.source.getPosition(level);
            if (!this.config.isValidVibration(event, entity)) {
                return false;
            } else {
                Vec3 source = PositionUtils.toVec(pos);
                Vec3 target = PositionUtils.toVec(optional.get());
                if (!this.config.shouldListen((ServerLevel)level, this, new BlockPos(source), event, entity)) {
                    return false;
                } else if (isOccluded(level, source, target)) {
                    return false;
                } else {
                    this.scheduleSignal(level, event, entity, source, target);
                    return true;
                }
            }
        }
    }

    private void scheduleSignal(Level level, GameEvent event, @Nullable Entity entity, Vec3 source, Vec3 target) {
        this.distance = (float)source.distanceTo(target);
        this.event = new Vibration(event, this.distance, source, entity);
        this.delay = Mth.floor(this.distance);
        ((ServerLevel)level).sendVibrationParticle(new VibrationPath(PositionUtils.toBlockPos(source), this.source, this.delay));
        this.config.onSignalSchedule();
    }

    private static boolean isOccluded(Level level, Vec3 source, Vec3 target) {
        Vec3 sourceVec = new Vec3((double)Mth.floor(source.x) + 0.5D, (double)Mth.floor(source.y) + 0.5D, (double)Mth.floor(source.z) + 0.5D);
        Vec3 targetVec = new Vec3((double)Mth.floor(target.x) + 0.5D, (double)Mth.floor(target.y) + 0.5D, (double)Mth.floor(target.z) + 0.5D);

        for (Direction direction : Direction.values()) {
            Vec3 offsetVec = PositionUtils.relative(sourceVec, direction, 1.0E-5F);
            if (level.isBlockInLine(new ClipBlockStateContext(offsetVec, targetVec, state -> {
                return state.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS);
            })).getType() != HitResult.Type.BLOCK) {
                return false;
            }
        }

        return true;
    }

    public record Vibration(GameEvent event, float distance, Vec3 pos, @Nullable UUID source, @Nullable UUID projectileOwner, @Nullable Entity entity) {
        public static final Codec<Vibration> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(Registry.GAME_EVENT.byNameCodec().fieldOf("game_event").forGetter(Vibration::event), Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("distance").forGetter(Vibration::distance), PositionUtils.VEC_CODEC.fieldOf("pos").forGetter(Vibration::pos), SerializableUUID.CODEC.optionalFieldOf("source").forGetter(entity -> {
                return Optional.ofNullable(entity.source());
            }), SerializableUUID.CODEC.optionalFieldOf("projectile_owner").forGetter(entity -> {
                return Optional.ofNullable(entity.projectileOwner());
            })).apply(instance, (event, distance, pos, source, projectileOwner) -> {
                return new Vibration(event, distance, pos, source.orElse(null), projectileOwner.orElse(null));
            });
        });

        public Vibration(GameEvent event, float distance, Vec3 pos, @Nullable UUID source, @Nullable UUID projectileOwner) {
            this(event, distance, pos, source, projectileOwner, null);
        }

        public Vibration(GameEvent event, float distance, Vec3 pos, @Nullable Entity entity) {
            this(event, distance, pos, entity == null ? null : entity.getUUID(), getProjectileOwner(entity), entity);
        }

        @Nullable
        private static UUID getProjectileOwner(@Nullable Entity entity) {
            if (entity instanceof Projectile projectile) {
                if (projectile.getOwner() != null) {
                    return projectile.getOwner().getUUID();
                }
            }

            return null;
        }

        public Optional<Entity> getEntity(ServerLevel level) {
            return Optional.ofNullable(this.entity).or(() -> {
                return Optional.ofNullable(this.source).map(level::getEntity);
            });
        }

        public Optional<Entity> getProjectileOwner(ServerLevel level) {
            return this.getEntity(level).filter(entity -> {
                return entity instanceof Projectile;
            }).map(entity -> {
                return (Projectile)entity;
            }).map(Projectile::getOwner).or(() -> {
                return Optional.ofNullable(this.projectileOwner).map(level::getEntity);
            });
        }
    }

    public interface VibrationConfig {
        default TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.VIBRATIONS;
        }

        default boolean canTriggerAvoidVibration() {
            return false;
        }

        default boolean isValidVibration(GameEvent event, @Nullable Entity entity) {
            if  (!event.is(this.getListenableEvents())) {
                return false;
            } else {
                if (entity != null) {
                    if (entity.isSpectator()) {
                        return false;
                    }

                    if (entity.isSteppingCarefully() && event.is(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
                        if (this.canTriggerAvoidVibration() && entity instanceof ServerPlayer player) {
                            WBCriteriaTriggers.AVOID_VIBRATION.trigger(player);
                        }

                        return false;
                    }

                    return !entity.occludesVibrations();
                }

                return true;
            }
        }

        boolean shouldListen(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity);

        void onSignalReceive(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity source, float distance);

        default void onSignalSchedule() {}
    }
}