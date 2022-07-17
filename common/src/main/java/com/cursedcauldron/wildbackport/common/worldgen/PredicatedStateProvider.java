package com.cursedcauldron.wildbackport.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;
import java.util.Random;

public record PredicatedStateProvider(BlockStateProvider fallback, List<Rule> rules) {
    public static final Codec<PredicatedStateProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockStateProvider.CODEC.fieldOf("fallback").forGetter(PredicatedStateProvider::fallback), Rule.CODEC.listOf().fieldOf("rules").forGetter(PredicatedStateProvider::rules)).apply(instance, PredicatedStateProvider::new));

    public static PredicatedStateProvider of(BlockStateProvider provider) {
        return new PredicatedStateProvider(provider, List.of());
    }

    public static PredicatedStateProvider of(Block block) {
        return of(BlockStateProvider.simple(block));
    }

    public BlockState getBlockState(WorldGenLevel level, Random random, BlockPos pos) {
        for (Rule rule : this.rules) if (rule.ifTrue.test(level, pos)) return rule.then.getState(random, pos);
        return this.fallback.getState(random, pos);
    }

    public record Rule(BlockPredicate ifTrue, BlockStateProvider then) {
        public static final Codec<Rule> CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockPredicate.CODEC.fieldOf("if_true").forGetter(Rule::ifTrue), BlockStateProvider.CODEC.fieldOf("then").forGetter(Rule::then)).apply(instance, Rule::new));
    }
}