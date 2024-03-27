package com.pancake.equipment_benediction.common.equipment_set;

import com.mojang.serialization.Codec;
import com.pancake.equipment_benediction.api.IEquipmentSet;
import com.pancake.equipment_benediction.common.bonus.BonusHandler;
import com.pancake.equipment_benediction.common.equippable.EquippableGroup;
import com.pancake.equipment_benediction.common.init.ModEquipmentSet;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public abstract class EquipmentSet implements IEquipmentSet {
    public static final String titleKey = "equipment_set.equipment_benediction.title";
    public EquippableGroup group = EquippableGroup.create();
    public BonusHandler<IEquipmentSet> handler = new BonusHandler<>();

    protected int textColor = 0xe74c3c;
    private String translationKey;
    private Component description;
    private Component displayName;


    public EquipmentSet() {
        init(handler, group);
    }

    public abstract void init(BonusHandler<IEquipmentSet> handler, EquippableGroup group);

    @Override
    public EquippableGroup getGroup() {
        return group;
    }

    @Override
    public BonusHandler<IEquipmentSet> getHandler() {
        return handler;
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
    public ResourceLocation getRegistryName() {
        return ModEquipmentSet.EQUIPMENT_SET_REGISTRY.get().getKey(this);
    }
    @Override
    public Codec<IEquipmentSet> codec() {
        return ResourceLocation.CODEC.xmap(ModEquipmentSet.EQUIPMENT_SET_REGISTRY.get()::getValue, IEquipmentSet::getRegistryName);
    }

    @Override
    public IEquipmentSet type() {
        return ModEquipmentSet.EQUIPMENT_SET_REGISTRY.get().getValue(getRegistryName());
    }

    @Override
    public final String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.makeDescriptionId("equipment_set", getRegistryName());
        }

        return this.translationKey;
    }

    @Override
    public Component getDisplayName() {
        if (displayName == null) {
            displayName = Component.translatable(titleKey).withStyle(style -> style.withColor(0xb1b1b1)).append(" ").append(Component.translatable(getTranslationKey()).withStyle(style -> style.withColor(getTextColor())));
        }
        return displayName;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public final TextColor getTextColor() {
        return TextColor.fromRgb(textColor);
    }

    @Override
    public Component getDescription() {
        if (description == null) {
            description = Component.translatable(getTranslationKey() + ".description").withStyle(style -> style.withColor(0x7a7b78));
        }
        return description;
    }

    @Override
    public boolean checkEquippable(LivingEntity livingEntity) {
        return getGroup().checkEquippable(livingEntity);
    }

}
