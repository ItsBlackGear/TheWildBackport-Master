package com.cursedcauldron.wildbackport.core.mixin.common;

import com.cursedcauldron.wildbackport.common.entities.access.Vibration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VibrationListener.class)
public class VibrationListenerMixin implements Vibration.Instance {
    private BlockPos pos;
    private Entity entity;
    private Entity source;
    private Vibration vibration;

    @Inject(method = "handleGameEvent", at = @At("HEAD"))
    private void handleSource(Level level, GameEvent event, Entity entity, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (level instanceof ServerLevel server) {
            this.setPos(pos);
            this.setVibration(new Vibration(entity));
            this.setEntity(this.vibration.getEntity(server).orElse(null));
            this.setSource(this.vibration.getOwner(server).orElse(null));
        }
    }

    @Override
    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public BlockPos getPos() {
        return this.pos;
    }

    @Override
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public void setSource(Entity entity) {
        this.source = entity;
    }

    @Override
    public Entity getSource() {
        return this.source;
    }

    @Override
    public void setVibration(Vibration vibration) {
        this.vibration = vibration;
    }
}