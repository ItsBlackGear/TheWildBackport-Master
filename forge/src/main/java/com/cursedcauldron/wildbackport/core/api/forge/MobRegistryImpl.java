package com.cursedcauldron.wildbackport.core.api.forge;

import com.cursedcauldron.wildbackport.WildBackport;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import java.util.function.Supplier;

public class MobRegistryImpl {
    public static void registerAttributes(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> attribute) {
        EventBuses.getModEventBusOrThrow(WildBackport.MOD_ID).<EntityAttributeCreationEvent>addListener(event -> event.put(type.get(), attribute.get().build()));
    }
}