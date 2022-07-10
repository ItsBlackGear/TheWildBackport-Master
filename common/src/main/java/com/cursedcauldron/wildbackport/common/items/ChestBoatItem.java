package com.cursedcauldron.wildbackport.common.items;

import com.cursedcauldron.wildbackport.common.entities.ChestBoat;
import com.cursedcauldron.wildbackport.common.entities.MangroveBoat;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

//<>

public class ChestBoatItem extends Item {
    private static final Predicate<Entity> RIDERS = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    private final Boat.Type type;
    private final boolean chested;

    public ChestBoatItem(boolean chested, Boat.Type type, Properties properties) {
        super(properties);
        this.chested = chested;
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(stack);
        } else {
            Vec3 viewVector = player.getViewVector(1.0F);
            List<Entity> entities = level.getEntities(player, player.getBoundingBox().expandTowards(viewVector.scale(5.0D)).inflate(1.0D), RIDERS);
            if (!entities.isEmpty()) {
                Vec3 eyePosition = player.getEyePosition();
                for (Entity entity : entities) {
                    AABB box = entity.getBoundingBox().inflate(entity.getPickRadius());
                    if (box.contains(eyePosition)) return InteractionResultHolder.pass(stack);
                }
            }

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                MangroveBoat boat = this.create(level, hitResult.getLocation());
                boat.setType(this.type);
                boat.setYRot(player.getYRot());
                if (!level.noCollision(boat, boat.getBoundingBox())) {
                    return InteractionResultHolder.fail(stack);
                } else {
                    if (!level.isClientSide) {
                        level.addFreshEntity(boat);
                        level.gameEvent(player, GameEvent.ENTITY_PLACE, new BlockPos(hitResult.getLocation()));
                        if (!player.getAbilities().instabuild) stack.shrink(1);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
                }
            } else {
                return InteractionResultHolder.pass(stack);
            }
        }
    }

    public MangroveBoat create(Level level, Vec3 pos) {
        if (this.chested) {
            return new ChestBoat(level, pos.x, pos.y, pos.z);
        } else {
            return new MangroveBoat(level, pos.x, pos.y, pos.z);
        }
    }
}