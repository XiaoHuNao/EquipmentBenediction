package com.xiaohunao.equipment_benediction.common.equippable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.common.init.EBCodecRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient;

public record VanillaEquippable(EquipmentSlot slotType) implements IEquippable {
    public static final MapCodec<VanillaEquippable> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EquipmentSlot.CODEC.fieldOf("slot_type").forGetter(VanillaEquippable::slotType)
    ).apply(instance, VanillaEquippable::new));

    public static final VanillaEquippable MAINHAND = new VanillaEquippable(EquipmentSlot.MAINHAND);
    public static final VanillaEquippable OFFHAND = new VanillaEquippable(EquipmentSlot.OFFHAND);
    public static final VanillaEquippable HEAD = new VanillaEquippable(EquipmentSlot.HEAD);
    public static final VanillaEquippable CHEST = new VanillaEquippable(EquipmentSlot.CHEST);
    public static final VanillaEquippable LEGS = new VanillaEquippable(EquipmentSlot.LEGS);
    public static final VanillaEquippable FEET = new VanillaEquippable(EquipmentSlot.FEET);

    public static final VanillaEquippable[] VALUES = new VanillaEquippable[]{
            MAINHAND, OFFHAND, HEAD, CHEST, LEGS, FEET
    };

    public static final VanillaEquippable[] ARMOR = new VanillaEquippable[]{
            HEAD, CHEST, LEGS, FEET
    };

    public static final VanillaEquippable[] HAND = new VanillaEquippable[]{
            MAINHAND, OFFHAND
    };

    @Override
    public boolean checkEquippable(LivingEntity livingEntity, Ingredient ingredient) {
        return ingredient.test(livingEntity.getItemBySlot(slotType));
    }

    @Override
    public MapCodec<? extends IEquippable> codec() {
        return EBCodecRegistries.VANILLA_EQUIPPABLE.get();
    }

    public static VanillaEquippable of(String slotType) {
        return new VanillaEquippable(EquipmentSlot.byName(slotType));
    }
    public static VanillaEquippable of(EquipmentSlot slotType) {
        return new VanillaEquippable(slotType);
    }

}