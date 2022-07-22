package com.cursedcauldron.wildbackport.common.blocks;

import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import com.cursedcauldron.wildbackport.common.tag.WBBlockTags;
import com.cursedcauldron.wildbackport.core.mixin.access.PointedDripstoneBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

public record DrippingFluid(BlockPos pos, Fluid fluid, BlockState sourceState) {
    public static void fillCauldron(BlockState state, ServerLevel world, BlockPos blockPos, CallbackInfo ci) {
        Optional<DrippingFluid> fluidAbove = getFluidAboveStalactite(world, blockPos, state);
        if (fluidAbove.isPresent()) {
            Fluid fluid = fluidAbove.get().fluid;
            if (fluidAbove.get().sourceState().is(WBBlockTags.MUD) && fluid == Fluids.WATER) {
                world.setBlockAndUpdate(fluidAbove.get().pos, Blocks.CLAY.defaultBlockState());
                world.levelEvent(1504, blockPos, 0);
                ci.cancel();
            }
        }
    }

    public static Optional<DrippingFluid> getFluidAboveStalactite(Level level, BlockPos pos, BlockState state) {
        return !PointedDripstoneBlockAccessor.callIsStalactite(state) ? Optional.empty() : PointedDripstoneBlockAccessor.callFindRootBlock(level, pos, state, 11).map(blockPos -> {
            BlockPos position = blockPos.above();
            BlockState sourceState = level.getBlockState(position);
            Fluid fluid = sourceState.is(WBBlockTags.MUD) && !level.dimensionType().ultraWarm() ? Fluids.WATER : level.getFluidState(position).getType();
            return new DrippingFluid(position, fluid, sourceState);
        });
    }
}