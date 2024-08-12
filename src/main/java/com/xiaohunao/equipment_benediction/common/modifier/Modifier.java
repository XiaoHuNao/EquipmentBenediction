package com.xiaohunao.equipment_benediction.common.modifier;

import com.xiaohunao.equipment_benediction.common.bonus.BonusHandler;
import com.xiaohunao.equipment_benediction.common.equippable.EquippableGroup;
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

public class Modifier{
    private final ResourceLocation registryName;


    public BonusHandler<ModifierInstance> bonus = new BonusHandler<>();
    public EquippableGroup group = EquippableGroup.create();
    protected Predicate<ItemStack> isViable = stack -> true;

    private int rarity;
    private int level;


    protected int color = 0x7a7b78;
    private String translationKey;
    private MutableComponent description;
    private MutableComponent displayName;


    private Modifier(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public static Modifier create(ResourceLocation registryName){
        return new Modifier(registryName);
    }


    public ResourceLocation getRegistryName() {
        return registryName;
    }
    public int getRarity() {
        return rarity;
    }
    public int getLevel() {
        return level;
    }
    public Predicate<ItemStack> isViable() {
        return isViable;
    }
    public void setViable(Predicate<ItemStack> isViable) {
        this.isViable = isViable;
    }
    public void setColor(int color) {
        this.color = color;
    }
    public final TextColor getColor() {
        return TextColor.fromRgb(color);
    }
    public final String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.makeDescriptionId("modifier", getRegistryName());
        }

        return this.translationKey;
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
    public boolean checkEquippable(LivingEntity livingEntity) {
        return group.checkEquippable(livingEntity);
    }

    public Component getDisplayName() {
        if (displayName == null) {
            displayName = Component.translatable(getTranslationKey()).withStyle(style -> style.withColor(getColor()));
        }
        return displayName;
    }
    public Component getDescription() {
        if (description == null) {
            description = Component.translatable(getTranslationKey() + ".description").withStyle(style -> style.withColor(0x7a7b78));
        }
        return description;
    }

    public static Modifier wrap(Context context, Object object) {
        if (object == null ) {
            return null;
        } else if (object instanceof Modifier) {
            return (Modifier) object;
        } else if (object instanceof ResourceLocation) {
            return ModifierHelper.getModifier((ResourceLocation) object);
        }else if (object instanceof String) {
            return ModifierHelper.getModifier(ResourceLocation.tryParse((String) object));
        }else {
            throw new IllegalArgumentException("Cannot convert object to Modifier: " + object);
        }
    }
}
