package com.pancake.equipment_benediction.common.modifier;

import com.mojang.serialization.Codec;
import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.common.init.ModModifiers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class TrulyInvisibleModifier extends Modifier {
    public static final ResourceLocation IDENTIFIER = EquipmentBenediction.asResource("truly_invisible");
    @Override
    public void init(ModifierHandler.Builder handle) {
        handle.addEffectModifier(MobEffects.INVISIBILITY, 1, 0);
        handle.addAttributeModifier(Attributes.MOVEMENT_SPEED, "ABC8FE99-AD65-4007-9850-39D4527119CB", 0.25, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
