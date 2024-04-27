package com.pancake.equipment_benediction.common.modifier;

import com.mojang.serialization.Codec;
import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.common.bonus.BonusHandler;
import com.pancake.equipment_benediction.common.equippable.EquippableGroup;
import com.pancake.equipment_benediction.common.init.ModModifiers;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public abstract class Modifier implements IModifier {
    protected BonusHandler<ModifierInstance> handler = new BonusHandler<>();
    public EquippableGroup group = EquippableGroup.create();
    protected int textColor = 0xe74c3c;
    private String translationKey;
    private Component description;
    private Component displayName;


    public Modifier() {
        init(handler,group);
    }

    public abstract void init(BonusHandler<ModifierInstance> handle, EquippableGroup group);
    @Override
    public EquippableGroup getGroup() {
        return group;
    }
    @Override
    public BonusHandler<ModifierInstance> getHandler() {
        return handler;
    }

    @Override
    public Component getDisplayName() {
        if (displayName == null) {
            displayName = Component.translatable(getTranslationKey()).withStyle(style -> style.withColor(getTextColor()));
        }
        return displayName;
    }
    @Override
    public Component getDescription() {
        if (description == null) {
            description = Component.translatable(getTranslationKey() + ".description").withStyle(style -> style.withColor(0x7a7b78));
        }
        return description;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public final TextColor getTextColor() {
        return TextColor.fromRgb(textColor);
    }
    @Override
    public final String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.makeDescriptionId("modifier", getRegistryName());
        }

        return this.translationKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            Modifier modifier = (Modifier)obj;
            return Objects.equals(this.getRegistryName(), modifier.getRegistryName());
        } else {
            return false;
        }
    }
    @Override
    public ResourceLocation getRegistryName() {
        return ModModifiers.MODIFIER_REGISTRY.get().getKey(this);
    }

    @Override
    public Codec<IModifier> codec() {
        return Codec.unit(() ->ModModifiers.MODIFIER_REGISTRY.get().getValue(getRegistryName()));
    }

    @Override
    public IModifier type() {
        return ModModifiers.MODIFIER_REGISTRY.get().getValue(getRegistryName());
    }

    @Override
    public void apply(Player player){
        getHandler().getBonuses().forEach((type, bonus) -> {
            bonus.apply(player);
        });
    }

    @Override
    public void clear(Player player){
        getHandler().getBonuses().forEach((type, bonus) -> {
            bonus.clear(player);
        });
    }
    @Override
    public boolean checkEquippable(LivingEntity livingEntity) {
        return group.checkEquippable(livingEntity);
    }
}
