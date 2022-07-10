package com.cursedcauldron.wildbackport.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SwiftSneakEnchantment extends Enchantment {
    public SwiftSneakEnchantment(Rarity rarity, EquipmentSlot[] equipmentSlots) {
        super(rarity, EnchantmentCategory.ARMOR_LEGS, equipmentSlots);
    }

    @Override
    public int getMinCost(int level) {
        return level * 25;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 50;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}