package com.cursedcauldron.wildbackport.core.api.event.fabric;

import com.cursedcauldron.wildbackport.core.api.event.Interactions;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.item.context.UseOnContext;

public class InteractionsImpl {
    public static void addRightClick(Interactions.Interaction interaction) {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> interaction.of(new UseOnContext(player, hand, hitResult)));
    }
}