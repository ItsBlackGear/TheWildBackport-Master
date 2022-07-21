package com.cursedcauldron.wildbackport.core.api.event.forge;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.event.Interactions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = WildBackport.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InteractionsImpl {
    private static final Set<Consumer<PlayerInteractEvent.RightClickBlock>> INTERACTIONS = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public static void event(PlayerInteractEvent.RightClickBlock event) {
        INTERACTIONS.forEach(consumer -> consumer.accept(event));
    }

    public static void addRightClick(Interactions.Interaction interaction) {
        INTERACTIONS.add(event -> event.setCancellationResult(interaction.of(new UseOnContext(event.getPlayer(), event.getHand(), event.getHitVec()))));
    }
}