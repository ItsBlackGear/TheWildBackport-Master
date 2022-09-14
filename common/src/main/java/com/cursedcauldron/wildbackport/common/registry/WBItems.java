package com.cursedcauldron.wildbackport.common.registry;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.client.registry.WBSoundEvents;
import com.cursedcauldron.wildbackport.common.entities.access.api.BoatTypes;
import com.cursedcauldron.wildbackport.common.items.ChestBoatItem;
import com.cursedcauldron.wildbackport.common.items.DiscFragmentItem;
import com.cursedcauldron.wildbackport.common.items.TadpoleBucketItem;
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntityTypes;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import com.cursedcauldron.wildbackport.core.mixin.access.RecordItemAccessor;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Supplier;

//<>

public class WBItems {
    public static final CoreRegistry<Item> ITEMS = CoreRegistry.create(Registry.ITEM, WildBackport.MOD_ID);

    // Spawns
    public static final Supplier<Item> ALLAY_SPAWN_EGG      = create("allay_spawn_egg", spawnEgg(WBEntityTypes.ALLAY, 56063, 44543));
    public static final Supplier<Item> FROG_SPAWN_EGG       = create("frog_spawn_egg", spawnEgg(WBEntityTypes.FROG, 13661252, 16762748));
    public static final Supplier<Item> TADPOLE_SPAWN_EGG    = create("tadpole_spawn_egg", spawnEgg(WBEntityTypes.TADPOLE, 7164733, 1444352));
    public static final Supplier<Item> WARDEN_SPAWN_EGG     = create("warden_spawn_egg", spawnEgg(WBEntityTypes.WARDEN, 1001033, 3790560));
    public static final Supplier<Item> TADPOLE_BUCKET       = create("tadpole_bucket", () -> new TadpoleBucketItem(WBEntityTypes.TADPOLE, Fluids.WATER, () -> WBSoundEvents.BUCKED_EMPTY_TADPOLE, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));

    // Boats
    public static final Supplier<Item> MANGROVE_BOAT        = create("mangrove_boat", boat(false, BoatTypes.MANGROVE.get()));
    public static final Supplier<Item> OAK_CHEST_BOAT       = create("oak_chest_boat", boat(true, Boat.Type.OAK));
    public static final Supplier<Item> SPRUCE_CHEST_BOAT    = create("spruce_chest_boat", boat(true, Boat.Type.SPRUCE));
    public static final Supplier<Item> ACACIA_CHEST_BOAT    = create("acacia_chest_boat", boat(true, Boat.Type.ACACIA));
    public static final Supplier<Item> BIRCH_CHEST_BOAT     = create("birch_chest_boat", boat(true, Boat.Type.BIRCH));
    public static final Supplier<Item> JUNGLE_CHEST_BOAT    = create("jungle_chest_boat", boat(true, Boat.Type.JUNGLE));
    public static final Supplier<Item> DARK_OAK_CHEST_BOAT  = create("dark_oak_chest_boat", boat(true, Boat.Type.DARK_OAK));
    public static final Supplier<Item> MANGROVE_CHEST_BOAT  = create("mangrove_chest_boat", boat(true, BoatTypes.MANGROVE.get()));

    // Deep Dark
    public static final Supplier<Item> ECHO_SHARD           = create("echo_shard", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final Supplier<Item> RECOVERY_COMPASS     = create("recovery_compass", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS)));

    // Music
    public static final Supplier<Item> MUSIC_DISC_5         = create("music_disc_5", () -> RecordItemAccessor.createRecordItem(15, WBSoundEvents.MUSIC_DISC_5, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE)));
    public static final Supplier<Item> DISC_FRAGMENT_5      = create("disc_fragment_5", () -> new DiscFragmentItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
//    public static final Supplier<Item> GOAT_HORN            = create("goat_horn", () -> new GoatHornItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC), InstrumentTags.GOAT_HORNS));

    private static <T extends Item> Supplier<T> create(String key, Supplier<T> item) {
        return ITEMS.register(key, item);
    }

    @ExpectPlatform
    private static Supplier<Item> spawnEgg(Supplier<? extends EntityType<? extends Mob>> mob, int background, int highlight) {
        throw new AssertionError();
    }

    private static Supplier<Item> boat(boolean chested, Boat.Type type) {
        return () -> new ChestBoatItem(chested, type, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TRANSPORTATION));
    }
}