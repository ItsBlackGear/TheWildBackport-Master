package com.cursedcauldron.wildbackport.common.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class FactorCalculationData {
    public static final Codec<FactorCalculationData> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("padding_duration").forGetter(data -> {
            return data.paddingDuration;
        }), Codec.FLOAT.fieldOf("factor_start").orElse(0.0F).forGetter(data -> {
            return data.factorStart;
        }), Codec.FLOAT.fieldOf("factor_target").orElse(1.0F).forGetter(data -> {
            return data.factorTarget;
        }), Codec.FLOAT.fieldOf("factor_current").orElse(0.0F).forGetter(data -> {
            return data.factorCurrent;
        }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("effect_changed_timestamp").orElse(0).forGetter(data -> {
            return data.effectChangedTimestamp;
        }), Codec.FLOAT.fieldOf("factor_previous_frame").orElse(0.0F).forGetter(data -> {
            return data.factorPreviousFrame;
        }), Codec.BOOL.fieldOf("had_effect_last_tick").orElse(false).forGetter(data -> {
            return data.hadEffectLastTick;
        })).apply(instance, FactorCalculationData::new);
    });
    private final int paddingDuration;
    private float factorStart;
    private float factorTarget;
    private float factorCurrent;
    public int effectChangedTimestamp;
    private float factorPreviousFrame;
    private boolean hadEffectLastTick;

    public FactorCalculationData(int paddingDuration, float factorStart, float factorTarget, float factorCurrent, int effectChangedTimestamp, float factorPreviousFrame, boolean hadEffectLastTick) {
        this.paddingDuration = paddingDuration;
        this.factorStart = factorStart;
        this.factorTarget = factorTarget;
        this.factorCurrent = factorCurrent;
        this.effectChangedTimestamp = effectChangedTimestamp;
        this.factorPreviousFrame = factorPreviousFrame;
        this.hadEffectLastTick = hadEffectLastTick;
    }

    public FactorCalculationData(int paddingDuration) {
        this(paddingDuration, 0.0F, 1.0F, 0.0F, 0, 0.0F, false);
    }

    public void update(MobEffectInstance instance) {
        this.factorPreviousFrame = this.factorCurrent;
        boolean inRange = instance.getDuration() > this.paddingDuration;

        if (this.hadEffectLastTick != inRange) {
            this.hadEffectLastTick = inRange;
            this.effectChangedTimestamp = instance.getDuration();
            this.factorStart = this.factorCurrent;
            this.factorTarget = inRange ? 1.0F : 0.0F;
        }

        float delta = Mth.clamp(((float)this.effectChangedTimestamp - (float)instance.getDuration()) / (float)this.paddingDuration, 0.0F, 1.0F);
        this.factorCurrent = Mth.lerp(delta, this.factorCurrent, this.factorTarget);
    }

    public float lerp(LivingEntity entity, float factor) {
        if (entity.isRemoved()) this.factorPreviousFrame = this.factorCurrent;

        return Mth.lerp(factor, this.factorPreviousFrame, this.factorCurrent);
    }
}