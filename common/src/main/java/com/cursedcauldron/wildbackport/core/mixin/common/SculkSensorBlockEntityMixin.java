package com.cursedcauldron.wildbackport.core.mixin.common;

import com.cursedcauldron.wildbackport.common.entities.access.Vibration;
import com.cursedcauldron.wildbackport.common.registry.WBGameEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkSensorBlockEntity.class)
public class SculkSensorBlockEntityMixin extends BlockEntity {
    @Shadow @Final private VibrationListener listener;

    public SculkSensorBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "onSignalReceive", at = @At("HEAD"))
    private void receiveSignal(Level level, GameEventListener listener, GameEvent event, int delay, CallbackInfo ci) {
        if (!level.isClientSide() && SculkSensorBlock.canActivate(this.getBlockState())) {
            Vibration.Instance instance = Vibration.Instance.of(this.listener);
            level.gameEvent(instance.getEntity(), WBGameEvents.SCULK_SENSOR_TENDRILS_CLICKING.get(), instance.getPos());
        }
    }
}