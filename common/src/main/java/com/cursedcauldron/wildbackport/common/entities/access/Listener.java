package com.cursedcauldron.wildbackport.common.entities.access;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.GameEventListenerRegistrar;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class Listener {
    public interface Instance {
        static Instance of(GameEventListenerRegistrar instance) {
            return (Instance)instance;
        }

        void onPosCallback(Level level);

        void setListener(GameEventListener listener, @Nullable Level level);

        GameEventListener getListener();
    }

    public interface MobInstance {
        static MobInstance of(Entity entity) {
            return MobInstance.class.cast(entity);
        }

        default void updateEventHandler(BiConsumer<GameEventListenerRegistrar, Level> callback) {
        }
    }

    public interface Callback<T> extends LevelCallback<T> {
        @SuppressWarnings("unchecked")
        static <T> Callback<T> of(T entity) {
            return (Callback<T>)entity;
        }
        void onSectionChange(T entry);
    }
}