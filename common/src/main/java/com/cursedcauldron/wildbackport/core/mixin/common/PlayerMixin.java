package com.cursedcauldron.wildbackport.core.mixin.common;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.entities.access.WardenTracker;
import com.cursedcauldron.wildbackport.common.entities.warden.WardenSpawnTracker;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//<>

@Mixin(Player.class)
public class PlayerMixin implements WardenTracker {
    private final Player player = Player.class.cast(this);
    private WardenSpawnTracker spawnTracker = new WardenSpawnTracker(0, 0, 0);

    @Inject(method = "tick", at = @At("TAIL"))
    private void wb$tick(CallbackInfo ci) {
        if (!player.level.isClientSide) this.spawnTracker.tick();
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void wb$readData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("warden_spawn_tracker", 10)) WardenSpawnTracker.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, tag.get("warden_spawn_tracker"))).resultOrPartial(WildBackport.LOGGER::error).ifPresent(tracker -> this.spawnTracker = tracker);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void wb$writeData(CompoundTag tag, CallbackInfo ci) {
        WardenSpawnTracker.CODEC.encodeStart(NbtOps.INSTANCE, this.spawnTracker).resultOrPartial(WildBackport.LOGGER::error).ifPresent(tracker -> tag.put("warden_spawn_Tracker", tracker));
    }

    @Inject(method = "blockUsingShield", at = @At("HEAD"))
    private void wb$blockShield(LivingEntity entity, CallbackInfo ci) {
        if (entity instanceof Warden) player.disableShield(true);
    }

    @Override
    public WardenSpawnTracker getWardenSpawnTracker() {
        return this.spawnTracker;
    }
}