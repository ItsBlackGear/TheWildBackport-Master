package com.cursedcauldron.wildbackport.core.mixin.access;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(GameRules.class)
public interface GameRulesAccessor {
    @Invoker
    static <T extends GameRules.Value<T>> GameRules.Key<T> callRegister(String string, GameRules.Category category, GameRules.Type<T> type) {
        throw new UnsupportedOperationException();
    }
}
