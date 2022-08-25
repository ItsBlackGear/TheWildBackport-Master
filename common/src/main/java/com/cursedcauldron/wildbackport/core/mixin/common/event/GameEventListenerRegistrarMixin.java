package com.cursedcauldron.wildbackport.core.mixin.common.event;

import com.cursedcauldron.wildbackport.common.entities.access.Listener;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.GameEventListenerRegistrar;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(GameEventListenerRegistrar.class)
public abstract class GameEventListenerRegistrarMixin implements Listener.Instance {
    @Mutable
    @Shadow @Final private GameEventListener listener;

    @Shadow protected abstract void ifEventDispatcherExists(Level level, @Nullable SectionPos sectionPos, Consumer<GameEventDispatcher> consumer);

    @Shadow @Nullable private SectionPos sectionPos;

    @Shadow public abstract void onListenerMove(Level level);

    @Override
    public void onPosCallback(Level level) {
        this.onListenerMove(level);
    }

    @Override
    public void setListener(GameEventListener listener, @Nullable Level level) {
        if (this.listener == listener) return;
        this.ifEventDispatcherExists(level, this.sectionPos, dispatcher -> dispatcher.unregister(this.listener));
        this.ifEventDispatcherExists(level, this.sectionPos, dispatcher -> dispatcher.register(this.listener));
        this.listener = listener;
    }

    @Override
    public GameEventListener getListener() {
        return this.listener;
    }
}