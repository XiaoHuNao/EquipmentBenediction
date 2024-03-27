package com.pancake.equipment_benediction.common.equipment_set;

import com.google.common.collect.ImmutableList;
import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.api.IEquipmentSet;
import com.pancake.equipment_benediction.common.init.ModEquipmentSet;
import com.pancake.equipment_benediction.common.network.ModMessages;
import com.pancake.equipment_benediction.common.network.message.PlayerEquipmentSetSyncS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class EquipmentSetHelper {
    public static ListTag getListTag(CompoundTag tag) {
        return tag != null ? tag.getList("EquipmentSet", 8) : new ListTag();
    }
    public static ListTag getPlayerListTag(Player player) {
        return getListTag(player.getPersistentData());
    }
    public static Optional<Tag> encodeStart(IEquipmentSet set) {
        return ModEquipmentSet.EQUIPMENT_SET_REGISTRY
                .get()
                .getCodec()
                .encodeStart(NbtOps.INSTANCE, set)
                .resultOrPartial(EquipmentBenediction.LOGGER::error);
    }
    public static Optional<IEquipmentSet> parse(Tag tag) {
        return ModEquipmentSet.EQUIPMENT_SET_REGISTRY
                .get()
                .getCodec()
                .parse(NbtOps.INSTANCE, tag)
                .resultOrPartial(EquipmentBenediction.LOGGER::error);
    }
    public static boolean hasSet(Player player, IEquipmentSet set) {
        return getPlayerListTag(player).stream().anyMatch((nbt) -> parse(nbt).filter((equipmentSet) -> equipmentSet.equals(set)).isPresent());
    }

    public static void updateSet(ItemStack from, ItemStack to, Player player) {
        if (hasSet(from)) {
            getSet(from).forEach((set) -> {
                if (!set.getGroup().checkEquippable(player)) {
                    removePlayerSetWithUpdate(set, player);
                    set.clear(player);
                }
            });
        }
        if (hasSet(to)) {
            getSet(to).forEach((set) -> {
                if (set.getGroup().checkEquippable(player) && !hasSet(player, set)) {
                    addPlayerSet(set, player);
                    set.apply(player);
                }
            });
        }
    }

    public static boolean hasSet(ItemStack stack) {
        return ModEquipmentSet.EQUIPMENT_SET_MAP.entries().stream().map(Map.Entry::getKey).anyMatch((ingredient) -> ingredient.test(stack));
    }

    public static Collection<IEquipmentSet> getSet(ItemStack stack) {
        return ModEquipmentSet.EQUIPMENT_SET_MAP.entries().stream()
                .filter((entry) -> entry.getKey().test(stack))
                .map(Map.Entry::getValue)
                .collect(ImmutableList.toImmutableList());
    }

    private static void addPlayerSetWithUpdate(IEquipmentSet set, Player player) {
        addPlayerSet(set, player);
        ModMessages.sendToClient(new PlayerEquipmentSetSyncS2CPacket(set, true), (ServerPlayer) player);
    }

    public static void addPlayerSet(IEquipmentSet set, Player player) {
        encodeStart(set).ifPresent((nbt) -> {
            ListTag listTag = getPlayerListTag(player);
            listTag.add(nbt);
            player.getPersistentData().put("EquipmentSet", listTag);
        });
    }

    private static void removePlayerSetWithUpdate(IEquipmentSet set, Player player) {
        removePlayerSet(set, player);
        ModMessages.sendToClient(new PlayerEquipmentSetSyncS2CPacket(set, false), (ServerPlayer) player);
    }

    public static void removePlayerSet(IEquipmentSet set, Player player) {
        ListTag listTag = getPlayerListTag(player);
        listTag.removeIf((nbt) -> parse(nbt).filter((equipmentSet) -> equipmentSet.equals(set)).isPresent());
        player.getPersistentData().put("EquipmentSet", listTag);
        ModMessages.sendToClient(new PlayerEquipmentSetSyncS2CPacket(set, false), (ServerPlayer) player);
    }
}
