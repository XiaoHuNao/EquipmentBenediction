package com.xiaohunao.equipment_benediction.common.quality;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.IQuality;
import com.xiaohunao.equipment_benediction.common.init.ModModifier;
import com.xiaohunao.equipment_benediction.common.init.ModQuality;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierHelper;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public class QualityHelper {
    public static ListTag getListTag(CompoundTag tag) {
        return tag != null ? tag.getList("Quality", Tag.TAG_STRING) : new ListTag();
    }
    public static ListTag getItemStackListTag(ItemStack stack) {
        return getListTag(stack.getTag());
    }
    public static Optional<Tag> encodeStart(IQuality set) {
        return ModQuality.REGISTRY
                .get()
                .getCodec()
                .encodeStart(NbtOps.INSTANCE, set)
                .resultOrPartial(EquipmentBenediction.LOGGER::error);
    }
    public static Optional<IQuality> parse(Tag tag) {
        return ModQuality.REGISTRY
                .get()
                .getCodec()
                .parse(NbtOps.INSTANCE, tag)
                .resultOrPartial(EquipmentBenediction.LOGGER::error);
    }
    public static boolean hasQuality(ItemStack stack, IQuality set) {
        return getItemStackListTag(stack).stream().anyMatch((nbt) -> parse(nbt).filter((equipmentSet) -> equipmentSet.equals(set)).isPresent());
    }
    public static boolean hasQuality(ItemStack stack){
        return !getItemStackListTag(stack).isEmpty();
    }
    public static IQuality getQuality(ItemStack stack) {
        if (hasQuality(stack)) {
            return parse(getItemStackListTag(stack).get(0)).orElse(null);
        }
        return null;
    }


    public static IQuality getOrCreateQuality(ItemStack stack) {
        if (!hasQuality(stack)) {
            generateRandomQuality(stack);
        }
        return getQuality(stack);
    }

    public static IQuality generateRandomQuality(ItemStack stack) {
        ClientLevel level = Minecraft.getInstance().level;
        long seed = stack.getCount() + Item.getId(stack.getItem()) + ForgeRegistries.ITEMS.getKey(stack.getItem()).hashCode( )+ level.getDayTime();
        level.random.setSeed(seed);
        if (level != null) {
            ModQuality.ITEM_WEIGHTED_QUALITY.get(stack.getItem()).getRandomValue(level.random).ifPresent((quality) -> {
                ListTag listTag = getItemStackListTag(stack);
                if(!listTag.isEmpty()) {
                    listTag.clear();
                }
                encodeStart(quality).ifPresent(listTag::add);
                stack.getOrCreateTag().put("Quality", listTag);
            });
        }
        return getQuality(stack);
    }

    public static void updateQuality(ItemStack stack) {
        if (hasQuality(stack)) {


        }else {
            ModQuality.REGISTRY.get().getEntries().forEach((entry) -> {
                if (entry.getValue().isViable().test(stack)) {
                    IQuality iQuality = generateRandomQuality(stack);
                    initModifier(stack,iQuality);
                }
            });
        }
    }

    public static void initModifier(ItemStack stack,IQuality quality) {
        List<ModifierInstance> modifiers = quality.getModifiers();
        int i = quality.getMaxModifierCount() - quality.getModifiers().size();
        for (int j = 0; j < i; j++) {
            ModModifier.WEIGHTED_MODIFIER.getRandomValue(Minecraft.getInstance().level.random).ifPresent((instance) -> {
                if (instance.getLevel() <= quality.getLevel()) {
                    modifiers.add(new ModifierInstance(instance));
                }
            });
        }
        modifiers.forEach((instance) -> {
            ModifierHelper.addItemStackModifier(instance,stack);
        });
    }

}
