package com.cursedcauldron.wildbackport.common.registry;

import com.cursedcauldron.wildbackport.core.mixin.access.BooleanValueAccessor;
import com.cursedcauldron.wildbackport.core.mixin.access.GameRulesAccessor;
import net.minecraft.world.level.GameRules;

public class WBGameRules {
    public static void setup() {}

    public static final GameRules.Key<GameRules.BooleanValue> DO_WARDEN_SPAWNING = create("doWardenSpawning", GameRules.Category.SPAWNING, BooleanValueAccessor.callCreate(true));

    private static <T extends GameRules.Value<T>> GameRules.Key<T> create(String key, GameRules.Category category, GameRules.Type<T> type) {
        return GameRulesAccessor.callRegister(key, category, type);
    }
}