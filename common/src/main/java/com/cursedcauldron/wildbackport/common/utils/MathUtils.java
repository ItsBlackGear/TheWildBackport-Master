package com.cursedcauldron.wildbackport.common.utils;

public class MathUtils {
    public static float catmullrom(float delta, float startPoint, float start, float end, float endPoint) {
        return 0.5F * (2.0F * start + (end - startPoint) * delta + (2.0F * startPoint - 5.0F * start + 4.0F * end - endPoint) * delta * delta + (3.0F * start - startPoint - 3.0F * end + endPoint) * delta * delta * delta);
    }
}