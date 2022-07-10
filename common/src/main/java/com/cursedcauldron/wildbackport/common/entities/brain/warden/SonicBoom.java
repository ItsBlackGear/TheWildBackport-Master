package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.cursedcauldron.wildbackport.client.registry.WBParticleTypes;
import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
import com.cursedcauldron.wildbackport.common.utils.MobUtils;
import com.cursedcauldron.wildbackport.core.mixin.access.DamageSourceAccessor;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;

//<>

public class SonicBoom extends Behavior<Warden> {
    private static final int TICKS_BEFORE_PLAYING_SOUND = Mth.ceil(34.0D);
    private static final int DURATION = Mth.ceil(60.0F);
    
    public SonicBoom() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, WBMemoryModules.SONIC_BOOM_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT, WBMemoryModules.SONIC_BOOM_SOUND_COOLDOWN.get(), MemoryStatus.REGISTERED, WBMemoryModules.SONIC_BOOM_SOUND_DELAY.get(), MemoryStatus.REGISTERED), DURATION);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Warden warden) {
        return MobUtils.closerThan(warden, warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get(), 15.0D, 20.0D);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Warden warden, long time) {
        return true;
    }

    @Override
    protected void start(ServerLevel level, Warden warden, long time) {
        warden.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, DURATION);
        warden.getBrain().setMemoryWithExpiry(WBMemoryModules.SONIC_BOOM_SOUND_DELAY.get(), Unit.INSTANCE, TICKS_BEFORE_PLAYING_SOUND);
        level.broadcastEntityEvent(warden, (byte)62);
        warden.playSound(WBSoundEvents.WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
    }

    @Override
    protected void tick(ServerLevel level, Warden warden, long time) {
        warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(target -> warden.getLookControl().setLookAt(target.position()));
        if (!warden.getBrain().hasMemoryValue(WBMemoryModules.SONIC_BOOM_SOUND_DELAY.get()) && !warden.getBrain().hasMemoryValue(WBMemoryModules.SONIC_BOOM_SOUND_COOLDOWN.get())) {
            warden.getBrain().setMemoryWithExpiry(WBMemoryModules.SONIC_BOOM_SOUND_COOLDOWN.get(), Unit.INSTANCE, DURATION - TICKS_BEFORE_PLAYING_SOUND);
            warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter(warden::isValidTarget).filter(target -> MobUtils.closerThan(warden, target, 15.0D, 20.0D)).ifPresent(target -> {
                Vec3 wardenPos = warden.position().add(0.0D, 1.6F, 0.0D);
                Vec3 distance = target.getEyePosition().subtract(wardenPos);
                Vec3 position = distance.normalize();

                for (int i = 1; i < Mth.floor(distance.length()) + 7; i++) {
                    Vec3 rayCharge = wardenPos.add(position.scale(i));
                    level.sendParticles(WBParticleTypes.SONIC_BOOM.get(), rayCharge.x, rayCharge.y, rayCharge.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }

                warden.playSound(WBSoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
                target.hurt(sonicBoom(warden), 10.0F);
                double yForce = 0.5D * (1.0D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                double xzForce = 2.5D * (1.0D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                target.push(position.x * xzForce, position.y * yForce, position.z * xzForce);
            });
        }
    }

    @Override
    protected void stop(ServerLevel level, Warden warden, long time) {
        setCooldown(warden, 40);
    }
    
    public static void setCooldown(LivingEntity entity, int cooldown) {
        entity.getBrain().setMemoryWithExpiry(WBMemoryModules.SONIC_BOOM_COOLDOWN.get(), Unit.INSTANCE, cooldown);
    }

    public static DamageSource sonicBoom(Entity entity) {
        return ((DamageSourceAccessor)new EntityDamageSource("sonic_boom", entity)).callBypassArmor().setMagic();
    }
}