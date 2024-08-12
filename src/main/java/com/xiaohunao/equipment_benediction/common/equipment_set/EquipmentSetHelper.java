package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.google.common.collect.ImmutableList;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.init.ModEquipmentSet;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class EquipmentSetHelper {
    public static ListTag getListTag(CompoundTag tag) {
        return tag != null ? tag.getList("EquipmentSet", Tag.TAG_STRING) : new ListTag();
    }
    public static ListTag getPlayerListTag(Player player) {
        return getListTag(player.getPersistentData());
    }
    public static Optional<Tag> encodeStart(EquipmentSet set) {
        ResourceLocation location = ModEquipmentSet.SET_MAP.inverse().get(set);
        return location != null ? Optional.of(StringTag.valueOf(location.toString())) : Optional.empty();

    }
    public static Optional<EquipmentSet> parse(Tag tag) {
        StringTag stringTag = (StringTag) tag;
        EquipmentSet equipmentSet = ModEquipmentSet.SET_MAP.get(ResourceLocation.tryParse(stringTag.getAsString()));
        return equipmentSet != null ? Optional.of(equipmentSet) : Optional.empty();
    }
    public static boolean hasSet(Player player, EquipmentSet set) {
        return getPlayerListTag(player).stream().anyMatch((nbt) -> parse(nbt).filter((equipmentSet) -> equipmentSet.equals(set)).isPresent());
    }

    public static void updateSet(ItemStack from, ItemStack to, Player player) {
        ModEquipmentSet.INGREDIENT_MAP.entries().forEach((entry) -> {
            EquipmentSet set = entry.getValue();
            if (set.group.checkEquippable(player) && hasSet(player,set)) {
                removePlayerSet(set, player);
            }
        });

        if (hasSet(from)) {
            getSet(from).forEach((set) -> {
                if (!set.group.checkEquippable(player)) {
                    removePlayerSet(set, player);
                }
            });
        }
        if (hasSet(to)) {
            getSet(to).forEach((set) -> {
                if (set.group.checkEquippable(player) && !hasSet(player, set)) {
                    addPlayerSet(set, player);
                }
            });
        }
    }

    public static boolean hasSet(ItemStack stack) {
        return ModEquipmentSet.INGREDIENT_MAP.entries().stream().map(Map.Entry::getKey).anyMatch((ingredient) -> ingredient.test(stack));
    }

    public static Collection<EquipmentSet> getSet(ItemStack stack) {
        return ModEquipmentSet.INGREDIENT_MAP.entries().stream()
                .filter((entry) -> entry.getKey().test(stack))
                .map(Map.Entry::getValue)
                .collect(ImmutableList.toImmutableList());
    }

    public static void addPlayerSet(EquipmentSet set, Player player) {
        encodeStart(set).ifPresent((nbt) -> {
            ListTag listTag = getPlayerListTag(player);
            if (listTag.add(nbt)) {
                set.apply(player);
                player.getPersistentData().put("EquipmentSet", listTag);
            }
        });
    }

    public static void removePlayerSet(EquipmentSet set, Player player) {
        ListTag listTag = getPlayerListTag(player);
        listTag.removeIf((nbt) -> parse(nbt).filter((equipmentSet) -> equipmentSet.equals(set)).isPresent());
        player.getPersistentData().put("EquipmentSet", listTag);
        set.clear(player);
    }

    public static EquipmentSet getEquipmentSet(ResourceLocation registryName) {
        return ModEquipmentSet.SET_MAP.get(registryName);
    }
}
