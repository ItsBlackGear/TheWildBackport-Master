package com.cursedcauldron.wildbackport.common.registry;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.entities.warden.MobPositionSource;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.gameevent.PositionSourceType;

import java.util.function.Supplier;

public class WBPositionSources {
    public static final CoreRegistry<PositionSourceType<?>> SOURCES = CoreRegistry.create(Registry.POSITION_SOURCE_TYPE, WildBackport.MOD_ID);

    public static final Supplier<PositionSourceType<MobPositionSource>> MOB = SOURCES.register("mob", MobPositionSource.Type::new);
}