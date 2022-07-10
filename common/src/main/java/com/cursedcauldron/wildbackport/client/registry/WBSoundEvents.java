package com.cursedcauldron.wildbackport.client.registry;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.stream.IntStream;

//<>

public class WBSoundEvents {
    public static final CoreRegistry<SoundEvent> SOUNDS = CoreRegistry.create(Registry.SOUND_EVENT, WildBackport.MOD_ID);

    // Blocks
    public static final SoundEvent BLOCK_SCULK_VEIN_BREAK                   = create("block.sculk_vein.break");
    public static final SoundEvent BLOCK_SCULK_VEIN_FALL                    = create("block.sculk_vein.fall");
    public static final SoundEvent BLOCK_SCULK_VEIN_HIT                     = create("block.sculk_vein.hit");
    public static final SoundEvent BLOCK_SCULK_VEIN_PLACE                   = create("block.sculk_vein.place");
    public static final SoundEvent BLOCK_SCULK_VEIN_STEP                    = create("block.sculk_vein.step");
    public static final SoundEvent BLOCK_SCULK_CATALYST_BLOOM               = create("block.sculk_catalyst.bloom");
    public static final SoundEvent BLOCK_SCULK_CATALYST_BREAK               = create("block.sculk_catalyst.break");
    public static final SoundEvent BLOCK_SCULK_CATALYST_FALL                = create("block.sculk_catalyst.fall");
    public static final SoundEvent BLOCK_SCULK_CATALYST_HIT                 = create("block.sculk_catalyst.hit");
    public static final SoundEvent BLOCK_SCULK_CATALYST_PLACE               = create("block.sculk_catalyst.place");
    public static final SoundEvent BLOCK_SCULK_CATALYST_STEP                = create("block.sculk_catalyst.step");
    public static final SoundEvent BLOCK_SCULK_SPREAD                       = create("block.sculk.spread");
    public static final SoundEvent BLOCK_SCULK_CHARGE                       = create("block.sculk.charge");
    public static final SoundEvent BLOCK_SCULK_BREAK                        = create("block.sculk.break");
    public static final SoundEvent BLOCK_SCULK_FALL                         = create("block.sculk.fall");
    public static final SoundEvent BLOCK_SCULK_HIT                          = create("block.sculk.hit");
    public static final SoundEvent BLOCK_SCULK_PLACE                        = create("block.sculk.place");
    public static final SoundEvent BLOCK_SCULK_STEP                         = create("block.sculk.step");
    public static final SoundEvent BLOCK_SCULK_SHRIEKER_BREAK               = create("block.sculk_shrieker.break");
    public static final SoundEvent BLOCK_SCULK_SHRIEKER_FALL                = create("block.sculk_shrieker.fall");
    public static final SoundEvent BLOCK_SCULK_SHRIEKER_HIT                 = create("block.sculk_shrieker.hit");
    public static final SoundEvent BLOCK_SCULK_SHRIEKER_PLACE               = create("block.sculk_shrieker.place");
    public static final SoundEvent BLOCK_SCULK_SHRIEKER_STEP                = create("block.sculk_shrieker.step");
    public static final SoundEvent BLOCK_SCULK_SHRIEKER_SHRIEK              = create("block.sculk_shrieker.shriek");
    public static final SoundEvent BLOCK_FROGLIGHT_BREAK                    = create("block.froglight.break");
    public static final SoundEvent BLOCK_FROGLIGHT_FALL                     = create("block.froglight.fall");
    public static final SoundEvent BLOCK_FROGLIGHT_HIT                      = create("block.froglight.hit");
    public static final SoundEvent BLOCK_FROGLIGHT_PLACE                    = create("block.froglight.place");
    public static final SoundEvent BLOCK_FROGLIGHT_STEP                     = create("block.froglight.step");
    public static final SoundEvent BLOCK_FROGSPAWN_BREAK                    = create("block.frogspawn.break");
    public static final SoundEvent BLOCK_FROGSPAWN_FALL                     = create("block.frogspawn.fall");
    public static final SoundEvent BLOCK_FROGSPAWN_HATCH                    = create("block.frogspawn.hatch");
    public static final SoundEvent BLOCK_FROGSPAWN_HIT                      = create("block.frogspawn.hit");
    public static final SoundEvent BLOCK_FROGSPAWN_PLACE                    = create("block.frogspawn.place");
    public static final SoundEvent BLOCK_FROGSPAWN_STEP                     = create("block.frogspawn.step");
    public static final SoundEvent BLOCK_MANGROVE_ROOTS_BREAK               = create("block.mangrove_roots.break");
    public static final SoundEvent BLOCK_MANGROVE_ROOTS_FALL                = create("block.mangrove_roots.fall");
    public static final SoundEvent BLOCK_MANGROVE_ROOTS_HIT                 = create("block.mangrove_roots.hit");
    public static final SoundEvent BLOCK_MANGROVE_ROOTS_PLACE               = create("block.mangrove_roots.place");
    public static final SoundEvent BLOCK_MANGROVE_ROOTS_STEP                = create("block.mangrove_roots.step");
    public static final SoundEvent BLOCK_MUD_BREAK                          = create("block.mud.break");
    public static final SoundEvent BLOCK_MUD_FALL                           = create("block.mud.fall");
    public static final SoundEvent BLOCK_MUD_HIT                            = create("block.mud.hit");
    public static final SoundEvent BLOCK_MUD_PLACE                          = create("block.mud.place");
    public static final SoundEvent BLOCK_MUD_STEP                           = create("block.mud.step");
    public static final SoundEvent BLOCK_MUD_BRICKS_BREAK                   = create("block.mud_bricks.break");
    public static final SoundEvent BLOCK_MUD_BRICKS_FALL                    = create("block.mud_bricks.fall");
    public static final SoundEvent BLOCK_MUD_BRICKS_HIT                     = create("block.mud_bricks.hit");
    public static final SoundEvent BLOCK_MUD_BRICKS_PLACE                   = create("block.mud_bricks.place");
    public static final SoundEvent BLOCK_MUD_BRICKS_STEP                    = create("block.mud_bricks.step");
    public static final SoundEvent BLOCK_MUDDY_MANGROVE_ROOTS_BREAK         = create("block.muddy_mangrove_roots.break");
    public static final SoundEvent BLOCK_MUDDY_MANGROVE_ROOTS_FALL          = create("block.muddy_mangrove_roots.fall");
    public static final SoundEvent BLOCK_MUDDY_MANGROVE_ROOTS_HIT           = create("block.muddy_mangrove_roots.hit");
    public static final SoundEvent BLOCK_MUDDY_MANGROVE_ROOTS_PLACE         = create("block.muddy_mangrove_roots.place");
    public static final SoundEvent BLOCK_MUDDY_MANGROVE_ROOTS_STEP          = create("block.muddy_mangrove_roots.step");
    public static final SoundEvent BLOCK_PACKED_MUD_BREAK                   = create("block.packed_mud.break");
    public static final SoundEvent BLOCK_PACKED_MUD_FALL                    = create("block.packed_mud.fall");
    public static final SoundEvent BLOCK_PACKED_MUD_HIT                     = create("block.packed_mud.hit");
    public static final SoundEvent BLOCK_PACKED_MUD_PLACE                   = create("block.packed_mud.place");
    public static final SoundEvent BLOCK_PACKED_MUD_STEP                    = create("block.packed_mud.step");

    // Items
    public static final SoundEvent BUCKED_EMPTY_TADPOLE                     = create("item.bucket.empty_tadpole");
    public static final SoundEvent BUCKED_FILL_TADPOLE                      = create("item.bucket.fill_tadpole");
    public static final ImmutableList<SoundEvent> GOAT_HORN_SOUND_VARIANTS  = IntStream.range(0, 8).mapToObj(value -> {
        return create("item.goat_horn.sound." + value);
    }).collect(ImmutableList.toImmutableList());

    // Entities
    public static final SoundEvent ALLAY_AMBIENT_WITH_ITEM                  = create("entity.allay.ambient_with_item");
    public static final SoundEvent ALLAY_AMBIENT_WITHOUT_ITEM               = create("entity.allay.ambient_without_item");
    public static final SoundEvent ALLAY_DEATH                              = create("entity.allay.death");
    public static final SoundEvent ALLAY_HURT                               = create("entity.allay.hurt");
    public static final SoundEvent ALLAY_ITEM_GIVEN                         = create("entity.allay.item_given");
    public static final SoundEvent ALLAY_ITEM_TAKEN                         = create("entity.allay.item_taken");
    public static final SoundEvent ALLAY_ITEM_THROW                         = create("entity.allay.item_thrown");
    public static final SoundEvent FROG_AMBIENT                             = create("entity.frog.ambient");
    public static final SoundEvent FROG_DEATH                               = create("entity.frog.death");
    public static final SoundEvent FROG_EAT                                 = create("entity.frog.eat");
    public static final SoundEvent FROG_HURT                                = create("entity.frog.hurt");
    public static final SoundEvent FROG_LAY_SPAWN                           = create("entity.frog.lay_spawn");
    public static final SoundEvent FROG_LONG_JUMP                           = create("entity.frog.long_jump");
    public static final SoundEvent FROG_STEP                                = create("entity.frog.step");
    public static final SoundEvent FROG_TONGUE                              = create("entity.frog.tongue");
//    public static final SoundEvent PARROT_IMITATE_WARDEN                    = create("entity.parrot.imitate_warden");
    public static final SoundEvent TADPOLE_DEATH                            = create("entity.tadpole.death");
    public static final SoundEvent TADPOLE_FLOP                             = create("entity.tadpole.flop");
    public static final SoundEvent TADPOLE_GROW_UP                          = create("entity.tadpole.grow_up");
    public static final SoundEvent TADPOLE_HURT                             = create("entity.tadpole.hurt");
    public static final SoundEvent WARDEN_AMBIENT                           = create("entity.warden.ambient");
    public static final SoundEvent WARDEN_LISTENING                         = create("entity.warden.listening");
    public static final SoundEvent WARDEN_LISTENING_ANGRY                   = create("entity.warden.listening_angry");
    public static final SoundEvent WARDEN_ANGRY                             = create("entity.warden.angry");
    public static final SoundEvent WARDEN_HURT                              = create("entity.warden.hurt");
    public static final SoundEvent WARDEN_DEATH                             = create("entity.warden.death");
    public static final SoundEvent WARDEN_STEP                              = create("entity.warden.step");
    public static final SoundEvent WARDEN_TENDRIL_CLICKS                    = create("entity.warden.tendril_clicks");
    public static final SoundEvent WARDEN_HEARTBEAT                         = create("entity.warden.heartbeat");
    public static final SoundEvent WARDEN_AGITATED                          = create("entity.warden.agitated");
    public static final SoundEvent WARDEN_ATTACK_IMPACT                     = create("entity.warden.attack_impact");
    public static final SoundEvent WARDEN_ROAR                              = create("entity.warden.roar");
    public static final SoundEvent WARDEN_SNIFF                             = create("entity.warden.sniff");
    public static final SoundEvent WARDEN_EMERGE                            = create("entity.warden.emerge");
    public static final SoundEvent WARDEN_DIG                               = create("entity.warden.dig");
    public static final SoundEvent WARDEN_NEARBY_CLOSEST                    = create("entity.warden.nearby_closest");
    public static final SoundEvent WARDEN_NEARBY_CLOSER                     = create("entity.warden.nearby_closer");
    public static final SoundEvent WARDEN_NEARBY_CLOSE                      = create("entity.warden.nearby_close");
    public static final SoundEvent WARDEN_SONIC_BOOM                        = create("entity.warden.sonic_boom");
    public static final SoundEvent WARDEN_SONIC_CHARGE                      = create("entity.warden.sonic_charge");

    // Music
    public static final SoundEvent MUSIC_DISC_5                             = create("music_disc.5");
//    public static final SoundEvent MUSIC_BIOME_DEEP_DARK                    = create("music.overworld.deep_dark");
    
    public static SoundEvent create(String key) {
        SoundEvent sound = new SoundEvent(new ResourceLocation(WildBackport.MOD_ID, key));
        SOUNDS.register(key, () -> sound);
        return sound;
    }
}