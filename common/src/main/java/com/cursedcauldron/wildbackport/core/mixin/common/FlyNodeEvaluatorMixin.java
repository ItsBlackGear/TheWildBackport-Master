package com.cursedcauldron.wildbackport.core.mixin.common;

import com.cursedcauldron.wildbackport.common.entities.Allay;
import com.cursedcauldron.wildbackport.core.mixin.access.WalkNodeEvaluatorAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//<>

@Mixin(FlyNodeEvaluator.class)
public abstract class FlyNodeEvaluatorMixin extends WalkNodeEvaluator {
    @Shadow protected abstract BlockPathTypes getCachedBlockPathType(int i, int j, int k);

    @Shadow @Nullable protected abstract Node getNode(int i, int j, int k);

    @Shadow public abstract BlockPathTypes getBlockPathType(BlockGetter blockGetter, int i, int j, int k);

    @Inject(method = "getStart", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableSet;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableSet;", shift = At.Shift.AFTER), cancellable = true)
    private void wb$start(CallbackInfoReturnable<Node> cir) {
        if (this.mob instanceof Allay allay) {
            for (BlockPos pos : allay.iteratePathfindingStartNodeCandidatePositions()) {
                BlockPathTypes types = this.getCachedBlockPathType(pos.getX(), pos.getY(), pos.getZ());
                if (this.mob.getPathfindingMalus(types) >= 0.0F) {
                    Node node = this.getNode(pos);
                    if (node != null) {
                        node.type = ((WalkNodeEvaluatorAccessor)this).callGetBlockPathType(this.mob, node.asBlockPos());
                        node.costMalus = this.mob.getPathfindingMalus(node.type);
                    }

                    cir.setReturnValue(node);
                }
            }
        }
    }
}