package com.cursedcauldron.wildbackport.common.entities.brain.allay;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FlyingRandomStroll extends RandomStroll {
    public FlyingRandomStroll(float distance) {
        super(distance, true);
    }

    @Override @Nullable
    protected Vec3 getTargetPos(PathfinderMob mob) {
        Vec3 vector = mob.getViewVector(0.0F);
        return AirAndWaterRandomPos.getPos(mob, this.maxHorizontalDistance, this.maxVerticalDistance, -2, vector.x, vector.z, (float)Math.PI / 2.0F);
    }
}