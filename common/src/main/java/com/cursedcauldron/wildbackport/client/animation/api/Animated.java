package com.cursedcauldron.wildbackport.client.animation.api;

import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

//<>

public interface Animated {
    static Animated of(ModelPart part) {
        return Animated.class.cast(part);
    }

    static void translate(ModelPart part, Vector3f vec) {
        part.x += vec.x();
        part.y += vec.y();
        part.z += vec.z();
    }

    static void rotate(ModelPart part, Vector3f vec) {
        part.xRot += vec.x();
        part.yRot += vec.y();
        part.zRot += vec.z();
    }

    static void scaleY(ModelPart part, Vector3f vec) {
        Animated.of(part).scaleXTo(vec.x());
        Animated.of(part).scaleYTo(vec.y());
        Animated.of(part).scaleZTo(vec.z());
    }

    PartPose resetToDefault();

    void setDefault(PartPose pose);

    static void resetToDefault(ModelPart part) {
        part.loadPose(Animated.of(part).resetToDefault());
    }

    float scaleX();

    void scaleX(float x);

    void scaleXTo(float x);

    float scaleY();

    void scaleY(float y);

    void scaleYTo(float y);

    float scaleZ();

    void scaleZ(float z);

    void scaleZTo(float z);
}
