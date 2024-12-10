package com.xiaohunao.equipment_benediction.common.bonus;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.common.init.EQMapCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public record AttributeBonus(Holder<Attribute> attribute,AttributeModifier attributeModifier) implements IBonus {
    public final static MapCodec<AttributeBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(AttributeBonus::attribute),
            AttributeModifier.CODEC.fieldOf("attributeModifier").forGetter(AttributeBonus::attributeModifier)
    ).apply(instance, AttributeBonus::new));

    @Override
    public void apply(Player player) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.addTransientModifier(attributeModifier);
        }
    }

    @Override
    public void clear(Player player) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.removeModifier(attributeModifier);
        }
    }


    @Override
    public MapCodec<? extends IBonus> mapCodec() {
        return EQMapCodecs.ATTRIBUTE_BONUS_CODEC.get();
    }
}
