package com.cursedcauldron.wildbackport.common.tag;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.items.Instrument;
import com.cursedcauldron.wildbackport.common.registry.WBRegistries;
import com.cursedcauldron.wildbackport.core.api.TagBuilder;
import net.minecraft.tags.TagKey;

public class InstrumentTags {
    public static final TagBuilder<Instrument> TAGS = TagBuilder.create(WBRegistries.INSTRUMENT.registry(), WildBackport.MOD_ID);

    public static final TagKey<Instrument> REGULAR_GOAT_HORNS = TAGS.create("regular_goat_horns");
    public static final TagKey<Instrument> SCREAMING_GOAT_HORNS = TAGS.create("screaming_goat_horns");
    public static final TagKey<Instrument> GOAT_HORNS = TAGS.create("goat_horns");
}