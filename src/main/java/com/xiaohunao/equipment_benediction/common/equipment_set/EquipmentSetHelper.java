package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.google.common.collect.ImmutableList;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.IEquipmentSet;
import com.xiaohunao.equipment_benediction.common.init.ModEquipmentSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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
    public static Optional<Tag> encodeStart(IEquipmentSet set) {
        return ModEquipmentSet.REGISTRY
                .get()
                .getCodec()
                .encodeStart(NbtOps.INSTANCE, set)
                .resultOrPartial(EquipmentBenediction.LOGGER::error);
    }
    public static Optional<IEquipmentSet> parse(Tag tag) {
        return ModEquipmentSet.REGISTRY
                .get()
                .getCodec()
                .parse(NbtOps.INSTANCE, tag)
                .resultOrPartial(EquipmentBenediction.LOGGER::error);
    }
    public static boolean hasSet(Player player, IEquipmentSet set) {
        return getPlayerListTag(player).stream().anyMatch((nbt) -> parse(nbt).filter((equipmentSet) -> equipmentSet.equals(set)).isPresent());
    }

    public static void updateSet(ItemStack from, ItemStack to, Player player) {
        ModEquipmentSet.SET_MAP.entries().forEach((entry) -> {
            IEquipmentSet set = entry.getValue();
            if (set.getGroup().checkEquippable(player) && hasSet(player,set)) {
                removePlayerSet(set, player);
            }
        });

        if (hasSet(from)) {
            getSet(from).forEach((set) -> {
                if (!set.getGroup().checkEquippable(player)) {
                    removePlayerSet(set, player);
                }
            });
        }
        if (hasSet(to)) {
            getSet(to).forEach((set) -> {
                if (set.getGroup().checkEquippable(player) && !hasSet(player, set)) {
                    addPlayerSet(set, player);
                }
            });
        }
    }

    public static boolean hasSet(ItemStack stack) {
        return ModEquipmentSet.SET_MAP.entries().stream().map(Map.Entry::getKey).anyMatch((ingredient) -> ingredient.test(stack));
    }

    public static Collection<IEquipmentSet> getSet(ItemStack stack) {
        return ModEquipmentSet.SET_MAP.entries().stream()
                .filter((entry) -> entry.getKey().test(stack))
                .map(Map.Entry::getValue)
                .collect(ImmutableList.toImmutableList());
    }

    public static void addPlayerSet(IEquipmentSet set, Player player) {
        encodeStart(set).ifPresent((nbt) -> {
            ListTag listTag = getPlayerListTag(player);
            if (listTag.add(nbt)) {
                set.apply(player);
                player.getPersistentData().put("EquipmentSet", listTag);
            }
        });
    }

    public static void removePlayerSet(IEquipmentSet set, Player player) {
        ListTag listTag = getPlayerListTag(player);
        listTag.removeIf((nbt) -> parse(nbt).filter((equipmentSet) -> equipmentSet.equals(set)).isPresent());
        player.getPersistentData().put("EquipmentSet", listTag);
        set.clear(player);
    }
}
