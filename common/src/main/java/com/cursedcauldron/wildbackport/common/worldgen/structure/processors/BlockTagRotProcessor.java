package com.cursedcauldron.wildbackport.common.worldgen.structure.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BlockTagRotProcessor extends StructureProcessor {
    public static final Codec<BlockTagRotProcessor> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(RegistryCodecs.homogeneousList(Registry.BLOCK_REGISTRY).optionalFieldOf("rottable_blocks").forGetter(processor -> {
            return processor.rottableBlocks;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("integrity").forGetter(processor -> {
            return processor.integrity;
        })).apply(instance, BlockTagRotProcessor::new);
    });
    private static final StructureProcessorType<BlockTagRotProcessor> BLOCK_TAG_ROT = Registry.register(Registry.STRUCTURE_PROCESSOR, new ResourceLocation("block_rot"), () -> CODEC);
    private final Optional<HolderSet<Block>> rottableBlocks;
    private final float integrity;

    public BlockTagRotProcessor(TagKey<Block> rottableBlocks, float integrity) {
        this(Optional.of(Registry.BLOCK.getOrCreateTag(rottableBlocks)), integrity);
    }

    public BlockTagRotProcessor(Optional<HolderSet<Block>> rottableBlocks, float integrity) {
        this.rottableBlocks = rottableBlocks;
        this.integrity = integrity;
    }

    @Override @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos from, BlockPos to, StructureTemplate.StructureBlockInfo pre, StructureTemplate.StructureBlockInfo post, StructurePlaceSettings settings) {
        return (this.rottableBlocks.isEmpty() || pre.state.is(this.rottableBlocks.get())) && !(settings.getRandom(post.pos).nextFloat() <= this.integrity) ? null : post;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return BLOCK_TAG_ROT;
    }
}