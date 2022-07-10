package com.cursedcauldron.wildbackport.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BlockProperties {
    // Block Properties
    public static final BooleanProperty SHRIEKING   = BooleanProperty.create("shrieking");
    public static final BooleanProperty CAN_SUMMON  = BooleanProperty.create("can_summon");
    public static final BooleanProperty BLOOM       = BooleanProperty.create("bloom");
    public static final IntegerProperty AGE_4       = IntegerProperty.create("age", 0, 4);

    // Block values
    public static boolean always(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> type) {
        return true;
    }

    public static boolean never(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> type) {
        return false;
    }

    public static boolean ocelotOrParrot(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> entity) {
        return entity == EntityType.OCELOT || entity == EntityType.PARROT;
    }

    public static boolean always(BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }

    public static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }
}