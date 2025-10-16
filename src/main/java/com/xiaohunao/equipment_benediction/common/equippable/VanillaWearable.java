package com.xiaohunao.equipment_benediction.common.equippable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.common.init.EBCodecRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient;

public record VanillaWearable(EquipmentSlot slotType) implements IWearable {
    public static final MapCodec<VanillaWearable> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EquipmentSlot.CODEC.fieldOf("slot_type").forGetter(VanillaWearable::slotType)
    ).apply(instance, VanillaWearable::new));

    public static final VanillaWearable MAINHAND = new VanillaWearable(EquipmentSlot.MAINHAND);
    public static final VanillaWearable OFFHAND = new VanillaWearable(EquipmentSlot.OFFHAND);
    public static final VanillaWearable HEAD = new VanillaWearable(EquipmentSlot.HEAD);
    public static final VanillaWearable CHEST = new VanillaWearable(EquipmentSlot.CHEST);
    public static final VanillaWearable LEGS = new VanillaWearable(EquipmentSlot.LEGS);
    public static final VanillaWearable FEET = new VanillaWearable(EquipmentSlot.FEET);

    public static final VanillaWearable[] VALUES = new VanillaWearable[]{
            MAINHAND, OFFHAND, HEAD, CHEST, LEGS, FEET
    };

    public static final VanillaWearable[] ARMOR = new VanillaWearable[]{
            HEAD, CHEST, LEGS, FEET
    };

    public static final VanillaWearable[] HAND = new VanillaWearable[]{
            MAINHAND, OFFHAND
    };

    @Override
    public boolean checkWearable(LivingEntity livingEntity, Ingredient ingredient) {
        return ingredient.test(livingEntity.getItemBySlot(slotType));
    }

    @Override
    public MapCodec<? extends IWearable> codec() {
        return EBCodecRegistries.VANILLA_EQUIPPABLE.get();
    }


    @Override
    public ResourceLocation getIcon() {
        return switch (slotType) {
            case EquipmentSlot.MAINHAND ->  ResourceLocation.withDefaultNamespace("textures/item/empty_armor_slot_shield.png");
            case EquipmentSlot.OFFHAND -> ResourceLocation.withDefaultNamespace("textures/item/empty_armor_slot_shield.png");
            case EquipmentSlot.HEAD -> ResourceLocation.withDefaultNamespace("textures/item/empty_armor_slot_helmet.png");
            case EquipmentSlot.CHEST -> ResourceLocation.withDefaultNamespace("textures/item/empty_armor_slot_chestplate.png");
            case EquipmentSlot.LEGS -> ResourceLocation.withDefaultNamespace("textures/item/empty_armor_slot_leggings.png");
            case EquipmentSlot.FEET -> ResourceLocation.withDefaultNamespace("textures/item/empty_armor_slot_boots.png");
            default -> throw new IllegalArgumentException("Invalid slot type: " + slotType);
        };
    }

    @Override
    public Component getDesc() {
        return Component.translatable("minecraft." + "equipment_benediction.wearable." + slotType.getName());
    }

    public static VanillaWearable of(String slotType) {
        return new VanillaWearable(EquipmentSlot.byName(slotType));
    }
    public static VanillaWearable of(EquipmentSlot slotType) {
        return new VanillaWearable(slotType);
    }
}