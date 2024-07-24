package com.xiaohunao.equipment_benediction.common.modifier;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.IModifier;
import com.xiaohunao.equipment_benediction.common.init.ModModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class ModifierHelper {
    public static ListTag getListTag(CompoundTag tag) {
        return tag != null ? tag.getList("Modifiers", Tag.TAG_COMPOUND) : new ListTag();
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
    public static boolean hasModifier(ItemStack itemStack) {
        return !getItemStackListTag(itemStack).isEmpty();
    }
    public static List<IModifier> getModifiers(ItemStack stack) {
        ListTag listTag = getItemStackListTag(stack);
        return listTag.stream().map((tag) -> parse(tag).map(ModifierInstance::getModifier).orElse(null)).toList();
    }


    public static boolean addItemStackModifier(ModifierInstance instance, ItemStack stack) {
        ListTag listTag = getItemStackListTag(stack);
        if (addModifier(instance, listTag) && instance.getModifier().isViable().test(stack)) {
            if (stack.getTag() != null) {
                stack.getTag().put("Modifiers", listTag);
            }
            return true;
        }
        return false;
    }
    public static boolean addPlayerModifier(ModifierInstance instance,Player player) {
        IModifier modifier = instance.getModifier();
        ListTag listTag = getPlayerListTag(player);
        if (addModifier(instance, listTag)) {
            player.getPersistentData().put("Modifiers", listTag);
            modifier.apply(player);
            return true;
        }
        return false;
    }

    public static boolean removeItemStackModifier(ModifierInstance instance,ItemStack stack) {
        if (removeModifier(instance, getItemStackListTag(stack))) {
            if (stack.getTag() != null) {
                stack.getTag().put("Modifiers", getItemStackListTag(stack));
            }
            return true;
        }
        return false;
    }

    public static boolean removePlayerModifier(ModifierInstance instance,Player player) {
        IModifier modifier = instance.getModifier();
        if(removeModifier(instance, getPlayerListTag(player))) {
            player.getPersistentData().put("Modifiers", getPlayerListTag(player));
            modifier.clear(player);
            return true;
        }
        return false;
    }
    public static void removeAllModifier(ItemStack stack){
        stack.removeTagKey("Modifiers");
    }

    public static void removeModifier(IModifier modifier, ItemStack itemStack) {
        ListTag listTag = getItemStackListTag(itemStack);
        listTag.removeIf((tag) -> parse(tag).filter((instance) -> instance.getModifier().equals(modifier)).isPresent());
        if (itemStack.getTag() != null) {
            itemStack.getTag().put("Modifiers", listTag);
        }
    }

    public static boolean removeModifier(ModifierInstance instance, ListTag tags) {
        for (Tag tag : tags) {
            Optional<ModifierInstance> optional = parse(tag);
            if (optional.isPresent()) {
                ModifierInstance modifierInstance = optional.get();
                if (modifierInstance.getModifier() == null || instance.getModifier() == null){
                    return false;
                }

                if (modifierInstance.getModifier().equals(instance.getModifier())) {
                    return tags.remove(tag);
                }
            }
        }
        return false;
    }

    public static void removeNonViableModifiers(ItemStack itemStack) {
        ModifierHelper.getModifiers(itemStack).forEach(modifier -> {
            if (!modifier.isViable().test(itemStack)) {
                ModifierHelper.removeModifier(modifier, itemStack);
            }
        });
    }
    public static boolean addModifier(ModifierInstance instance, ListTag tags) {
        for (Tag tag : tags) {
            Optional<ModifierInstance> parse = parse(tag);
            if (parse.isPresent()) {
                ModifierInstance modifierInstance = parse.get();
                if (modifierInstance.getModifier() == null || instance.getModifier() == null){
                    return false;
                }
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

    public static void updateModifier(ItemStack from, ItemStack to, Player player) {
        ListTag fromTag = ModifierHelper.getItemStackListTag(from);
        ListTag toTag = ModifierHelper.getItemStackListTag(to);

        ModModifier.REGISTRY.get().getEntries().forEach((entry) -> {
            IModifier modifier = entry.getValue();
            if (! modifier.getGroup().checkEquippable(player) && ModifierHelper.hasModifier(player,modifier)) {
                ModifierHelper.removePlayerModifier(new ModifierInstance(modifier,0),player);
            }
        });

        fromTag.forEach((tag) -> {
            parse(tag).ifPresent((instance) -> {
                removePlayerModifier(instance, player);
            });
        });

        toTag.forEach((tag) -> {
            parse(tag).ifPresent((instance) -> {
                IModifier modifier = instance.getModifier();
                if (modifier != null && modifier.checkEquippable(player)) {
                    addPlayerModifier(instance, player);
                }
            });
        });
    }
}
