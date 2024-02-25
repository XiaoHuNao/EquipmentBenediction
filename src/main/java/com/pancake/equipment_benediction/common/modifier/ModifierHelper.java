package com.pancake.equipment_benediction.common.modifier;

import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.common.init.ModModifiers;
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
    public static ListTag getModifierListTag(CompoundTag tag) {
        return tag != null ? tag.getList("Modifiers", 10) : new ListTag();
    }

     public static ListTag getItemStackModifierListTag(ItemStack stack) {
        return getModifierListTag(stack.getTag());
    }

    public static ListTag getPlayerModifierListTag(Player player) {
        return getModifierListTag(player.getPersistentData());
    }
    public static Optional<Tag> encodeStart(ModifierInstance instance) {
        return ModifierInstance.CODEC.encodeStart(NbtOps.INSTANCE, instance)
                .resultOrPartial(EquipmentBenediction.LOGGER::error);
    }
    public static Optional<ModifierInstance> parse(Tag tag) {
        return ModifierInstance.CODEC.parse(NbtOps.INSTANCE,tag)
                .resultOrPartial(EquipmentBenediction.LOGGER::error);
    }

    public static boolean addItemStackModifier(ModifierInstance instance, ItemStack stack) {
        if (addModifier(instance, getItemStackModifierListTag(stack))) {
            stack.getOrCreateTag().put("Modifiers", getItemStackModifierListTag(stack));
            return true;
        }
        return false;
    }
    public static boolean removeItemStackModifier(ModifierInstance instance,ItemStack stack) {
        if (removeModifier(instance, getItemStackModifierListTag(stack))) {
            stack.getOrCreateTag().put("Modifiers", getItemStackModifierListTag(stack));
            return true;
        }
        return false;
    }
    public static boolean addPlayerModifier(ModifierInstance instance,Player player) {
        IModifier modifier = instance.getModifier();
        ListTag listTag = getPlayerModifierListTag(player);
        boolean added = addModifier(instance, listTag);
        if (added) {
            player.getPersistentData().put("Modifiers", listTag);
            registryEvent(modifier);
            modifier.addAttributeModifier(player);
            modifier.addEffectModifier(player);
        }
        return added;
    }
    public static boolean removePlayerModifier(ModifierInstance instance,Player player) {
        IModifier modifier = instance.getModifier();
        boolean removed = removeModifier(instance, getPlayerModifierListTag(player));
        if(removed) {
            player.getPersistentData().put("Modifiers", getPlayerModifierListTag(player));
            unregisterEvent(modifier);
            modifier.removeAttributeModifier(player);
            modifier.removeEffectModifier(player);
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

    private static void registryEvent(IModifier modifier) {
        IModifier value = ModModifiers.MODIFIER_REGISTRY.get().getValue(modifier.getRegistryName());
        if (value != null) {
            value.registryEvent();
        }
    }

    private static void unregisterEvent(IModifier modifier) {
        IModifier value = ModModifiers.MODIFIER_REGISTRY.get().getValue(modifier.getRegistryName());
        if (value != null) {
            value.unregisterEvent();
        }
    }

    public static void updateModifierInstance(ModifierInstance oldInstance, ModifierInstance newInstance) {
        int amplifier = oldInstance.getAmplifier();
        int newAmplifier = newInstance.getAmplifier();

        if (newAmplifier > amplifier) {
            oldInstance.setAmplifier(newAmplifier);
        }
    }

    public static void updateModifier(ItemStack from, ItemStack to, EquipmentSlot slot, Player player) {
        ListTag fromTag = ModifierHelper.getItemStackModifierListTag(from);
        ListTag toTag = ModifierHelper.getItemStackModifierListTag(to);
        ListTag playerTag = ModifierHelper.getPlayerModifierListTag(player);


        fromTag.forEach((tag) -> {
            parse(tag).ifPresent((instance) -> {
                removePlayerModifier(instance, player);
                ModMessages.sendToClient(new PlayerModifierSyncS2CPacket(instance, false), (ServerPlayer) player);
            });
        });

        if (checkSlot(slot)) {
            toTag.forEach((tag) -> {
                parse(tag).ifPresent((instance) -> {
                    addPlayerModifier(instance, player);
                    ModMessages.sendToClient(new PlayerModifierSyncS2CPacket(instance, true), (ServerPlayer) player);
                });
            });
        }
    }

    public static void updateTickModifiers(Player player) {
        ListTag listTag = getPlayerModifierListTag(player);
        listTag.forEach((tag) -> {
            parse(tag)
                .ifPresent((instance) -> {
                    instance.getModifier().getHandler().getTickConsumer().accept(player, instance);
                });
        });
    }

//    private void addEffectModifier(Player player) {
//        this.effectModifiers.forEach((effect, modifier) -> {
//            player.addEffect(modifier);
//        });
//    }
//
//    private void addAttributeModifier(Player player) {
//        this.attributeModifiers.forEach((attribute, modifier) -> {
//            AttributeInstance attributeInstance = player.getAttribute(attribute);
//            if (attributeInstance != null){
//                attributeInstance.addTransientModifier(modifier);
//            }
//        });
//    }

//
//
//    public static void applyModifier(ModifierInstance instance, ListTag tags) {
//        tags.forEach((tag) -> {
//            parse(tag).ifPresent((modifierInstance) -> {
//                if (modifierInstance.getModifier().equals(instance.getModifier())) {
//                    updateModifier(modifierInstance, instance);
//                }else {
//                    encodeStart(instance).ifPresent(tags::add);
//                }
//            });
//        });
//    }






//
//    public static void addModifier(ModifierInstance instance,ItemStack stack) {
//        applyModifier(instance, getModifierTags(stack));
//    }

//
//    public static void removeAllModifier(ListTag tags) {
//        tags.clear();
//    }
//
//    public static void updateModifier(ItemStack from, ItemStack to,EquipmentSlot slot, Player player) {
//        ListTag fromTag = ModifierHelper.getModifierTags(from);
//        ListTag toTag = ModifierHelper.getModifierTags(to);
//        ListTag playerTag = ModifierHelper.getModifierTags(player);
//
//        fromTag.forEach((tag) -> {
//            parse(tag).ifPresent((instance) -> {
//                removeModifier(instance,player);
//            });
//        });
//
//        if (checkSlot(slot)) {
//            toTag.forEach((tag) -> {
//                parse(tag)
//                    .ifPresent((instance) -> {
//                        addModifier(instance, player);
//                    });
//            });
//        }
//    }
//
//    public static void removeModifier(ModifierInstance instance, ListTag tags) {
//        tags.forEach((tag) -> {
//            parse(tag)
//                .ifPresent((modifierInstance) -> {
//                    if (modifierInstance.getModifier().equals(instance.getModifier())) {
//                        tags.remove(tag);
//                    }
//                });
//        });
//    }
//
//    public static void removeModifier(ModifierInstance instance,ItemStack stack) {
//        removeAllModifier(getModifierTags(stack));
//    }
//

//
//    public static void updateTickModifiers(Player player) {
//        ListTag listTag = getModifierTags(player);
//        listTag.forEach((tag) -> {
//            parse(tag)
//                .ifPresent((instance) -> {
//                    instance.getModifier().getHandler().getTickConsumer().accept(player, instance);
//                });
//        });
//    }

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
}
