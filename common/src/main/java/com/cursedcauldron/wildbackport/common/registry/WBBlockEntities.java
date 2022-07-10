package com.cursedcauldron.wildbackport.common.registry;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.blocks.entity.MangroveSignBlockEntity;
import com.cursedcauldron.wildbackport.common.blocks.entity.SculkCatalystBlockEntity;
import com.cursedcauldron.wildbackport.common.blocks.entity.SculkShriekerBlockEntity;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

//<>

public class WBBlockEntities {
    public static final CoreRegistry<BlockEntityType<?>> BLOCKS = CoreRegistry.create(Registry.BLOCK_ENTITY_TYPE, WildBackport.MOD_ID);

    public static final Supplier<BlockEntityType<SculkCatalystBlockEntity>> SCULK_CATALYST  = BLOCKS.register("sculk_catalyst", () -> BlockEntityType.Builder.of(SculkCatalystBlockEntity::new, WBBlocks.SCULK_CATALYST.get()).build(null));
    public static final Supplier<BlockEntityType<SculkShriekerBlockEntity>> SCULK_SHRIEKER  = BLOCKS.register("sculk_shrieker", () -> BlockEntityType.Builder.of(SculkShriekerBlockEntity::new, WBBlocks.SCULK_SHRIEKER.get()).build(null));
//    public static final Supplier<BlockEntityType<MangroveSignBlockEntity>> MANGROVE_SIGN    = BLOCKS.register("mangrove_sign", () -> BlockEntityType.Builder.of(MangroveSignBlockEntity::new, WBBlocks.MANGROVE_SIGN.get(), WBBlocks.MANGROVE_WALL_SIGN.get()).build(null));
//    public static final Supplier<BlockEntityType<MangroveSignBlockEntity>> MANGROVE_SIGN    = BLOCKS.register("mangrove_sign", () -> BlockEntityType.Builder.of(MangroveSignBlockEntity::new, WBBlocks.MANGROVE_SIGN.getFirst().get(), WBBlocks.MANGROVE_SIGN.getSecond().get()).build(null));
}