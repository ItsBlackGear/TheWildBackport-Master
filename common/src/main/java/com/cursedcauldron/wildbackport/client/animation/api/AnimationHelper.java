package com.cursedcauldron.wildbackport.client.animation.api;

import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//<>

@Environment(EnvType.CLIENT)
public class AnimationHelper {
    public static void animate(AnimatedModel<?> model, Animation animation, long runningTime, float speed, Vector3f cache) {
        float runningSeconds = getRunningSeconds(animation, runningTime);

        for (Map.Entry<String, List<Transformation>> animations : animation.boneAnimations().entrySet()) {
            Optional<ModelPart> modelPart = model.getChild(animations.getKey());
            List<Transformation> transformations = animations.getValue();
            modelPart.ifPresent(part -> transformations.forEach(transformation -> {
                Keyframe[] keyframes = transformation.keyframes();
                int start = Math.max(0, Mth.binarySearch(0, keyframes.length, i -> runningSeconds <= keyframes[i].timestamp()) - 1);
                int end = Math.min(keyframes.length - 1, start + 1);
                Keyframe frameStart = keyframes[start];
                Keyframe frameEnd = keyframes[end];
                float current = runningSeconds - frameStart.timestamp();
                float delta = Mth.clamp(current / (frameEnd.timestamp() - frameStart.timestamp()), 0.0f, 1.0f);
                frameEnd.interpolation().apply(cache, delta, keyframes, start, end, speed);
                transformation.target().apply(part, cache);
            }));
        }
    }

    private static float getRunningSeconds(Animation animation, long runningTime) {
        float time = (float)runningTime / 1000.0f;
        return animation.looping() ? time % animation.lengthInSeconds() : time;
    }

    public static Vector3f translate(float x, float y, float z) {
        return new Vector3f(x, -y, z);
    }

    public static Vector3f rotation(float x, float y, float z) {
        return new Vector3f(x * ((float)Math.PI / 180), y * ((float)Math.PI / 180), z * ((float)Math.PI / 180));
    }

    public static Vector3f scale(double x, double y, double z) {
        return new Vector3f((float)(x - 1.0), (float)(y - 1.0), (float)(z - 1.0));
    }
}