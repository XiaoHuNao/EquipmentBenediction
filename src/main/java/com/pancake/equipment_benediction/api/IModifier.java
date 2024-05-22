package com.pancake.equipment_benediction.api;

import com.pancake.equipment_benediction.common.bonus.BonusHandler;
import com.pancake.equipment_benediction.common.equippable.EquippableGroup;
import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface IModifier extends ICodec<IModifier> {
    EquippableGroup getGroup();

    BonusHandler<ModifierInstance> getHandler();

    int getRarity();

    int getLevel();

    Predicate<ItemStack> isViable();

    Component getDisplayName();
    Component getDescription();

    TextColor getColor();

    String getTranslationKey();

    ResourceLocation getRegistryName();


    void apply(Player player);

    void clear(Player player);


    boolean checkEquippable(LivingEntity livingEntity);
}


