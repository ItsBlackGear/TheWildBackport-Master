package com.cursedcauldron.wildbackport.core.api.event;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public class Interactions {
    @ExpectPlatform
    public static void addRightClick(Interaction interaction) {
        throw new AssertionError();
    }

    public interface Interaction {
        InteractionResult of(UseOnContext context);
    }
}