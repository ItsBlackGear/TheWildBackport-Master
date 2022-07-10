package com.cursedcauldron.wildbackport.client.registry;

import com.cursedcauldron.wildbackport.client.sound.CoreSoundType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

//<>

public class WBSoundTypes {
    public static final SoundType SCULK                     = create(WBSoundEvents.BLOCK_SCULK_BREAK, WBSoundEvents.BLOCK_SCULK_STEP, WBSoundEvents.BLOCK_SCULK_PLACE, WBSoundEvents.BLOCK_SCULK_HIT, WBSoundEvents.BLOCK_SCULK_FALL);
    public static final SoundType SCULK_CATALYST            = create(WBSoundEvents.BLOCK_SCULK_CATALYST_BREAK, WBSoundEvents.BLOCK_SCULK_CATALYST_STEP, WBSoundEvents.BLOCK_SCULK_CATALYST_PLACE, WBSoundEvents.BLOCK_SCULK_CATALYST_HIT, WBSoundEvents.BLOCK_SCULK_CATALYST_FALL);
    public static final SoundType SCULK_VEIN                = create(WBSoundEvents.BLOCK_SCULK_VEIN_BREAK, WBSoundEvents.BLOCK_SCULK_VEIN_STEP, WBSoundEvents.BLOCK_SCULK_VEIN_PLACE, WBSoundEvents.BLOCK_SCULK_VEIN_HIT, WBSoundEvents.BLOCK_SCULK_VEIN_FALL);
    public static final SoundType SCULK_SHRIEKER            = create(WBSoundEvents.BLOCK_SCULK_SHRIEKER_BREAK, WBSoundEvents.BLOCK_SCULK_SHRIEKER_STEP, WBSoundEvents.BLOCK_SCULK_SHRIEKER_PLACE, WBSoundEvents.BLOCK_SCULK_SHRIEKER_HIT, WBSoundEvents.BLOCK_SCULK_SHRIEKER_FALL);
    public static final SoundType FROGLIGHT                 = create(WBSoundEvents.BLOCK_FROGLIGHT_BREAK, WBSoundEvents.BLOCK_FROGLIGHT_STEP, WBSoundEvents.BLOCK_FROGLIGHT_PLACE, WBSoundEvents.BLOCK_FROGLIGHT_HIT, WBSoundEvents.BLOCK_FROGLIGHT_FALL);
    public static final SoundType FROGSPAWN                 = create(WBSoundEvents.BLOCK_FROGSPAWN_BREAK, WBSoundEvents.BLOCK_FROGSPAWN_STEP, WBSoundEvents.BLOCK_FROGSPAWN_PLACE, WBSoundEvents.BLOCK_FROGSPAWN_HIT, WBSoundEvents.BLOCK_FROGSPAWN_FALL);
    public static final SoundType MANGROVE_ROOTS            = create(WBSoundEvents.BLOCK_MANGROVE_ROOTS_BREAK, WBSoundEvents.BLOCK_MANGROVE_ROOTS_STEP, WBSoundEvents.BLOCK_MANGROVE_ROOTS_PLACE, WBSoundEvents.BLOCK_MANGROVE_ROOTS_HIT, WBSoundEvents.BLOCK_MANGROVE_ROOTS_FALL);
    public static final SoundType MUD                       = create(WBSoundEvents.BLOCK_MUD_BREAK, WBSoundEvents.BLOCK_MUD_STEP, WBSoundEvents.BLOCK_MUD_PLACE, WBSoundEvents.BLOCK_MUD_HIT, WBSoundEvents.BLOCK_MUD_FALL);
    public static final SoundType MUD_BRICKS                = create(WBSoundEvents.BLOCK_MUD_BRICKS_BREAK, WBSoundEvents.BLOCK_MUD_BRICKS_STEP, WBSoundEvents.BLOCK_MUD_BRICKS_PLACE, WBSoundEvents.BLOCK_MUD_BRICKS_HIT, WBSoundEvents.BLOCK_MUD_BRICKS_FALL);
    public static final SoundType MUDDY_MANGROVE_ROOTS      = create(WBSoundEvents.BLOCK_MUDDY_MANGROVE_ROOTS_BREAK, WBSoundEvents.BLOCK_MUDDY_MANGROVE_ROOTS_STEP, WBSoundEvents.BLOCK_MUDDY_MANGROVE_ROOTS_PLACE, WBSoundEvents.BLOCK_MUDDY_MANGROVE_ROOTS_HIT, WBSoundEvents.BLOCK_MUDDY_MANGROVE_ROOTS_FALL);
    public static final SoundType PACKED_MUD                = create(WBSoundEvents.BLOCK_PACKED_MUD_BREAK, WBSoundEvents.BLOCK_PACKED_MUD_STEP, WBSoundEvents.BLOCK_PACKED_MUD_PLACE, WBSoundEvents.BLOCK_PACKED_MUD_HIT, WBSoundEvents.BLOCK_PACKED_MUD_FALL);

    public static SoundType create(SoundEvent breakSnd, SoundEvent stepSnd, SoundEvent placeSnd, SoundEvent hitSnd, SoundEvent fallSnd) {
        return new CoreSoundType(() -> breakSnd, () -> stepSnd, () -> placeSnd, () -> hitSnd, () -> fallSnd);
    } 
}