package com.cursedcauldron.wildbackport.core.mixin.common.event;

import com.cursedcauldron.wildbackport.common.entities.access.Listener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEventListenerRegistrar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.EntityCallbacks.class)
public abstract class ServerLevel$CallbackMixin implements Listener.Callback<Entity> {
//    @Inject(method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
//    private void wb$onTrackingStart(Entity entity, CallbackInfo ci) {
//        Listener.MobInstance instance = Listener.MobInstance.of(entity);
//        instance.updateEventHandler(GameEventListenerRegistrar::onListenerMove);
//    }
//
//    @Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
//    private void wb$onTrackingEnd(Entity entity, CallbackInfo ci) {
//        Listener.MobInstance instance = Listener.MobInstance.of(entity);
//        instance.updateEventHandler(GameEventListenerRegistrar::onListenerRemoved);
//    }
//
//    @Override
//    public void onSectionChange(Entity entry) {
//        Listener.MobInstance instance = Listener.MobInstance.of(entry);
//        instance.updateEventHandler(GameEventListenerRegistrar::onListenerRemoved);
//    }
}