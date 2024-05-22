package com.pancake.equipment_benediction.compat.kubejs;

import com.pancake.equipment_benediction.api.IEquipmentSet;
import com.pancake.equipment_benediction.api.IEquippable;
import com.pancake.equipment_benediction.common.bonus.BonusHandler;
import com.pancake.equipment_benediction.common.equipment_set.EquipmentSet;
import com.pancake.equipment_benediction.common.equippable.EquippableGroup;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class KubeJSEquipmentSet extends EquipmentSet {
    public KubeJSEquipmentSet(Builder builder) {
        super();
        this.handler = builder.handler;
        this.group = builder.group;
        this.color = builder.color;
    }

    @Override
    public void init(BonusHandler<IEquipmentSet> handler, EquippableGroup group) {

    }


    public static class Builder  extends BonusHandlerBuilder<IEquipmentSet,IEquipmentSet> {
        public EquippableGroup group = EquippableGroup.create();
        private int color;

        public Builder(ResourceLocation location) {
            super(location);
        }

        @Override
        public RegistryInfo<IEquipmentSet> getRegistryType() {
            return ModKubeJSPlugin.EQUIPMENT_SET;
        }

        @Override
        public IEquipmentSet createObject() {
            return new KubeJSEquipmentSet(this);
        }

        public Builder addGroup(IEquippable<?> equippable, Ingredient ingredient) {
            this.group.addGroup(equippable, ingredient);
            return this;
        }
        public Builder addBlacklist(IEquippable<?> equippable) {
            this.group.addBlacklist(equippable);
            return this;
        }
        public Builder color(int color) {
            this.color = color;
            return this;
        }
    }
}
