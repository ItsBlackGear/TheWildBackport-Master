package com.cursedcauldron.wildbackport.common.entities.brain.frog;

import com.cursedcauldron.wildbackport.common.entities.Frog;
import com.cursedcauldron.wildbackport.common.entities.access.api.Poses;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class Croak extends Behavior<Frog> {
    private int ticks;

    public Croak() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), 100);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Frog frog) {
        return frog.isInPose(Pose.STANDING);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Frog frog, long time) {
        return this.ticks < 40;
    }

    @Override
    protected void start(ServerLevel level, Frog frog, long time) {

        if (!frog.isInWaterOrBubble() && !frog.isInLava()) {
            frog.setPose(Poses.CROAKING.get());
            this.ticks = 0;
        }
    }

    @Override
    protected void stop(ServerLevel level, Frog frog, long time) {
        frog.setPose(Pose.STANDING);
    }

    @Override
    protected void tick(ServerLevel level, Frog frog, long time) {
        ++this.ticks;
    }
}