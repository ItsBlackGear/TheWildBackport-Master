package com.cursedcauldron.wildbackport.common.registry;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.items.Instrument;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class Instruments {
    public static final CoreRegistry<Instrument> INSTRUMENTS = CoreRegistry.create(WBRegistries.INSTRUMENT.registry(), WildBackport.MOD_ID);

    public static final Supplier<Instrument> PONDER_GOAT_HORN = create("ponder_goat_horn", WBSoundEvents.GOAT_HORN_SOUNDS.get(0));
    public static final Supplier<Instrument> SIGN_GOAT_HORN = create("sign_goat_horn", WBSoundEvents.GOAT_HORN_SOUNDS.get(1));
    public static final Supplier<Instrument> SEEK_GOAT_HORN = create("seek_goat_horn", WBSoundEvents.GOAT_HORN_SOUNDS.get(2));
    public static final Supplier<Instrument> FEEL_GOAT_HORN = create("feel_goat_horn", WBSoundEvents.GOAT_HORN_SOUNDS.get(3));
    public static final Supplier<Instrument> ADMIRE_GOAT_HORN = create("admire_goat_horn", WBSoundEvents.GOAT_HORN_SOUNDS.get(4));
    public static final Supplier<Instrument> CALL_GOAT_HORN = create("call_goat_horn", WBSoundEvents.GOAT_HORN_SOUNDS.get(5));
    public static final Supplier<Instrument> YEARN_GOAT_HORN = create("yearn_goat_horn", WBSoundEvents.GOAT_HORN_SOUNDS.get(6));
    public static final Supplier<Instrument> DREAM_GOAT_HORN = create("dream_goat_horn", WBSoundEvents.GOAT_HORN_SOUNDS.get(7));

    private static Supplier<Instrument> create(String key, SoundEvent soundEvent) {
        return INSTRUMENTS.register(key, () -> new Instrument(soundEvent, 140, 256.0F));
    }

//    public static final ResourceKey<Instrument> PONDER_GOAT_HORN = Instruments.of("ponder_goat_horn");
//    public static final ResourceKey<Instrument> SIGN_GOAT_HORN = Instruments.of("sign_goat_horn");
//    public static final ResourceKey<Instrument> SEEK_GOAT_HORN = Instruments.of("seek_goat_horn");
//    public static final ResourceKey<Instrument> FEEL_GOAT_HORN = Instruments.of("feel_goat_horn");
//    public static final ResourceKey<Instrument> ADMIRE_GOAT_HORN = Instruments.of("admire_goat_horn");
//    public static final ResourceKey<Instrument> CALL_GOAT_HORN = Instruments.of("call_goat_horn");
//    public static final ResourceKey<Instrument> YEARN_GOAT_HORN = Instruments.of("yearn_goat_horn");
//    public static final ResourceKey<Instrument> DREAM_GOAT_HORN = Instruments.of("dream_goat_horn");
//
//    private static ResourceKey<Instrument> of(String name) {
//        return ResourceKey.create(WBRegistries.INSTRUMENT.key(), new ResourceLocation(name));
//    }
//
//    public static Instrument registerAndGetDefault(Registry<Instrument> registry) {
//        Registry.register(registry, PONDER_GOAT_HORN, new Instrument(WBSoundEvents.GOAT_HORN_SOUNDS.get(0), 140, 256.0F));
//        Registry.register(registry, SIGN_GOAT_HORN, new Instrument(WBSoundEvents.GOAT_HORN_SOUNDS.get(1), 140, 256.0F));
//        Registry.register(registry, SEEK_GOAT_HORN, new Instrument(WBSoundEvents.GOAT_HORN_SOUNDS.get(2), 140, 256.0F));
//        Registry.register(registry, FEEL_GOAT_HORN, new Instrument(WBSoundEvents.GOAT_HORN_SOUNDS.get(3), 140, 256.0F));
//        Registry.register(registry, ADMIRE_GOAT_HORN, new Instrument(WBSoundEvents.GOAT_HORN_SOUNDS.get(4), 140, 256.0F));
//        Registry.register(registry, CALL_GOAT_HORN, new Instrument(WBSoundEvents.GOAT_HORN_SOUNDS.get(5), 140, 256.0F));
//        Registry.register(registry, YEARN_GOAT_HORN, new Instrument(WBSoundEvents.GOAT_HORN_SOUNDS.get(6), 140, 256.0F));
//        return Registry.register(registry, DREAM_GOAT_HORN, new Instrument(WBSoundEvents.GOAT_HORN_SOUNDS.get(7), 140, 256.0F));
//    }
}