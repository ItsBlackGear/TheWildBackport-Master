package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecordItem.class)
public interface RecordItemAccessor {
    @Invoker("<init>")
    static RecordItem createRecordItem(int i, SoundEvent soundEvent, Item.Properties properties) {
        throw new UnsupportedOperationException();
    }
}
