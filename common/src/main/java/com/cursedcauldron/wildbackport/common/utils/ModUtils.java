package com.cursedcauldron.wildbackport.common.utils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Random;

public class ModUtils {
    public static <T> void shuffle(List<T> entries, Random random) {
        int size = entries.size();
        for (int i = size; i > 1; --i) {
            entries.set(i - 1, entries.set(random.nextInt(i), entries.get(i - 1)));
        }
    }

    public static <T> List<T> copyShuffled(T[] entries, Random random) {
        ObjectArrayList<T> objects = new ObjectArrayList<>(entries);
        shuffle(objects, random);
        return objects;
    }

    public static <T> List<T> copyShuffled(ObjectArrayList<T> entries, Random random) {
        ObjectArrayList<T> objects = new ObjectArrayList<>(entries);
        shuffle(objects, random);
        return objects;
    }
}