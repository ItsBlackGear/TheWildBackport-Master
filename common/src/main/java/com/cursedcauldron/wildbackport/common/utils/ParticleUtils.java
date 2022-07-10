package com.cursedcauldron.wildbackport.common.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class ParticleUtils {
    public static void spawnParticles(Level world, BlockPos pos, ParticleOptions effect, IntProvider count, Direction direction, Supplier<Vec3> velocity, double offset) {
        int sample = count.sample(world.getRandom());
        for (int i = 0; i < sample; ++i) {
            spawnParticle(world, pos, direction, effect, velocity.get(), offset);
        }
    }

    public static void spawnParticle(Level level, BlockPos pos, Direction direction, ParticleOptions effect, Vec3 velocity, double offset) {
        Vec3 center = Vec3.atCenterOf(pos);
        int xStep = direction.getStepX();
        int yStep = direction.getStepY();
        int zStep = direction.getStepZ();
        double x = center.x + (xStep == 0 ? Mth.nextDouble(level.getRandom(), -0.5D, 0.5D) : (double)xStep * offset);
        double y = center.y + (yStep == 0 ? Mth.nextDouble(level.getRandom(), -0.5D, 0.5D) : (double)yStep * offset);
        double z = center.z + (zStep == 0 ? Mth.nextDouble(level.getRandom(), -0.5D, 0.5D) : (double)zStep * offset);
        double xVelocity = xStep == 0 ? velocity.x() : 0.0D;
        double yVelocity = yStep == 0 ? velocity.y() : 0.0D;
        double zVelocity = zStep == 0 ? velocity.z() : 0.0D;
        ServerLevel server = level instanceof ServerLevel side ? side : null;
        if (server != null) server.sendParticles(effect, x, y, z, 1, xVelocity, yVelocity, zVelocity, 0.0D);
    }
}
