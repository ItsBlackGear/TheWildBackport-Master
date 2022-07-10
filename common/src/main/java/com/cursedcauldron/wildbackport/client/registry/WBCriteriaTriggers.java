package com.cursedcauldron.wildbackport.client.registry;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.mixin.access.CriteriaTriggersAccessor;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.resources.ResourceLocation;

//<>

public class WBCriteriaTriggers {
    public static final KilledTrigger KILL_MOB_NEAR_SCULK_CATALYST = create(new KilledTrigger(new ResourceLocation(WildBackport.MOD_ID, "kill_mob_near_sculk_catalyst")));
//    public static final ItemUsedOnBlockTrigger ALLAY_DROP_ITEM_ON_BLOCK = create(new ItemUsedOnBlockTrigger(new ResourceLocation(WildBackport.MOD_ID, "kill_mob_near_sculk_catalyst")));
    public static final LocationTrigger AVOID_VIBRATION = create(new LocationTrigger(new ResourceLocation(WildBackport.MOD_ID, "avoid_vibration")));

    public static <T extends CriterionTrigger<?>> T create(T type) {
        return CriteriaTriggersAccessor.callRegister(type);
    }
}