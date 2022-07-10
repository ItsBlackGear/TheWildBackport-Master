package com.cursedcauldron.wildbackport.common.blocks.entity;

import com.cursedcauldron.wildbackport.client.registry.WBCriteriaTriggers;
import com.cursedcauldron.wildbackport.common.blocks.SculkCatalystBlock;
import com.cursedcauldron.wildbackport.common.blocks.SculkSpreadManager;
import com.cursedcauldron.wildbackport.common.entities.access.EntityExperience;
import com.cursedcauldron.wildbackport.common.registry.WBBlockEntities;
import com.cursedcauldron.wildbackport.common.registry.WBGameEvents;
import com.cursedcauldron.wildbackport.common.utils.PositionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import org.jetbrains.annotations.Nullable;

//<>

public class SculkCatalystBlockEntity extends BlockEntity implements GameEventListener {
    private final BlockPositionSource positionSource = new BlockPositionSource(this.worldPosition);
    private final SculkSpreadManager spreadManager = SculkSpreadManager.create();

    public SculkCatalystBlockEntity(BlockPos pos, BlockState state) {
        super(WBBlockEntities.SCULK_CATALYST.get(), pos, state);
    }

    @Override
    public PositionSource getListenerSource() {
        return this.positionSource;
    }

    @Override
    public int getListenerRadius() {
        return 8;
    }

    @Override
    public boolean handleGameEvent(Level level, GameEvent event, @Nullable Entity entity, BlockPos pos) {
        if (!this.isRemoved()) {
            if (event == WBGameEvents.ENTITY_DIE.get()) {
                if (entity instanceof LivingEntity living && living instanceof EntityExperience mob) {
                    if (!mob.isExpDropDisabled()) {
                        int charge = mob.getExpToDrop();
                        if (!living.isBaby() && charge > 0) {
                            this.spreadManager.spread(new BlockPos(PositionUtils.relative(PositionUtils.toVec(pos), Direction.UP, 0.5D)), charge);
                            LivingEntity attacker = living.getLastHurtByMob();
                            if (attacker instanceof ServerPlayer player) {
                                DamageSource source = living.getLastDamageSource() == null ? DamageSource.playerAttack(player) : living.getLastDamageSource();
                                WBCriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.trigger(player, entity, source);
                            }
                        }

                        mob.disableExpDrop();
                        SculkCatalystBlock.bloom((ServerLevel) level, this.worldPosition, this.getBlockState(), level.getRandom());
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SculkCatalystBlockEntity catalyst) {
        catalyst.spreadManager.tick(level, pos, level.getRandom(), true);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.spreadManager.readTag(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.spreadManager.writeTag(tag);
    }
}