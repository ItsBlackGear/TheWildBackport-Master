package com.cursedcauldron.wildbackport.core.mixin.common.event;

import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TransientEntitySectionManager.class)
public interface TransientEntitySectionManagerAccessor<T> {
    @Accessor
    LevelCallback<T> getCallbacks();
}
