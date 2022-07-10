package com.cursedcauldron.wildbackport.common.entities.warden;

import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;

import java.util.Arrays;

//<>

public enum Angriness {
    CALM(0, WBSoundEvents.WARDEN_AMBIENT, WBSoundEvents.WARDEN_LISTENING),
    AGITATED(40, WBSoundEvents.WARDEN_AGITATED, WBSoundEvents.WARDEN_LISTENING_ANGRY),
    ANGRY(80, WBSoundEvents.WARDEN_ANGRY, WBSoundEvents.WARDEN_LISTENING_ANGRY);

    private static final Angriness[] VALUES = Util.make(Angriness.values(), values -> Arrays.sort(values, (a, b) -> Integer.compare(b.threshold, a.threshold)));
    private final int threshold;
    private final SoundEvent sound;
    private final SoundEvent listeningSound;

    Angriness(int threshold, SoundEvent sound, SoundEvent listeningSound) {
        this.threshold = threshold;
        this.sound = sound;
        this.listeningSound = listeningSound;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    public SoundEvent getListeningSound() {
        return this.listeningSound;
    }

    public static Angriness getForAnger(int anger) {
        for (Angriness angriness : VALUES) {
            if (anger >= angriness.threshold) {
                return angriness;
            }
        }

        return CALM;
    }

    public boolean isAngry() {
        return this == ANGRY;
    }
}