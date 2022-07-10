package com.cursedcauldron.wildbackport.common.utils;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PositionUtils {
    public static final Codec<Vec3> VEC_CODEC = Codec.DOUBLE.listOf().comapFlatMap(values -> {
        return Util.fixedSize(values, 3).map(pos -> {
            return new Vec3(pos.get(0), pos.get(1), pos.get(2));
        });
    }, pos -> {
        return List.of(pos.x(), pos.y(), pos.z());
    });

    public static BlockPos toBlockPos(Vec3 pos) {
        return new BlockPos(pos.x, pos.y, pos.z);
    }

    public static Vec3 toVec(BlockPos pos) {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3 relative(Vec3 pos, Direction direction, double offset) {
        Vec3i normal = direction.getNormal();
        return new Vec3(pos.x + offset * (double)normal.getX(), pos.y + offset * (double)normal.getY(), pos.z + offset * (double)normal.getZ());
    }
}