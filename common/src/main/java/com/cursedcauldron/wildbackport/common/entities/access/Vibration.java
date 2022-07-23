package com.cursedcauldron.wildbackport.common.entities.access;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record Vibration(@Nullable UUID uuid, @Nullable UUID sourceUuid, @Nullable Entity entity) {
    public Vibration(@Nullable Entity entity) {
        this(entity == null ? null : entity.getUUID(), Vibration.getOwnerUuid(entity), entity);
    }

    @Nullable
    private static UUID getOwnerUuid(@Nullable Entity entity) {
        if (entity instanceof Projectile projectile && projectile.getOwner() != null) {
            return projectile.getOwner().getUUID();
        } else {
            return null;
        }
    }

    public Optional<Entity> getEntity(ServerLevel level) {
        return Optional.ofNullable(this.entity).or(() -> Optional.ofNullable(this.uuid).map(level::getEntity));
    }

    public Optional<Entity> getOwner(ServerLevel level) {
        return Optional.ofNullable(this.entity).filter(entity -> entity instanceof Projectile).map(entity -> (Projectile)entity).map(Projectile::getOwner).or(() -> Optional.ofNullable(this.sourceUuid).map(level::getEntity));
    }

    public interface Instance {
        static Instance of(VibrationListener listener) {
            return (Instance)listener;
        }

        void setPos(BlockPos pos);

        BlockPos getPos();

        void setEntity(Entity entity);

        Entity getEntity();

        void setSource(Entity entity);

        Entity getSource();

        void setVibration(Vibration vibration);
    }
}