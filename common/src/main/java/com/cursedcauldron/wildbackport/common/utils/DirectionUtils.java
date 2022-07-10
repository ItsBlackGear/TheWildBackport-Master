package com.cursedcauldron.wildbackport.common.utils;

import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectionUtils {
    public static Collection<Direction> shuffle(Random random) {
        List<Direction> directions = Arrays.stream(Direction.values()).collect(Collectors.toList());
        Collections.shuffle(directions, random);
        return directions;
    }

    public static Stream<Direction> stream() {
        return Stream.of(Direction.values());
    }

    public static Collection<Direction> unpack(byte faces) {
        ArrayList<Direction> directions = new ArrayList<>(6);
        for (Direction direction : Direction.values()) {
            if ((faces & (byte)(2 << direction.ordinal() >> 1)) > 0) {
                directions.add(direction);
            }
        }

        return directions;
    }
}