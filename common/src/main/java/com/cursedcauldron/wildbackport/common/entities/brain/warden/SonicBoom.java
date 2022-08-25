package com.cursedcauldron.wildbackport.common.entities.brain.warden;

import com.cursedcauldron.wildbackport.client.registry.WBParticleTypes;
import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.registry.entity.WBMemoryModules;
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
    private static final int SOUND_DELAY = Mth.ceil(34.0D);
    private static final int DURATION = Mth.ceil(60.0F);

    public SonicBoom() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, WBMemoryModules.SONIC_BOOM_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT, WBMemoryModules.SONIC_BOOM_SOUND_COOLDOWN.get(), MemoryStatus.REGISTERED, WBMemoryModules.SONIC_BOOM_SOUND_DELAY.get(), MemoryStatus.REGISTERED), DURATION);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Warden warden) {
        return warden.closerThan(warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get(), 15.0D, 20.0D);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Warden warden, long time) {
        return true;
    }

    @Override
    protected void start(ServerLevel level, Warden warden, long time) {
        warden.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, DURATION);
        warden.getBrain().setMemoryWithExpiry(WBMemoryModules.SONIC_BOOM_SOUND_DELAY.get(), Unit.INSTANCE, SOUND_DELAY + 5);
        level.broadcastEntityEvent(warden, Warden.SONIC_BOOM);
        warden.playSound(WBSoundEvents.WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
    }

    @Override
    protected void tick(ServerLevel level, Warden warden, long time) {
        warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(target -> warden.getLookControl().setLookAt(target.position()));
        if (!warden.getBrain().hasMemoryValue(WBMemoryModules.SONIC_BOOM_SOUND_DELAY.get()) && !warden.getBrain().hasMemoryValue(WBMemoryModules.SONIC_BOOM_SOUND_COOLDOWN.get())) {
            warden.getBrain().setMemoryWithExpiry(WBMemoryModules.SONIC_BOOM_SOUND_COOLDOWN.get(), Unit.INSTANCE, DURATION - SOUND_DELAY + 5);
            warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter(warden::isValidTarget).filter(target -> warden.closerThan(target, 15.0D, 20.0D)).ifPresent(target -> {
                Vec3 sourcePos = warden.position().add(0.0D, 1.6F, 0.0D);
                Vec3 targetPos = target.getEyePosition().subtract(sourcePos);
                Vec3 distance = targetPos.normalize();

                for(int i = 1; i < Mth.floor(targetPos.length()) + 7; ++i) {
                    Vec3 charge = sourcePos.add(distance.scale(i));
                    level.sendParticles(WBParticleTypes.SONIC_BOOM.get(), charge.x, charge.y, charge.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }

                warden.playSound(WBSoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
                target.hurt(sonicBoom(warden), 10.0F);
                double yKnockback = 0.5D * (1.0D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                double xzKnockback = 2.5D * (1.0D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                target.push(distance.x() * xzKnockback, distance.y() * yKnockback, distance.z() * xzKnockback);
            });
        }
    }

    @Override
    protected void stop(ServerLevel level, Warden warden, long time) {
        SonicBoom.setCooldown(warden, 60L);
    }

    public static void setCooldown(LivingEntity entity, long cooldown) {
        entity.getBrain().setMemoryWithExpiry(WBMemoryModules.SONIC_BOOM_COOLDOWN.get(), Unit.INSTANCE, cooldown);
    }

    @SuppressWarnings("ConstantConditions")
    public static DamageSource sonicBoom(Entity entity) {
        return ((DamageSourceAccessor)new EntityDamageSource("sonic_boom", entity)).callBypassArmor().setMagic();
    }
}