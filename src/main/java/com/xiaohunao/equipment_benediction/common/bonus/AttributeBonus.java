package com.xiaohunao.equipment_benediction.common.bonus;

import com.xiaohunao.equipment_benediction.api.IBonus;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public class AttributeBonus implements IBonus {
    private Attribute attribute;
    private AttributeModifier attributeModifier;

    public AttributeBonus(Attribute attribute, AttributeModifier attributeModifier) {
        this.attribute = attribute;
        this.attributeModifier = attributeModifier;
    }

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


}
