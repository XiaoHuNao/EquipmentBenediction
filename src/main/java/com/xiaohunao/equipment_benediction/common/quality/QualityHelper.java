package com.xiaohunao.equipment_benediction.common.quality;

import com.google.common.collect.Lists;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.init.ModEquipmentSet;
import com.xiaohunao.equipment_benediction.common.init.ModModifier;
import com.xiaohunao.equipment_benediction.common.init.ModQuality;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierHelper;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
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
    
    public static Optional<Tag> encodeStart(Quality set) {
        ResourceLocation location = ModQuality.QUALITY_MAP.inverse().get(set);
        return location != null ? Optional.of(StringTag.valueOf(location.toString())) : Optional.empty();
    }
    public static Optional<Quality> parse(Tag tag) {
        StringTag stringTag = (StringTag) tag;
        Quality quality = ModQuality.QUALITY_MAP.get(ResourceLocation.tryParse(stringTag.getAsString()));
        return quality != null ? Optional.of(quality) : Optional.empty();
    }

    public static boolean hasQuality(ItemStack stack, Quality set) {
        return getItemStackListTag(stack).stream().anyMatch((nbt) -> parse(nbt).filter((equipmentSet) -> equipmentSet.equals(set)).isPresent());
    }
    public static Quality getQuality(ResourceLocation registryName) {
        return ModQuality.QUALITY_MAP.get(registryName);
    }

    public static boolean hasQuality(ItemStack stack){
        return !getItemStackListTag(stack).isEmpty();
    }
    public static Quality getQuality(ItemStack stack) {
        if (hasQuality(stack)) {
            return parse(getItemStackListTag(stack).get(0)).orElse(null);
        }
        return null;
    }


    public static Quality getOrCreateQuality(ItemStack stack) {
        if (!hasQuality(stack)) {
            generateRandomQuality(stack);
        }
        return getQuality(stack);
    }

    public static Quality generateRandomQuality(ItemStack stack) {
        ClientLevel level = Minecraft.getInstance().level;
        long seed = stack.getCount() + Item.getId(stack.getItem()) + ForgeRegistries.ITEMS.getKey(stack.getItem()).hashCode() + level.getDayTime();
        level.random.setSeed(seed);
        SimpleWeightedRandomList<Quality> list = getWeightedQualityList(stack);
        list.getRandomValue(level.random).ifPresent(quality -> {
            ListTag listTag = getItemStackListTag(stack);
            if(!listTag.isEmpty()) {
                listTag.clear();
            }
            encodeStart(quality).ifPresent(listTag::add);
            stack.getOrCreateTag().put("Quality", listTag);
        });
        return getQuality(stack);
    }
    public static SimpleWeightedRandomList<Quality> getWeightedQualityList(ItemStack stack){
        SimpleWeightedRandomList.Builder<Quality> builder = SimpleWeightedRandomList.builder();
        ModQuality.QUALITY_MAP.values().stream()
                .filter((quality) -> quality != getQuality(stack) && quality.isViable().test(stack))
                .forEach((quality) -> {
                    builder.add(quality, quality.getRarity());
                });

        return builder.build();
    }

    public static void updateQuality(ItemStack stack) {
        if (hasQuality(stack)) {

        }else {
            ModQuality.QUALITY_MAP.values().stream()
                    .filter((quality) -> quality.isAutoAdd() && quality.isViable().test(stack))
                    .findFirst()
                    .ifPresent((quality) -> {
                        ListTag listTag = getItemStackListTag(stack);
                        if(!listTag.isEmpty()) {
                            listTag.clear();
                        }
                        encodeStart(quality).ifPresent(listTag::add);
                        stack.getOrCreateTag().put("Quality", listTag);
                        QualityHelper.initModifier(stack,quality);
                    });
        }
    }

    public static void initModifier(ItemStack stack,Quality quality) {
        List<ModifierInstance> modifiers = Lists.newArrayList(quality.getFixedModifiers());

        int count = quality.getMaxModifierCount() - modifiers.size();
        for (int i = 0; i < count; i++) {
            quality.getWeightedModifiers().getRandomValue(Minecraft.getInstance().level.random).ifPresent((instance) -> {
                modifiers.add(new ModifierInstance(instance));
            });
        }

        int count1 = quality.getMaxModifierCount() - modifiers.size();
        for (int j = 0; j < count1; j++) {
            ModModifier.getWeightedList().getRandomValue(Minecraft.getInstance().level.random).ifPresent((instance) -> {
                if (instance.getLevel() <= quality.getLevel()) {
                    modifiers.add(new ModifierInstance(instance));
                }
            });
        }

        ModifierHelper.removeAllModifier(stack);
        modifiers.forEach((instance) -> {
            ModifierHelper.addItemStackModifier(instance,stack);
        });
        quality.getModifiers().clear();
        quality.getModifiers().addAll(modifiers);
    }

}
