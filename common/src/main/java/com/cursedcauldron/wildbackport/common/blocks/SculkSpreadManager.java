package com.cursedcauldron.wildbackport.common.blocks;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.particle.SculkChargeParticleOptions;
import com.cursedcauldron.wildbackport.client.registry.WBParticleTypes;
import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.tag.WBBlockTags;
import com.cursedcauldron.wildbackport.common.utils.DirectionUtils;
import com.cursedcauldron.wildbackport.common.utils.ModUtils;
import com.cursedcauldron.wildbackport.common.utils.ParticleUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

public class SculkSpreadManager {
    final boolean isWorldGen;
    private final TagKey<Block> replaceableBlocks;
    private final int extraBlockChance;
    private final int maxDistance;
    private final int spreadChance;
    private final int decayChance;
    private List<Cursor> cursors = new ArrayList<>();

    public SculkSpreadManager(boolean isWorldGen, TagKey<Block> replaceableBlocks, int extraBlockChance, int maxDistance, int spreadChance, int decayChance) {
        this.isWorldGen = isWorldGen;
        this.replaceableBlocks = replaceableBlocks;
        this.extraBlockChance = extraBlockChance;
        this.maxDistance = maxDistance;
        this.spreadChance = spreadChance;
        this.decayChance = decayChance;
    }

    public static SculkSpreadManager create() {
        return new SculkSpreadManager(false, WBBlockTags.SCULK_REPLACEABLE, 10, 4, 10, 5);
    }

    public static SculkSpreadManager createWorldGen() {
        return new SculkSpreadManager(true, WBBlockTags.SCULK_REPLACEABLE_WORLD_GEN, 50, 1, 5, 10);
    }

    public TagKey<Block> getReplaceableBlocks() {
        return this.replaceableBlocks;
    }

    public int getExtraBlockChance() {
        return this.extraBlockChance;
    }

    public int getMaxDistance() {
        return this.maxDistance;
    }

    public int getSpreadChance() {
        return this.spreadChance;
    }

    public int getDecayChance() {
        return this.decayChance;
    }

    public boolean isWorldGen() {
        return this.isWorldGen;
    }

    public void clearCursors() {
        this.cursors.clear();
    }

    public void readTag(CompoundTag tag) {
        if (tag.contains("cursors", 9)) {
            this.cursors.clear();
            List<Cursor> cursors = Cursor.CODEC.listOf().parse(new Dynamic<>(NbtOps.INSTANCE, tag.getList("cursors", 10))).resultOrPartial(WildBackport.LOGGER::error).orElseGet(ArrayList::new);
            int size = Math.min(cursors.size(), 32);

            for (int i = 0; i < size; i++) {
                this.addCursor(cursors.get(i));
            }
        }
    }

    public void writeTag(CompoundTag tag) {
        Cursor.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.cursors).resultOrPartial(WildBackport.LOGGER::error).ifPresent(value -> {
            tag.put("cursors", value);
        });
    }

    public void spread(BlockPos pos, int charge) {
        while (charge > 0) {
            int spread = Math.min(charge, 1000);
            this.addCursor(new Cursor(pos, spread));
            charge -= spread;
        }
    }

    private void addCursor(Cursor cursor) {
        if (this.cursors.size() < 32) {
            this.cursors.add(cursor);
        }
    }

    public void tick(LevelAccessor level, BlockPos pos, Random random, boolean shouldConvert) {
        Level instance = level instanceof Level side ? side : null;
        if (!this.cursors.isEmpty()) {
            List<Cursor> cursors = new ArrayList<>();
            Map<BlockPos, Cursor> cursorPositions = new HashMap<>();
            Object2IntMap<BlockPos> positions = new Object2IntOpenHashMap<>();

            for (Cursor cursor : this.cursors) {
                cursor.spread(level, pos, random, this, shouldConvert);
                if (cursor.charge <= 0) {
                    applySculkCharge(instance, cursor.getPos(), 0);
                } else {
                    BlockPos position = cursor.getPos();
                    positions.computeInt(position, (blockPos, charge) -> (charge == null ? 0 : charge) + cursor.charge);
                    Cursor target = cursorPositions.get(position);
                    if (target == null) {
                        cursorPositions.put(position, cursor);
                        cursors.add(cursor);
                    } else if (!this.isWorldGen() && cursor.charge + target.charge <= 1000) {
                        target.merge(cursor);
                    } else {
                        cursors.add(cursor);
                        if (cursor.charge < target.charge) {
                            cursorPositions.put(position, cursor);
                        }
                    }
                }
            }

            for (Object2IntMap.Entry<BlockPos> entry : positions.object2IntEntrySet()) {
                BlockPos position = entry.getKey();
                int exp = entry.getIntValue();
                Cursor cursor = cursorPositions.get(position);
                Set<Direction> directions = cursor == null ? null : cursor.getFacings();
                if (exp > 0 && directions != null) {
                    int charge = (int)(Math.log1p(exp) / (double)2.3F) + 1;
                    int data = (charge << 6) + SculkVeinBlock.directionsToFlag(directions);
                    applySculkCharge(instance, cursor.getPos(), data);
                }
            }

            this.cursors = cursors;
        }
    }

    public static class Cursor {
        private static final ObjectArrayList<Vec3i> OFFSETS = Util.make(new ObjectArrayList<>(18), positions -> {
            BlockPos.betweenClosedStream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1)).filter(pos -> {
                return (pos.getX() == 0 || pos.getY() == 0 || pos.getZ() == 0) && pos != BlockPos.ZERO;
            }).map(BlockPos::immutable).forEach(positions::add);
        });
        private BlockPos pos;
        private int charge;
        private int updateDelay;
        private int decayDelay;
        @Nullable
        private Set<Direction> facings;
        private static final Codec<Set<Direction>> DIRECTION_SET = Direction.CODEC.listOf().xmap(directions -> Sets.newEnumSet(directions, Direction.class), Lists::newArrayList);
        public static final Codec<Cursor> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(Cursor::getPos), Codec.intRange(0, 1000).fieldOf("charge").orElse(0).forGetter(Cursor::getCharge), Codec.intRange(0, 1).fieldOf("decay_delay").orElse(1).forGetter(Cursor::getDecayDelay), Codec.intRange(0, Integer.MAX_VALUE).fieldOf("update_delay").orElse(0).forGetter(cursor -> {
                return cursor.updateDelay;
            }), DIRECTION_SET.optionalFieldOf("facings").forGetter(cursor -> {
                return Optional.ofNullable(cursor.getFacings());
            })).apply(instance, Cursor::new);
        });

        private Cursor(BlockPos pos, int charge, int decayDelay, int updateDelay, Optional<Set<Direction>> facings) {
            this.pos = pos;
            this.charge = charge;
            this.decayDelay = decayDelay;
            this.updateDelay = updateDelay;
            this.facings = facings.orElse(null);
        }

        public Cursor(BlockPos pos, int charge) {
            this(pos, charge, 1, 0, Optional.empty());
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public int getCharge() {
            return this.charge;
        }

        public int getDecayDelay() {
            return this.decayDelay;
        }

        @Nullable
        public Set<Direction> getFacings() {
            return this.facings;
        }

        private boolean canSpread(LevelAccessor level, BlockPos pos, boolean isWorldGen) {
            if (this.charge <= 0) {
                return false;
            } else if (isWorldGen) {
                return true;
            } else if (level instanceof ServerLevel server) {
                return server.shouldTickBlocksAt(ChunkPos.asLong(pos));
            } else {
                return false;
            }
        }

        public void spread(LevelAccessor level, BlockPos pos, Random random, SculkSpreadManager spreadManager, boolean shouldConvert) {
            if (this.canSpread(level, pos, spreadManager.isWorldGen)) {
                if (this.updateDelay > 0) {
                    --this.updateDelay;
                } else {
                    BlockState state = level.getBlockState(this.pos);
                    SculkSpreadable spreadable = getSpreadable(state);
                    if (shouldConvert && spreadable.spread(level, this.pos, state, this.facings, spreadManager.isWorldGen())) {
                        if (spreadable.shouldConvertToSpreadable()) {
                            state = level.getBlockState(this.pos);
                            spreadable = getSpreadable(state);
                        }

                        level.playSound(null, this.pos, WBSoundEvents.BLOCK_SCULK_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    this.charge = spreadable.spread(this, level, pos, random, spreadManager, shouldConvert);
                    if (this.charge <= 0) {
                        spreadable.spreadAtSamePosition(level, state, this.pos, random);
                    } else {
                        BlockPos target = getSpreadPos(level, this.pos, random);
                        if (target != null) {
                            spreadable.spreadAtSamePosition(level, state, this.pos, random);
                            this.pos = target.immutable();
                            if (spreadManager.isWorldGen() && !this.pos.closerThan(new Vec3i(pos.getX(), this.pos.getY(), pos.getZ()), 15.0D)) {
                                this.charge = 0;
                                return;
                            }

                            state = level.getBlockState(target);
                        }

                        if (state.getBlock() instanceof SculkSpreadable) {
                            this.facings = SculkVeinBlock.collectDirections(state);
                        }

                        this.decayDelay = spreadable.getDecay(this.decayDelay);
                        this.updateDelay = spreadable.getUpdate();
                    }
                }
            }
        }

        void merge(Cursor cursor) {
            this.charge += cursor.charge;
            cursor.charge = 0;
            this.updateDelay = Math.min(this.updateDelay, cursor.updateDelay);
        }

        private static SculkSpreadable getSpreadable(BlockState state) {
            return state.getBlock() instanceof SculkSpreadable spreadable ? spreadable : SculkSpreadable.DEFAULT;
        }

        private static List<Vec3i> shuffleOffsets(Random random) {
            return ModUtils.copyShuffled(OFFSETS, random);
        }

        @Nullable
        private static BlockPos getSpreadPos(LevelAccessor level, BlockPos pos, Random random) {
            BlockPos.MutableBlockPos target = pos.mutable();
            BlockPos.MutableBlockPos source = pos.mutable();

            for (Vec3i offset : shuffleOffsets(random)) {
                source.setWithOffset(pos, offset);
                BlockState state = level.getBlockState(source);
                if (state.getBlock() instanceof SculkSpreadable && canSpread(level, pos, source)) {
                    target.set(source);
                    if (SculkVeinBlock.veinCoversSculkReplaceable(level, state, source)) {
                        break;
                    }
                }
            }

            return target.equals(pos) ? null : target;
        }

        private static boolean canSpread(LevelAccessor level, BlockPos source, BlockPos target) {
            if (source.distManhattan(target) == 1) {
                return true;
            } else {
                BlockPos pos = target.subtract(target);
                Direction xAxis = Direction.fromAxisAndDirection(Direction.Axis.X, pos.getX() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
                Direction yAxis = Direction.fromAxisAndDirection(Direction.Axis.Y, pos.getY() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
                Direction zAxis = Direction.fromAxisAndDirection(Direction.Axis.Z, pos.getZ() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
                if (pos.getX() == 0) {
                    return canSpread(level, source, yAxis) || canSpread(level, source, zAxis);
                } else if (pos.getY() == 0) {
                    return canSpread(level, source, xAxis) || canSpread(level, source, zAxis);
                } else {
                    return canSpread(level, source, xAxis) || canSpread(level, source, yAxis);
                }
            }
        }

        private static boolean canSpread(LevelAccessor level, BlockPos pos, Direction direction) {
            BlockPos facing = pos.relative(direction);
            return !level.getBlockState(facing).isFaceSturdy(level, facing, direction.getOpposite());
        }
    }

    public static void applySculkCharge(Level level, BlockPos pos, int data) {
        if (level == null) return;

        Random random = level.getRandom();
        ClientLevel client = level instanceof ClientLevel side ? side : null;
        ServerLevel server = level instanceof ServerLevel side ? side : null;

        int charge = data >> 6;
        if (charge > 0) {
            if (random.nextFloat() < (float)charge * 0.2F) {
                float volume = 0.15F + 0.05F * (float)charge * (float)charge * random.nextFloat();
                float pitch = 0.4F * (float)charge - 0.2F * random.nextFloat();
                if (client != null) client.playLocalSound(pos, WBSoundEvents.BLOCK_SCULK_CHARGE, SoundSource.BLOCKS, volume, pitch, false);
            }

            int facings = (byte)(data & 63);
            UniformInt spread = UniformInt.of(0, charge);
            Supplier<Vec3> velocities = () -> {
                return new Vec3(Mth.nextDouble(random, -0.005D, 0.005D), Mth.nextDouble(random, -0.005D, 0.005D), Mth.nextDouble(random, -0.005D, 0.005D));
            };

            if (facings == 0) {
                for (Direction direction : Direction.values()) {
                    float roll = direction == Direction.DOWN ? (float)Math.PI : 0.0F;
                    double offset = direction == Direction.UP || direction == Direction.DOWN ? 0.32D : 0.57D;
                    ParticleUtils.spawnParticles(level, pos, new SculkChargeParticleOptions(roll), spread, direction, velocities, offset);
                }
            } else {
                for (Direction direction : DirectionUtils.unpack((byte)data)) {
                    float roll = direction == Direction.UP ? (float)Math.PI : 0.0F;
                    ParticleUtils.spawnParticles(level, pos, new SculkChargeParticleOptions(roll), spread, direction, velocities, 0.35D);
                }
            }
        } else {
            if (client != null) client.playLocalSound(pos, WBSoundEvents.BLOCK_SCULK_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            boolean fullBlock = level.getBlockState(pos).isCollisionShapeFullBlock(level, pos);
            int tries = fullBlock ? 40 : 20;
            float spread = fullBlock ? 0.45F : 0.25F;

            for (int i = 0; i < tries; i++) {
                float x = 2.0F * random.nextFloat() - 1.0F;
                float y = 2.0F * random.nextFloat() - 1.0F;
                float z = 2.0F * random.nextFloat() - 1.0F;
                if (server != null) server.sendParticles(WBParticleTypes.SCULK_CHARGE_POP.get(), (double)pos.getX() + 0.5D + (double)(x * spread), (double)pos.getY() + 0.5D + (double)(y * spread), (double)pos.getZ() + 0.5D + (double)(z * spread), 1, x * 0.07F, y * 0.07F, z * 0.07F, 0.0D);
            }
        }
    }
}