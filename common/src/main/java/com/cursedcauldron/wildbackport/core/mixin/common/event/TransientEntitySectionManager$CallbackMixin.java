package com.cursedcauldron.wildbackport.core.mixin.common.event;

import com.cursedcauldron.wildbackport.common.entities.access.Listener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TransientEntitySectionManager.Callback.class)
public class TransientEntitySectionManager$CallbackMixin<T extends EntityAccess> {
    @Shadow @Final TransientEntitySectionManager<T> field_27285;

    @Shadow @Final private T entity;

    @SuppressWarnings("unchecked")
    @Inject(method = "onMove", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/level/entity/EntityAccess;isAlwaysTicking()Z"))
    private void wb$onMove(CallbackInfo ci) {
//        Listener.Callback<LevelCallback<T>> callback = Listener.Callback.of(((TransientEntitySectionManagerAccessor<T>)this.field_27285).getCallbacks());
//        Listener.Callback.of(callback).onSectionChange(this.entity);

    }
}