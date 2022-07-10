package com.cursedcauldron.wildbackport.common.entities;

import com.cursedcauldron.wildbackport.common.entities.access.api.BoatTypes;
import com.cursedcauldron.wildbackport.common.registry.WBItems;
import com.cursedcauldron.wildbackport.common.registry.entity.WBEntities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

//<>

public class MangroveBoat extends Boat {
    public MangroveBoat(EntityType<? extends Boat> type, Level level) {
        super(type, level);
    }

    public MangroveBoat(Level level, double x, double y, double z) {
        super(WBEntities.MANGROVE_BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.level.isClientSide() && !this.isRemoved()) {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.setDamage(this.getDamage() + amount * 10.0F);
            this.markHurt();
            this.gameEvent(GameEvent.ENTITY_DAMAGED, source.getEntity());
            boolean isCreativePlayer = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
            if (isCreativePlayer || this.getDamage() > 40.0F) {
                if (!isCreativePlayer && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) this.dropItems(source);

                this.discard();
            }

            return true;
        } else {
            return true;
        }
    }

    protected void dropItems(DamageSource source) {
        this.spawnAtLocation(this.getDropItem());
    }

    @Override
    public Item getDropItem() {
        return this.getBoatType() != BoatTypes.MANGROVE.get() ? super.getDropItem() : WBItems.MANGROVE_BOAT.get();
    }
}