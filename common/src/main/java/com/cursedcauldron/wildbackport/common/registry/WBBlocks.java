package com.cursedcauldron.wildbackport.common.registry;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.registry.WBSoundTypes;
import com.cursedcauldron.wildbackport.common.blocks.StateProperties;
import com.cursedcauldron.wildbackport.common.blocks.FrogspawnBlock;
import com.cursedcauldron.wildbackport.common.blocks.MangroveLeavesBlock;
import com.cursedcauldron.wildbackport.common.blocks.MangrovePropaguleBlock;
import com.cursedcauldron.wildbackport.common.blocks.MangroveRootsBlock;
import com.cursedcauldron.wildbackport.common.blocks.MudBlock;
import com.cursedcauldron.wildbackport.common.blocks.SculkBlock;
import com.cursedcauldron.wildbackport.common.blocks.SculkCatalystBlock;
import com.cursedcauldron.wildbackport.common.blocks.SculkShriekerBlock;
import com.cursedcauldron.wildbackport.common.blocks.SculkVeinBlock;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import com.cursedcauldron.wildbackport.core.api.WoodTypeRegistry;
import com.cursedcauldron.wildbackport.core.mixin.access.DoorBlockAccessor;
import com.cursedcauldron.wildbackport.core.mixin.access.PressurePlateBlockAccessor;
import com.cursedcauldron.wildbackport.core.mixin.access.StairBlockAccessor;
import com.cursedcauldron.wildbackport.core.mixin.access.TrapDoorBlockAccessor;
import com.cursedcauldron.wildbackport.core.mixin.access.WoodButtonBlockAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.WaterLilyBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.function.Function;
import java.util.function.Supplier;

//<>

public class WBBlocks {
    public static final CoreRegistry<Block> BLOCKS = CoreRegistry.create(Registry.BLOCK, WildBackport.MOD_ID);

    // Sculk
    public static final Supplier<Block> SCULK                       = create("sculk", () -> new SculkBlock(BlockBehaviour.Properties.of(Material.SCULK).strength(0.6F).sound(WBSoundTypes.SCULK)), CreativeModeTab.TAB_DECORATIONS);
    public static final Supplier<Block> SCULK_VEIN                  = create("sculk_vein", () -> new SculkVeinBlock(BlockBehaviour.Properties.of(Material.SCULK).noCollission().strength(0.2F).sound(WBSoundTypes.SCULK_VEIN)), CreativeModeTab.TAB_DECORATIONS);
    public static final Supplier<Block> SCULK_CATALYST              = create("sculk_catalyst", () -> new SculkCatalystBlock(BlockBehaviour.Properties.of(Material.SCULK).requiresCorrectToolForDrops().strength(3.0F).sound(WBSoundTypes.SCULK_CATALYST).lightLevel(value -> 6)), CreativeModeTab.TAB_REDSTONE);
    public static final Supplier<Block> SCULK_SHRIEKER              = create("sculk_shrieker", () -> new SculkShriekerBlock(BlockBehaviour.Properties.of(Material.SCULK, MaterialColor.COLOR_BLACK).strength(3.0F).sound(WBSoundTypes.SCULK_SHRIEKER)), CreativeModeTab.TAB_REDSTONE);

    // Frog
    public static final Supplier<Block> OCHRE_FROGLIGHT             = create("ochre_froglight", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.DECORATION).instabreak().lightLevel(value -> 15).sound(WBSoundTypes.FROGLIGHT)), CreativeModeTab.TAB_DECORATIONS);
    public static final Supplier<Block> VERDANT_FROGLIGHT           = create("verdant_froglight", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.DECORATION).instabreak().lightLevel(value -> 15).sound(WBSoundTypes.FROGLIGHT)), CreativeModeTab.TAB_DECORATIONS);
    public static final Supplier<Block> PEARLESCENT_FROGLIGHT       = create("pearlescent_froglight", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.DECORATION).instabreak().lightLevel(value -> 15).sound(WBSoundTypes.FROGLIGHT)), CreativeModeTab.TAB_DECORATIONS);
    public static final Supplier<Block> FROGSPAWN                   = create("frogspawn", () -> new FrogspawnBlock(BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS).instabreak().noDrops().noOcclusion().noCollission().sound(WBSoundTypes.FROGSPAWN)), entry -> new WaterLilyBlockItem(entry.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    // Mangrove
    public static final Supplier<Block> MANGROVE_LOG                = create("mangrove_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.COLOR_RED : MaterialColor.PODZOL).strength(2.0F).sound(SoundType.WOOD)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Supplier<Block> MANGROVE_WOOD               = create("mangrove_wood", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED).strength(2.0F).sound(SoundType.WOOD)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Supplier<Block> STRIPPED_MANGROVE_LOG       = create("stripped_mangrove_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED).strength(2.0F).sound(SoundType.WOOD)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Supplier<Block> STRIPPED_MANGROVE_WOOD      = create("stripped_mangrove_wood", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED).strength(2.0F).sound(SoundType.WOOD)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Supplier<Block> MANGROVE_PLANKS             = create("mangrove_planks", () -> new Block(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED).strength(2.0F, 3.0F).sound(SoundType.WOOD)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Supplier<Block> MANGROVE_STAIRS             = create("mangrove_stairs", () -> StairBlockAccessor.createStairBlock(MANGROVE_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(MANGROVE_PLANKS.get())), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Supplier<Block> MANGROVE_SLAB               = create("mangrove_slab", () -> new SlabBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED).strength(2.0F, 3.0F).sound(SoundType.WOOD)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Supplier<Block> MANGROVE_FENCE              = create("mangrove_fence", () -> new FenceBlock(BlockBehaviour.Properties.of(Material.WOOD, MANGROVE_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), CreativeModeTab.TAB_DECORATIONS);
    public static final Supplier<Block> MANGROVE_FENCE_GATE         = create("mangrove_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of(Material.WOOD, MANGROVE_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)), CreativeModeTab.TAB_REDSTONE);
    public static final Supplier<Block> MANGROVE_DOOR               = create("mangrove_door", () -> DoorBlockAccessor.createDoorBlock(BlockBehaviour.Properties.of(Material.WOOD, MANGROVE_PLANKS.get().defaultMaterialColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion()), CreativeModeTab.TAB_REDSTONE);
    public static final Supplier<Block> MANGROVE_TRAPDOOR           = create("mangrove_trapdoor", () -> TrapDoorBlockAccessor.createTrapDoorBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn(StateProperties::never)), CreativeModeTab.TAB_REDSTONE);
    public static final Supplier<Block> MANGROVE_PRESSURE_PLATE     = create("mangrove_pressure_plate", () -> PressurePlateBlockAccessor.createPressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of(Material.WOOD, MANGROVE_PLANKS.get().defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)), CreativeModeTab.TAB_REDSTONE);
    public static final Supplier<Block> MANGROVE_BUTTON             = create("mangrove_button", () -> WoodButtonBlockAccessor.createWoodButtonBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)), CreativeModeTab.TAB_REDSTONE);
    public static final Supplier<Block> MANGROVE_LEAVES             = create("mangrove_leaves", () -> new MangroveLeavesBlock(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(StateProperties::ocelotOrParrot).isSuffocating(StateProperties::never).isViewBlocking(StateProperties::never)), CreativeModeTab.TAB_DECORATIONS);
    public static final Supplier<Block> MANGROVE_PROPAGULE          = create("mangrove_propagule", () -> new MangrovePropaguleBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)), CreativeModeTab.TAB_DECORATIONS);
    public static final Supplier<Block> POTTED_MANGROVE_PROPAGULE   = create("potted_mangrove_propagule", () -> new FlowerPotBlock(MANGROVE_PROPAGULE.get(), BlockBehaviour.Properties.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final Supplier<Block> MANGROVE_ROOTS              = create("mangrove_roots", () -> new MangroveRootsBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(0.7F).randomTicks().sound(WBSoundTypes.MANGROVE_ROOTS).noOcclusion().isValidSpawn(StateProperties::ocelotOrParrot).isSuffocating(StateProperties::never).isViewBlocking(StateProperties::never)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Supplier<Block> MUDDY_MANGROVE_ROOTS        = create("muddy_mangrove_roots", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.DIRT).strength(0.7F).sound(WBSoundTypes.MUDDY_MANGROVE_ROOTS)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Pair<Supplier<StandingSignBlock>, Supplier<WallSignBlock>> MANGROVE_SIGN = create("mangrove", Material.WOOD, MaterialColor.COLOR_RED);

    // Mud
    public static final Supplier<Block> MUD                         = create("mud", () -> new MudBlock(BlockBehaviour.Properties.copy(Blocks.DIRT).color(MaterialColor.TERRACOTTA_CYAN).isValidSpawn(StateProperties::always).isRedstoneConductor(StateProperties::always).isViewBlocking(StateProperties::always).isSuffocating(StateProperties::always).sound(WBSoundTypes.MUD)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Supplier<Block> PACKED_MUD                  = create("packed_mud", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT).strength(1.0F, 3.0F).sound(WBSoundTypes.PACKED_MUD)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Supplier<Block> MUD_BRICKS                  = create("mud_bricks", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(1.5F, 3.0F).sound(WBSoundTypes.MUD_BRICKS)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Supplier<Block> MUD_BRICK_STAIRS            = create("mud_brick_stairs", () -> StairBlockAccessor.createStairBlock(MUD_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(MUD_BRICKS.get())), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Supplier<Block> MUD_BRICK_SLAB              = create("mud_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BROWN).requiresCorrectToolForDrops().strength(1.5F, 3.0F)), CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final Supplier<Block> MUD_BRICK_WALL              = create("mud_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(MUD_BRICKS.get())), CreativeModeTab.TAB_DECORATIONS);

    // Deepslate
    public static final Supplier<Block> REINFORCED_DEEPSLATE        = create("reinforced_deepslate", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DEEPSLATE).sound(SoundType.DEEPSLATE).strength(55.0F, 1200.0F).noDrops()), CreativeModeTab.TAB_DECORATIONS);

    private static <T extends Block> Supplier<T> create(String key, Supplier<T> block, CreativeModeTab tab) {
        return create(key, block, entry -> new BlockItem(entry.get(), new Item.Properties().tab(tab)));
    }

    private static <T extends Block> Supplier<T> create(String key, Supplier<T> block, Function<Supplier<T>, Item> item) {
        Supplier<T> entry = create(key, block);
        WBItems.ITEMS.register(key, () -> item.apply(entry));
        return entry;
    }

    private static <T extends Block> Supplier<T> create(String key, Supplier<T> block) {
        return BLOCKS.register(key, block);
    }

    // Signs
    public static Pair<Supplier<StandingSignBlock>, Supplier<WallSignBlock>> create(String key, Material material, MaterialColor color) {
        return create(key, BlockBehaviour.Properties.of(material, color).noCollission().strength(1.0F).sound(SoundType.WOOD), new Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS));
    }

    public static Pair<Supplier<StandingSignBlock>, Supplier<WallSignBlock>> create(String key, BlockBehaviour.Properties blocks, Item.Properties items) {
        WoodType woodType = WoodTypeRegistry.create(new ResourceLocation(WildBackport.MOD_ID, key));
        Supplier<StandingSignBlock> standing = create(key + "_sign", () -> new StandingSignBlock(blocks, woodType));
        Supplier<WallSignBlock> wall = create(key + "_wall_sign", () -> new WallSignBlock(blocks.dropsLike(standing.get()), woodType));
        WBItems.ITEMS.register(key + "_sign", () -> new SignItem(items, standing.get(), wall.get()));
        return Pair.of(standing, wall);
    }
}