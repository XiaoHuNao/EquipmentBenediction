package com.pancake.equipment_benediction.common.modifier;

import com.mojang.serialization.Codec;
import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.common.bonus.BonusHandler;
import com.pancake.equipment_benediction.common.equipment_set.equippable.EquippableGroup;
import com.pancake.equipment_benediction.common.init.ModModifier;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class Modifier implements IModifier {
    protected BonusHandler<ModifierInstance> handler = new BonusHandler<>();
    public EquippableGroup group = EquippableGroup.create();
    protected Predicate<ItemStack> isViable = stack -> true;

    private final int rarity;
    private final int level;


    protected int color = 0x7a7b78;
    private String translationKey;
    private MutableComponent description;
    private MutableComponent displayName;


    public Modifier(int rarity, int level) {
        this.rarity = rarity;
        this.level = level;
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
    public int getRarity() {
        return rarity;
    }

    @Override
    public int getLevel() {
        return level;
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
    public Component getDisplayName() {
        if (displayName == null) {
            displayName = Component.translatable(getTranslationKey()).withStyle(style -> style.withColor(getColor()));
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

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public final TextColor getColor() {
        return TextColor.fromRgb(color);
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
        return ModModifier.REGISTRY.get().getKey(this);
    }

    @Override
    public Codec<IModifier> codec() {
        return Codec.unit(() -> ModModifier.REGISTRY.get().getValue(getRegistryName()));
    }

    @Override
    public IModifier type() {
        return ModModifier.REGISTRY.get().getValue(getRegistryName());
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
