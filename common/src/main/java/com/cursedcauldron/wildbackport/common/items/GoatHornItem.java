package com.cursedcauldron.wildbackport.common.items;

import com.cursedcauldron.wildbackport.common.registry.WBGameEvents;
import com.cursedcauldron.wildbackport.common.registry.WBItems;
import com.cursedcauldron.wildbackport.common.registry.WBRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GoatHornItem extends Item {
    private static final String INSTRUMENT_KEY = "instrument";
    private TagKey<Instrument> instruments;

    public GoatHornItem(Properties properties, TagKey<Instrument> instrumentTag) {
        super(properties);
        this.instruments = instrumentTag;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, components, tooltipFlag);
        Optional<ResourceKey<Instrument>> instrument = this.getInstrument(stack).flatMap(Holder::unwrapKey);
        if (instrument.isPresent()) {
            MutableComponent component = new TranslatableComponent(Util.makeDescriptionId(INSTRUMENT_KEY, instrument.get().location()));
            components.add(component.withStyle(ChatFormatting.GRAY));
        }
    }

    public static ItemStack getStackForInstrument(Item item, Holder<Instrument> instrument) {
        ItemStack stack = new ItemStack(item);
        setInstrument(stack, instrument);
        return stack;
    }

    public static void setRandomInstrumentFromTag(ItemStack stack, TagKey<Instrument> tag, Random random) {
        Optional<Holder<Instrument>> instrument = WBRegistries.INSTRUMENT.registry().getTag(tag).flatMap(holders -> holders.getRandomElement(random));
        instrument.ifPresent(holder -> setInstrument(stack, holder));
    }

    private static void setInstrument(ItemStack stack, Holder<Instrument> instrument) {
        stack.getOrCreateTag().putString(INSTRUMENT_KEY, instrument.unwrapKey().orElseThrow(() -> {
            return new IllegalStateException("Invalid instrument");
        }).location().toString());
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> stacks) {
        if (this.allowdedIn(tab)) {
            for (Holder<Instrument> holder : WBRegistries.INSTRUMENT.registry().getTagOrEmpty(this.instruments)) {
//                stacks.add(getStackForInstrument(WBItems.GOAT_HORN.get(), holder));
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Optional<Holder<Instrument>> holder = this.getInstrument(stack);
        if (holder.isPresent()) {
            Instrument instrument = holder.get().value();
            player.startUsingItem(hand);
            playSound(level, player, instrument);
            player.getCooldowns().addCooldown(this, instrument.useDuration());
            return InteractionResultHolder.consume(stack);
        } else {
            return InteractionResultHolder.fail(stack);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        Optional<Holder<Instrument>> instrument = this.getInstrument(stack);
        return instrument.map(holder -> holder.value().useDuration()).orElse(0);
    }

    private Optional<Holder<Instrument>> getInstrument(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            ResourceLocation location = ResourceLocation.tryParse(tag.getString("instrument"));
            if (location != null) {
                return WBRegistries.INSTRUMENT.registry().getHolder(ResourceKey.create(WBRegistries.INSTRUMENT.key(), location));
            }
        }

        Iterator<Holder<Instrument>> instruments = WBRegistries.INSTRUMENT.registry().getTagOrEmpty(this.instruments).iterator();
        return instruments.hasNext() ? Optional.of(instruments.next()) : Optional.empty();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPYGLASS;
    }

    private static void playSound(Level level, Player player, Instrument instrument) {
        level.playSound(player, player, instrument.soundEvent(), SoundSource.RECORDS, instrument.range() / 16.0F, 1.0F);
        level.gameEvent(WBGameEvents.INSTRUMENT_PLAY.get(), player);
    }
}