package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mob.class)
public interface MobAccessor {
    @Accessor
    Entity getLeashHolder();

    @Accessor
    CompoundTag getLeashInfoTag();

    @Invoker
    void callRestoreLeashFromSave();
}
