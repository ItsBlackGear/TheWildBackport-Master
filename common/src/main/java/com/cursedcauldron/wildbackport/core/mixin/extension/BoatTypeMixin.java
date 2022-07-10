package com.cursedcauldron.wildbackport.core.mixin.extension;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.common.entities.access.api.BoatTypes;
import com.cursedcauldron.wildbackport.common.registry.WBBlocks;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//<>

@Mixin(Boat.Type.class)
public class BoatTypeMixin {
    @Shadow @Mutable @Final private static Boat.Type[] $VALUES;

    @Invoker("<init>")
    public static Boat.Type create(String internal, int id, Block planks, String name) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/vehicle/Boat$Type;$VALUES:[Lnet/minecraft/world/entity/vehicle/Boat$Type;", shift = At.Shift.AFTER))
    private static void wb$addBoat(CallbackInfo ci) {
        List<Boat.Type> types = new ArrayList<>(Arrays.asList($VALUES));
        Boat.Type last = types.get(types.size() - 1);
        int i = 1;

//        for (BoatTypes type : BoatTypes.values()) {
            types.add(create("mangrove", last.ordinal() + 1, Registry.BLOCK.get(new ResourceLocation(WildBackport.MOD_ID, "mangrove_planks")), "mangrove"));
//            i++;
//        }

        $VALUES = types.toArray(new Boat.Type[0]);
    }
}