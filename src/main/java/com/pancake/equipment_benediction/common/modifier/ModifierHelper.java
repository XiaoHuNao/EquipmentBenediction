package com.pancake.equipment_benediction.common.modifier;

import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.common.network.ModMessages;
import com.pancake.equipment_benediction.common.network.message.PlayerModifierSyncS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.Optional;

public class ModifierHelper {
    public static ListTag getListTag(CompoundTag tag) {
        return tag != null ? tag.getList("Modifiers", 10) : new ListTag();
    }
     public static ListTag getItemStackListTag(ItemStack stack) {
        return getListTag(stack.getTag());
    }

    public static ListTag getPlayerListTag(Player player) {
        return getListTag(player.getPersistentData());
    }
    public static Optional<Tag> encodeStart(ModifierInstance instance) {
        return ModifierInstance.CODEC.encodeStart(NbtOps.INSTANCE, instance)
                .resultOrPartial(EquipmentBenediction.LOGGER::error);
    }
    public static Optional<ModifierInstance> parse(Tag tag) {
        return ModifierInstance.CODEC.parse(NbtOps.INSTANCE,tag)
                .resultOrPartial(EquipmentBenediction.LOGGER::error);
    }

    public static boolean hasModifier(ItemStack stack, IModifier modifier) {
        return getItemStackListTag(stack).stream().anyMatch((nbt) -> parse(nbt).filter(instance1 -> instance1.getModifier().equals(modifier)).isPresent());
    }
    public static boolean hasModifier(Player player, IModifier modifier) {
        return getPlayerListTag(player).stream().anyMatch((nbt) -> parse(nbt).filter(instance1 -> instance1.getModifier().equals(modifier)).isPresent());
    }

    public static boolean addItemStackModifier(ModifierInstance instance, ItemStack stack) {
        if (addModifier(instance, getItemStackListTag(stack))) {
            stack.getOrCreateTag().put("Modifiers", getItemStackListTag(stack));
            return true;
        }
        return false;
    }
    public static boolean removeItemStackModifier(ModifierInstance instance,ItemStack stack) {
        if (removeModifier(instance, getItemStackListTag(stack))) {
            stack.getOrCreateTag().put("Modifiers", getItemStackListTag(stack));
            return true;
        }
        return false;
    }
    public static boolean addPlayerModifier(ModifierInstance instance,Player player) {
        IModifier modifier = instance.getModifier();
        ListTag listTag = getPlayerListTag(player);
        boolean added = addModifier(instance, listTag);
        if (added) {
            player.getPersistentData().put("Modifiers", listTag);
            modifier.apply(player);
        }
        return added;
    }
    public static boolean removePlayerModifier(ModifierInstance instance,Player player) {
        IModifier modifier = instance.getModifier();
        boolean removed = removeModifier(instance, getPlayerListTag(player));
        if(removed) {
            player.getPersistentData().put("Modifiers", getPlayerListTag(player));
            modifier.clear(player);
        }
        return removed;
    }
    public static boolean removeModifier(ModifierInstance instance, ListTag tags) {
        for (Tag tag : tags) {
            Optional<ModifierInstance> optional = parse(tag);
            if (optional.isPresent()) {
                ModifierInstance modifierInstance = optional.get();
                if (modifierInstance.getModifier().equals(instance.getModifier())) {
                    return tags.remove(tag);
                }
            }
        }
        return false;
    }
    public static boolean addModifier(ModifierInstance instance, ListTag tags) {
        for (Tag tag : tags) {
            Optional<ModifierInstance> parse = parse(tag);
            if (parse.isPresent()) {
                ModifierInstance modifierInstance = parse.get();
                if (modifierInstance.getModifier().equals(instance.getModifier())) {
                    updateModifierInstance(modifierInstance, instance);
                    return true;
                }
            }
        }
        Optional<Tag> optionalTag = encodeStart(instance);
        if (optionalTag.isPresent()){
            tags.add(optionalTag.get());
            return true;
        }
        return false;
    }
    public static void updateModifierInstance(ModifierInstance oldInstance, ModifierInstance newInstance) {
        int amplifier = oldInstance.getAmplifier();
        int newAmplifier = newInstance.getAmplifier();

        if (newAmplifier > amplifier) {
            oldInstance.setAmplifier(newAmplifier);
        }
    }

    public static void updateModifier(ItemStack from, ItemStack to, EquipmentSlot slot, Player player) {
        ListTag fromTag = ModifierHelper.getItemStackListTag(from);
        ListTag toTag = ModifierHelper.getItemStackListTag(to);

        fromTag.forEach((tag) -> {
            removePlayerModifierWithUpdate(player, tag);
        });

        if (checkSlot(slot)) {
            addPlayerModifierWithUpdate(player, toTag);
        }
    }

    public static void removePlayerModifierWithUpdate(Player player, Tag tag) {
        parse(tag).ifPresent((instance) -> {
            if (removePlayerModifier(instance, player)) {
                ModMessages.sendToClient(new PlayerModifierSyncS2CPacket(instance, false), (ServerPlayer) player);
            }
        });
    }

    public static void addPlayerModifierWithUpdate(Player player, ListTag toTag) {
        toTag.forEach((tag) -> {
            parse(tag).ifPresent((instance) -> {
                if (addPlayerModifier(instance, player)) {
                    ModMessages.sendToClient(new PlayerModifierSyncS2CPacket(instance, true), (ServerPlayer) player);
                }
            });
        });
    }


    public static boolean checkSlot(EquipmentSlot stack) {
        EquipmentSlot[] slots = new EquipmentSlot[] {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET,
                EquipmentSlot.MAINHAND,
                EquipmentSlot.OFFHAND
        };

        for (EquipmentSlot slot : slots) {
            if (slot == stack) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasModifier(ItemStack itemStack) {
        return !getItemStackListTag(itemStack).isEmpty();
    }
}
