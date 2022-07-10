package com.cursedcauldron.wildbackport.core.mixin.common;

import com.cursedcauldron.wildbackport.common.entities.access.EntityExperience;
import com.cursedcauldron.wildbackport.common.registry.WBGameEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityExperience {
    @Shadow @Nullable protected Player lastHurtByPlayer;
    private boolean expDroppingDisabled;

    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Shadow protected abstract int getExperienceReward(Player player);

    @Inject(method = "die", at = @At("HEAD"))
    private void wb$die(DamageSource source, CallbackInfo ci) {
        this.gameEvent(WBGameEvents.ENTITY_DIE.get());
    }

    @Override
    public void disableExpDrop() {
        this.expDroppingDisabled = true;
    }

    @Override
    public boolean isExpDropDisabled() {
        return this.expDroppingDisabled;
    }

    @Override
    public int getExpToDrop() {
        return this.getExperienceReward(this.lastHurtByPlayer);
    }

    @Inject(method = "dropExperience", at = @At("HEAD"), cancellable = true)
    private void wb$dropExp(CallbackInfo ci) {
        if (this.isExpDropDisabled()) ci.cancel();
    }
}