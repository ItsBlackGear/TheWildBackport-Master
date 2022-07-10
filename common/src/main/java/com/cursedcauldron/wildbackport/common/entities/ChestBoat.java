package com.cursedcauldron.wildbackport.common.entities;

import com.cursedcauldron.wildbackport.common.registry.WBItems;
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntities;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

//<>

public class ChestBoat extends MangroveBoat implements Container, MenuProvider {
    private NonNullList<ItemStack> stacks = NonNullList.withSize(27, ItemStack.EMPTY);
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;

    public ChestBoat(EntityType<? extends Boat> type, Level level) {
        super(type, level);
    }

    public ChestBoat(Level level, double x, double y, double z) {
        super(WBEntities.CHEST_BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.15F;
    }

    @Override
    protected boolean canAddPassenger(Entity entity) {
        return this.getPassengers().size() < 1;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.lootTable != null) {
            tag.putString("LootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) tag.putLong("LootTableSeed", this.lootTableSeed);
        } else {
            ContainerHelper.saveAllItems(tag, this.stacks);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (tag.contains("LootTable", 8)) {
            this.lootTable = new ResourceLocation(tag.getString("LootTable"));
            this.lootTableSeed = tag.getLong("LootTableSeed");
        } else {
            ContainerHelper.loadAllItems(tag, this.stacks);
        }
    }

    @Override
    protected void dropItems(DamageSource source) {
        super.dropItems(source);
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            Containers.dropContents(this.level, this, this);
            if (!this.level.isClientSide) {
                Entity entity = source.getDirectEntity();
                if (entity != null && entity.getType() == EntityType.PLAYER) PiglinAi.angerNearbyPiglins((Player)entity, true);
            }
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level.isClientSide && reason.shouldDestroy()) Containers.dropContents(this.level, this, this);
        super.remove(reason);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isCrouching()) {
            player.openMenu(this);
            if (!player.level.isClientSide) {
                this.gameEvent(GameEvent.CONTAINER_OPEN, player);
                PiglinAi.angerNearbyPiglins(player, true);
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.SUCCESS;
            }
        }

        return super.interact(player, hand);
    }

    public void openInventory(Player player) {
        player.openMenu(this);
        if (!player.level.isClientSide()) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, player);
            PiglinAi.angerNearbyPiglins(player, true);
        }
    }

    @Override @SuppressWarnings("UnnecessaryDefault")
    public Item getDropItem() {
        return switch (this.getBoatType()) {
            case OAK -> WBItems.OAK_CHEST_BOAT.get();
            case SPRUCE -> WBItems.SPRUCE_CHEST_BOAT.get();
            case BIRCH -> WBItems.BIRCH_CHEST_BOAT.get();
            case JUNGLE -> WBItems.JUNGLE_CHEST_BOAT.get();
            case ACACIA -> WBItems.ACACIA_CHEST_BOAT.get();
            case DARK_OAK -> WBItems.DARK_OAK_CHEST_BOAT.get();
            default -> WBItems.MANGROVE_CHEST_BOAT.get();
        };
    }

    public void unpackLootTable(@Nullable Player player) {
        MinecraftServer server = this.level.getServer();
        if (this.lootTable != null && server != null) {
            LootTable lootTable = server.getLootTables().get(this.lootTable);
            if (player != null) CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)player, this.lootTable);

            this.lootTable = null;
            LootContext.Builder builder = new LootContext.Builder((ServerLevel)this.level).withParameter(LootContextParams.ORIGIN, this.position()).withOptionalRandomSeed(this.lootTableSeed);
            if (player != null) builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);

            lootTable.fill(this, builder.create(LootContextParamSets.CHEST));
        }
    }

    @Override
    public void clearContent() {
        this.unpackLootTable(null);
        this.stacks.clear();
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public ItemStack getItem(int slot) {
        this.unpackLootTable(null);
        return this.stacks.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        this.unpackLootTable(null);
        return ContainerHelper.removeItem(this.stacks, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        this.unpackLootTable(null);
        ItemStack stack = this.stacks.get(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.stacks.set(slot, ItemStack.EMPTY);
            return stack;
        }
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.unpackLootTable(null);
        this.stacks.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) stack.setCount(this.getMaxStackSize());
    }

    @Override
    public SlotAccess getSlot(int slot) {
        return slot >= 0 && slot < this.getContainerSize() ? new SlotAccess() {
            @Override public ItemStack get() {
                return ChestBoat.this.getItem(slot);
            }

            @Override public boolean set(ItemStack stack) {
                ChestBoat.this.setItem(slot, stack);
                return true;
            }
        } : super.getSlot(slot);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return !this.isRemoved() && this.position().closerThan(player.position(), 8.0D);
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.stacks) if (!stack.isEmpty()) return false;
        return true;
    }

    @Override @Nullable
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        if (this.lootTable == null || !player.isSpectator()) {
            this.unpackLootTable(inventory.player);
            return ChestMenu.threeRows(i, inventory, this);
        }

        return null;
    }
}