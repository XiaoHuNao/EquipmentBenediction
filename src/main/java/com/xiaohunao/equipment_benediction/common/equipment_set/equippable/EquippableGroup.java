package com.xiaohunao.equipment_benediction.common.equipment_set.equippable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaohunao.equipment_benediction.api.IEquippable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class EquippableGroup {
    private EquippableGroup() {}

    private final LinkedHashMap<IEquippable<?>, Ingredient> EQUIPAGES = Maps.newLinkedHashMap();
    //黑名单
    private final LinkedList<IEquippable<?>> BLACKLIST = Lists.newLinkedList();

    public EquippableGroup addGroup(IEquippable<?> equippable, Ingredient ingredient) {
        this.EQUIPAGES.put(equippable, ingredient);
        return this;
    }
    public EquippableGroup addGroup(IEquippable<?> equippable, Item item) {
        return addGroup(equippable, Ingredient.of(item));
    }
    public EquippableGroup addGroup(IEquippable<?> equippable, Item... items) {
        return addGroup(equippable, Ingredient.of(items));
    }
    public EquippableGroup addGroup(IEquippable<?> equippable, TagKey<Item> tagKey) {
        return addGroup(equippable, Ingredient.of(tagKey));
    }
    public EquippableGroup addBlacklist(IEquippable<?> equippable) {
        this.BLACKLIST.add(equippable);
        return this;
    }
    public boolean checkEquippable(LivingEntity livingEntity) {
        boolean match = EQUIPAGES
                .entrySet()
                .stream()
                .allMatch(entry -> entry.getKey().checkEquippable(livingEntity, entry.getValue()));

        boolean black = BLACKLIST
                .stream()
                .allMatch(equippable -> equippable.checkEquippable(livingEntity, Ingredient.EMPTY));

        return match && black;
    }

    public static EquippableGroup create() {
        return new EquippableGroup();
    }

    public Map<IEquippable<?>, Ingredient> getEquipages() {
        return EQUIPAGES;
    }

    public boolean checkBlacklist(IEquippable<?> equippable) {
        return BLACKLIST.stream().anyMatch(equippable1 -> equippable1.equals(equippable));
    }


}
