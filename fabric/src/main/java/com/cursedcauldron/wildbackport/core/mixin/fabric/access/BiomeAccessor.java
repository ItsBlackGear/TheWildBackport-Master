package com.cursedcauldron.wildbackport.core.mixin.fabric.access;

import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Biome.class)
public interface BiomeAccessor {
    @Invoker
    Biome.BiomeCategory callGetBiomeCategory();
}
