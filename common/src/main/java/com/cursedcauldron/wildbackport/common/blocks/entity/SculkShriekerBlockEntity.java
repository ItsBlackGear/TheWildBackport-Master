package com.cursedcauldron.wildbackport.common.blocks.entity;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.particle.ShriekParticleOptions;
import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.blocks.SculkShriekerBlock;
import com.cursedcauldron.wildbackport.common.entities.warden.VibrationHandler;
import com.cursedcauldron.wildbackport.common.entities.Warden;
import com.cursedcauldron.wildbackport.common.entities.warden.WardenSpawnHelper;
import com.cursedcauldron.wildbackport.common.entities.warden.WardenSpawnTracker;
import com.cursedcauldron.wildbackport.common.registry.WBBlockEntities;
import com.cursedcauldron.wildbackport.common.registry.WBGameEvents;
import com.cursedcauldron.wildbackport.common.tag.WBGameEventTags;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

//<>

public class SculkShriekerBlockEntity extends BlockEntity implements VibrationHandler.VibrationConfig {
    private static final Int2ObjectMap<SoundEvent> SOUND_BY_LEVEL = Util.make(new Int2ObjectOpenHashMap<>(), map -> {
        map.put(1, WBSoundEvents.WARDEN_NEARBY_CLOSE);
        map.put(2, WBSoundEvents.WARDEN_NEARBY_CLOSER);
        map.put(3, WBSoundEvents.WARDEN_NEARBY_CLOSEST);
        map.put(4, WBSoundEvents.WARDEN_LISTENING_ANGRY);
    });
    private int warningLevel;
    private VibrationHandler listener = new VibrationHandler(new BlockPositionSource(this.worldPosition), 8, this, null, 0.0F, 0);

    public SculkShriekerBlockEntity(BlockPos pos, BlockState state) {
        super(WBBlockEntities.SCULK_SHRIEKER.get(), pos, state);
    }

    public VibrationHandler getListener() {
        return this.listener;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("warning_level", 99)) {
            this.warningLevel = tag.getInt("warning_level");
        }

        if (tag.contains("listener", 10)) {
            VibrationHandler.codec(this).parse(new Dynamic<>(NbtOps.INSTANCE, tag.getCompound("listener"))).resultOrPartial(WildBackport.LOGGER::error).ifPresent(listener -> this.listener = listener);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("warning_level", this.warningLevel);
        VibrationHandler.codec(this).encodeStart(NbtOps.INSTANCE, this.listener).resultOrPartial(WildBackport.LOGGER::error).ifPresent(listener -> tag.put("listener", listener));
    }

    @Override
    public TagKey<GameEvent> getListenableEvents() {
        return WBGameEventTags.WARDEN_CAN_LISTEN;
    }

    @Override
    public boolean shouldListen(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity) {
        return !this.isRemoved() && !this.getBlockState().getValue(SculkShriekerBlock.SHRIEKING) && tryGetPlayer(entity) != null;
    }

    @Nullable
    public static ServerPlayer tryGetPlayer(@Nullable Entity entity) {
        if (entity instanceof ServerPlayer player) {
            return player;
        } else {
            if (entity != null) {
                Entity passenger = entity.getControllingPassenger();
                if (passenger instanceof ServerPlayer player) {
                    return player;
                }
            }

            if (entity instanceof Projectile projectile) {
                Entity owner = projectile.getOwner();
                if (owner instanceof ServerPlayer player) {
                    return player;
                }
            }

            return null;
        }
    }

    @Override
    public void onSignalReceive(ServerLevel level, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity source, float distance) {
        this.tryShriek(level, tryGetPlayer(source != null ? source : entity));
    }

    public void tryShriek(ServerLevel level, @Nullable ServerPlayer player) {
        if (player != null) {
            BlockState state = this.getBlockState();
            if (!state.getValue(SculkShriekerBlock.SHRIEKING)) {
                this.warningLevel = 0;
                if (!this.canRespond(level) || this.tryToWarn(level, player)) {
                    this.shriek(level, player);
                }
            }
        }
    }

    private boolean tryToWarn(ServerLevel level, ServerPlayer player) {
        OptionalInt warning = WardenSpawnTracker.tryWarn(level, this.getBlockPos(), player);
        warning.ifPresent(warningLevel -> this.warningLevel = warningLevel);
        return warning.isPresent();
    }

    private void shriek(ServerLevel level, @Nullable Entity entity) {
        BlockPos pos = this.getBlockPos();
        BlockState state = this.getBlockState();
        level.setBlock(pos, state.setValue(SculkShriekerBlock.SHRIEKING, true), 2);
        level.scheduleTick(pos, state.getBlock(), 90);

        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), WBSoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 2.0F, 0.6F + level.random.nextFloat() * 0.4F);
        for (int i = 0; i < 10; i++) {
            int delay = i * 5;
            level.sendParticles(new ShriekParticleOptions(delay), pos.getX() + 0.5D, pos.getY() + SculkShriekerBlock.TOP_Y, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        level.gameEvent(entity, WBGameEvents.SHRIEK.get(), pos);
    }

    private boolean canRespond(ServerLevel level) {
        //TODO: add gamerule for warden spawning
        return this.getBlockState().getValue(SculkShriekerBlock.CAN_SUMMON) && level.getDifficulty() != Difficulty.PEACEFUL;
    }

    public void tryRespond(ServerLevel level) {
        if (this.canRespond(level) && this.warningLevel > 0) {
            if (!this.trySummonWarden(level)) {
                this.playWardenReplySound();
            }

            Warden.addDarknessToClosePlayers(level, Vec3.atCenterOf(this.getBlockPos()), null, 40);
        }
    }

    private void playWardenReplySound() {
        SoundEvent sound = SOUND_BY_LEVEL.get(this.warningLevel);
        if (sound != null && this.level != null) {
            BlockPos pos = this.getBlockPos();
            int x = pos.getX() + Mth.randomBetweenInclusive(this.level.random, -10, 10);
            int y = pos.getY() + Mth.randomBetweenInclusive(this.level.random, -10, 10);
            int z = pos.getZ() + Mth.randomBetweenInclusive(this.level.random, -10, 10);
            this.level.playSound(null, x, y, z, sound, SoundSource.HOSTILE, 5.0F, 1.0F);
        }
    }

    private boolean trySummonWarden(ServerLevel level) {
        return this.warningLevel >= 4 && WardenSpawnHelper.trySpawnMob(EntityType.IRON_GOLEM, MobSpawnType.TRIGGERED, level, this.getBlockPos(), 20, 5, 6).isPresent();
    }

    @Override
    public void onSignalSchedule() {
        this.setChanged();
    }
}