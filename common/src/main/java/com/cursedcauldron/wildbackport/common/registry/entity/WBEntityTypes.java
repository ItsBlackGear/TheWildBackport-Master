package com.cursedcauldron.wildbackport.common.registry.entity;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.entities.Allay;
import com.cursedcauldron.wildbackport.common.entities.ChestBoat;
import com.cursedcauldron.wildbackport.common.entities.Frog;
import com.cursedcauldron.wildbackport.common.entities.MangroveBoat;
import com.cursedcauldron.wildbackport.common.entities.Tadpole;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

//<>

public class WBEntityTypes {
    public static final CoreRegistry<EntityType<?>> ENTITIES = CoreRegistry.create(Registry.ENTITY_TYPE, WildBackport.MOD_ID);

    public static final Supplier<EntityType<Allay>> ALLAY                   = create("allay", EntityType.Builder.of(Allay::new, MobCategory.CREATURE).sized(0.35F, 0.6F).clientTrackingRange(8).updateInterval(2));
    public static final Supplier<EntityType<Frog>> FROG                     = create("frog", EntityType.Builder.of(Frog::new, MobCategory.CREATURE).sized(0.5F, 0.5F).clientTrackingRange(10));
    public static final Supplier<EntityType<Tadpole>> TADPOLE               = create("tadpole", EntityType.Builder.of(Tadpole::new, MobCategory.CREATURE).sized(0.4F, 0.3F).clientTrackingRange(10));
    public static final Supplier<EntityType<Warden>> WARDEN                 = create("warden", EntityType.Builder.of(Warden::new, MobCategory.MONSTER).sized(0.9F, 2.9F).clientTrackingRange(16).fireImmune());
    public static final Supplier<EntityType<MangroveBoat>> MANGROVE_BOAT    = create("mangrove_boat", EntityType.Builder.<MangroveBoat>of(MangroveBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10));
    public static final Supplier<EntityType<ChestBoat>> CHEST_BOAT          = create("chest_boat", EntityType.Builder.<ChestBoat>of(ChestBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10));

    private static <T extends Entity> Supplier<EntityType<T>> create(String key, EntityType.Builder<T> builder) {
        return ENTITIES.register(key, () -> builder.build(key));
    }
}