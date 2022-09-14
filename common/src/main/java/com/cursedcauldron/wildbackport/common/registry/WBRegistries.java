package com.cursedcauldron.wildbackport.common.registry;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.items.Instrument;
import com.cursedcauldron.wildbackport.common.registry.Instruments;
import com.cursedcauldron.wildbackport.common.registry.worldgen.RootPlacerType;
import com.cursedcauldron.wildbackport.core.api.RegistryBuilder;
import com.cursedcauldron.wildbackport.core.api.SampleRegistry;

//<>

public class WBRegistries {
    public static final RegistryBuilder BUILDER = RegistryBuilder.create(WildBackport.MOD_ID);

    public static final SampleRegistry<RootPlacerType<?>> ROOT_PLACER_TYPES = BUILDER.create("worldgen/root_placer_type", registry -> RootPlacerType.MANGROVE_ROOT_PLACER.get());
    public static final SampleRegistry<Instrument> INSTRUMENT = BUILDER.create("instrument", registry -> Instruments.DREAM_GOAT_HORN.get());
//    public static final SampleRegistry<Instrument> INSTRUMENT = BUILDER.create("instrument", Instruments::registerAndGetDefault);
}