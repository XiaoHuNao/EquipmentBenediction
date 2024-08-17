package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.mojang.serialization.Codec;
import com.xiaohunao.equipment_benediction.common.bonus.BonusHandler;
import com.xiaohunao.equipment_benediction.common.equippable.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.init.ModEquipmentSet;
import com.xiaohunao.equipment_benediction.common.quality.Quality;
import com.xiaohunao.equipment_benediction.common.quality.QualityHelper;
import dev.latvian.mods.rhino.Context;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class EquipmentSet{
    public static final Codec<EquipmentSet> CODEC = Codec.STRING.xmap(ResourceLocation::tryParse, ResourceLocation::toString)
            .xmap(EquipmentSetHelper::getEquipmentSet, EquipmentSet::getRegistryName);

    public static final String titleKey = "equipment_set.equipment_benediction.title";
    private final ResourceLocation registryName;
    public EquippableGroup group = EquippableGroup.create();
    public BonusHandler<EquipmentSet> bonus = new BonusHandler<>();
    protected Predicate<ItemStack> isViable = stack -> true;

    protected int color = 0x7a7b78;
    private String translationKey;
    private MutableComponent description;
    private MutableComponent displayName;


    private EquipmentSet(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public static EquipmentSet create(ResourceLocation registryName) {
        return new EquipmentSet(registryName);
    }

    public EquipmentSet setColor(int color) {
        this.color = color;
        return this;
    }
    public EquipmentSet setViable(Predicate<ItemStack> isViable) {
        this.isViable = isViable;
        return this;
    }


    public void apply(Player player){
        bonus.getBonuses().forEach((type, bonus) -> {
            bonus.apply(player);
        });
    }

    public void clear(Player player){
        bonus.getBonuses().forEach((type, bonus) -> {
            bonus.clear(player);
        });
    }

    public final String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.makeDescriptionId("equipment_set", getRegistryName());
        }
        return this.translationKey;
    }

    public ResourceLocation getRegistryName() {
        return ModEquipmentSet.SET_MAP.inverse().get(this);
    }

    public final TextColor getColor() {
        return TextColor.fromRgb(color);
    }

    public Predicate<ItemStack> isViable() {
        return isViable;
    }



    public boolean checkEquippable(LivingEntity livingEntity) {
        return group.checkEquippable(livingEntity);
    }
    public Component getDisplayName() {
        if (displayName == null) {
            displayName = Component.translatable(titleKey).withStyle(style -> style.withColor(0xb1b1b1)).append(" ").append(Component.translatable(getTranslationKey()).withStyle(style -> style.withColor(getColor())));
        }
        return displayName;
    }
    public Component getDescription() {
        if (description == null) {
            description = Component.translatable(getTranslationKey() + ".description").withStyle(style -> style.withColor(0x7a7b78));
        }
        return description;
    }

    public static EquipmentSet wrap(Context context, Object object) {
        if (object == null) {
            return null;
        }else if (object instanceof EquipmentSet) {
            return (EquipmentSet) object;
        }else if (object instanceof ResourceLocation) {
            return EquipmentSetHelper.getEquipmentSet((ResourceLocation) object);
        }else if (object instanceof String) {
            return EquipmentSetHelper.getEquipmentSet(ResourceLocation.tryParse((String) object));
        }else {
            throw new IllegalArgumentException("Cannot convert object to EquipmentSet: " + object);
        }
    }

}
