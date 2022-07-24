package com.cursedcauldron.wildbackport.core.mixin.common;

import com.cursedcauldron.wildbackport.common.tag.WBBiomeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(Slime.class)
public class SlimeMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;getDifficulty()Lnet/minecraft/world/Difficulty;", shift = At.Shift.AFTER), method = "checkSlimeSpawnRules", cancellable = true)
    private static void WB$checkSlimeSpawnRules(EntityType<Slime> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random randomSource, CallbackInfoReturnable<Boolean> cir) {
        if (levelAccessor.getBiome(blockPos).is(WBBiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS) && blockPos.getY() > 50 && blockPos.getY() < 70 && randomSource.nextFloat() < 0.5F && randomSource.nextFloat() < levelAccessor.getMoonBrightness() && levelAccessor.getMaxLocalRawBrightness(blockPos) <= randomSource.nextInt(8)) {
            cir.setReturnValue(Mob.checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource));
        }
    }

}
