package com.cursedcauldron.wildbackport.client.animation.api;

import com.cursedcauldron.wildbackport.core.mixin.access.ModelPartAccessor;
import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

//<>

@Environment(EnvType.CLIENT)
public abstract class AnimatedModel<E extends Entity> extends HierarchicalModel<E> {
    private static final Vector3f CACHE = new Vector3f();

    public AnimatedModel() {
        this(RenderType::entityCutoutNoCull);
    }

    public AnimatedModel(Function<ResourceLocation, RenderType> function) {
        super(function);
    }

    public Optional<ModelPart> getChild(String name) {
        return this.root().getAllParts().filter(part -> ((ModelPartAccessor)(Object)part).getChildren().containsKey(name)).findFirst().map(part -> part.getChild(name));
    }

    protected void animate(AnimationState state, Animation animation, float animationProgress) {
        this.animate(state, animation, animationProgress, 1.0F);
    }

    protected void animate(AnimationState animationState, Animation animation, float animationProgress, float speedMultiplier) {
        animationState.run(animationProgress, speedMultiplier);
        animationState.run(state -> AnimationHelper.animate(this, animation, state.runningTime(), 1.0F, CACHE));
    }
}