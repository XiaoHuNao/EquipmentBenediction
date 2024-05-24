package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.mojang.serialization.Codec;
import com.xiaohunao.equipment_benediction.api.IEquipmentSet;
import com.xiaohunao.equipment_benediction.common.bonus.BonusHandler;
import com.xiaohunao.equipment_benediction.common.equipment_set.equippable.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.init.ModEquipmentSet;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public abstract class EquipmentSet implements IEquipmentSet {
    public static final String titleKey = "equipment_set.equipment_benediction.title";
    public EquippableGroup group = EquippableGroup.create();
    public BonusHandler<IEquipmentSet> handler = new BonusHandler<>();
    protected Predicate<ItemStack> isViable = stack -> true;

    protected int color = 0x7a7b78;
    private String translationKey;
    private MutableComponent description;
    private MutableComponent displayName;


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
        return ModEquipmentSet.REGISTRY.get().getKey(this);
    }
    @Override
    public Codec<IEquipmentSet> codec() {
        return ResourceLocation.CODEC.xmap(ModEquipmentSet.REGISTRY.get()::getValue, IEquipmentSet::getRegistryName);
    }

    @Override
    public IEquipmentSet type() {
        return ModEquipmentSet.REGISTRY.get().getValue(getRegistryName());
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
            displayName = Component.translatable(titleKey).withStyle(style -> style.withColor(0xb1b1b1)).append(" ").append(Component.translatable(getTranslationKey()).withStyle(style -> style.withColor(getColor())));
        }
        return displayName;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public final TextColor getColor() {
        return TextColor.fromRgb(color);
    }

    @Override
    public Predicate<ItemStack> isViable() {
        return isViable;
    }

    @Override
    public void setViable(Predicate<ItemStack> isViable) {
        this.isViable = isViable;
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
        return group.checkEquippable(livingEntity);
    }

}
