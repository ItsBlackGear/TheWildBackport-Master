package com.cursedcauldron.wildbackport.core.mixin.access;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TrunkPlacerType.class)
public interface TrunkPlacerTypeAccessor {
    @Invoker("<init>")
    static <P extends TrunkPlacer> TrunkPlacerType<P> createTrunkPlacerType(Codec<P> codec) {
        throw new UnsupportedOperationException();
    }
}
