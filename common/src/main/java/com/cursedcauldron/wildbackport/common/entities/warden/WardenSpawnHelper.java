package com.cursedcauldron.wildbackport.common.entities.warden;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class WardenSpawnHelper {
    public static <T extends Mob> Optional<T> trySpawnMob(EntityType<T> type, MobSpawnType spawnType, ServerLevel level, BlockPos pos, int tries, int xzRange, int yRange) {
        BlockPos.MutableBlockPos mutable = pos.mutable();

        for(int i = 0; i < tries; ++i) {
            int x = Mth.randomBetweenInclusive(level.random, -xzRange, xzRange);
            int z = Mth.randomBetweenInclusive(level.random, -xzRange, xzRange);
            mutable.setWithOffset(pos, x, yRange, z);
            if (level.getWorldBorder().isWithinBounds(mutable) && moveToPossibleSpawnPosition(level, yRange, mutable)) {
                T mob = type.create(level, null, null, null, mutable, spawnType, false, false);
                if (mob != null) {
                    if (mob.checkSpawnRules(level, spawnType) && mob.checkSpawnObstruction(level)) {
                        level.addFreshEntityWithPassengers(mob);
                        return Optional.of(mob);
                    }

                    mob.discard();
                }
            }
        }

        return Optional.empty();
    }

    private static boolean moveToPossibleSpawnPosition(ServerLevel level, int yRange, BlockPos.MutableBlockPos pos) {
        BlockPos.MutableBlockPos toPos = (new BlockPos.MutableBlockPos()).set(pos);
        BlockState toState = level.getBlockState(toPos);

        for(int i = yRange; i >= -yRange; --i) {
            pos.move(Direction.DOWN);
            toPos.setWithOffset(pos, Direction.UP);
            BlockState state = level.getBlockState(pos);
            if (canSpawnOn(level, pos, state, toPos, toState)) {
                pos.move(Direction.UP);
                return true;
            }

            toState = state;
        }

        return false;
    }

    public static boolean canSpawnOn(ServerLevel level, BlockPos pos, BlockState state, BlockPos toPos, BlockState toState) {
        return toState.getCollisionShape(level, toPos).isEmpty() && Block.isFaceFull(state.getCollisionShape(level, pos), Direction.UP);
    }
}