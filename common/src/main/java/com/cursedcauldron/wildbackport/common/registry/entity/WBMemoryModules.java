package com.cursedcauldron.wildbackport.common.registry.entity;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import com.cursedcauldron.wildbackport.core.mixin.access.MemoryModuleTypeAccessor;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SerializableUUID;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

//<>

public class WBMemoryModules {
    public static final CoreRegistry<MemoryModuleType<?>> MEMORIES = CoreRegistry.create(Registry.MEMORY_MODULE_TYPE, WildBackport.MOD_ID);


    public static final Supplier<MemoryModuleType<Unit>> IS_IN_WATER                        = create("is_in_water", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<Unit>> IS_PREGNANT                        = create("is_pregnant", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<List<UUID>>> UNREACHABLE_TONGUE_TARGETS   = create("unreachable_tongue_targets");
    public static final Supplier<MemoryModuleType<LivingEntity>> ROAR_TARGET                = create("roar_target");
    public static final Supplier<MemoryModuleType<BlockPos>> DISTURBANCE_LOCATION           = create("disturbance_location");
    public static final Supplier<MemoryModuleType<Unit>> RECENT_PROJECTILE                  = create("recent_projectile", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<Unit>> IS_SNIFFING                        = create("is_sniffing", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<Unit>> IS_EMERGING                        = create("is_emerging", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<Unit>> ROAR_SOUND_DELAY                   = create("roar_sound_delay", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<Unit>> DIG_COOLDOWN                       = create("dig_cooldown", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<Unit>> ROAR_SOUND_COOLDOWN                = create("roar_sound_cooldown", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<Unit>> SNIFF_COOLDOWN                     = create("sniff_cooldown", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<Unit>> TOUCH_COOLDOWN                     = create("touch_cooldown", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<Unit>> VIBRATION_COOLDOWN                 = create("vibration_cooldown", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<Unit>> SONIC_BOOM_COOLDOWN                = create("sonic_boom_cooldown", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<Unit>> SONIC_BOOM_SOUND_COOLDOWN          = create("sonic_boom_sound_cooldown", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<Unit>> SONIC_BOOM_SOUND_DELAY             = create("sonic_boom_sound_delay", Codec.unit(Unit.INSTANCE));
    public static final Supplier<MemoryModuleType<UUID>> LIKED_PLAYER                       = create("liked_player", SerializableUUID.CODEC);
    public static final Supplier<MemoryModuleType<GlobalPos>> LIKED_NOTEBLOCK               = create("liked_noteblock", GlobalPos.CODEC);
    public static final Supplier<MemoryModuleType<Integer>> LIKED_NOTEBLOCK_COOLDOWN_TICKS  = create("liked_noteblock_cooldown_ticks", Codec.INT);
    public static final Supplier<MemoryModuleType<Integer>> ITEM_PICKUP_COOLDOWN_TICKS      = create("item_pickup_cooldown_ticks", Codec.INT);

    private static <U> Supplier<MemoryModuleType<U>> create(String key) {
        return MEMORIES.register(key, () -> MemoryModuleTypeAccessor.createMemoryModuleType(Optional.empty()));
    }

    private static <U> Supplier<MemoryModuleType<U>> create(String key, Codec<U> codec) {
        return MEMORIES.register(key, () -> MemoryModuleTypeAccessor.createMemoryModuleType(Optional.of(codec)));
    }
}