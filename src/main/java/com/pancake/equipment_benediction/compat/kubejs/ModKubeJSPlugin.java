package com.pancake.equipment_benediction.compat.kubejs;

import com.pancake.equipment_benediction.api.IEquipmentSet;
import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.common.equipment_set.EquipmentSetHelper;
import com.pancake.equipment_benediction.common.equippable.VanillaIEquippable;
import com.pancake.equipment_benediction.common.init.ModEquipmentSet;
import com.pancake.equipment_benediction.common.init.ModModifiers;
import com.pancake.equipment_benediction.common.modifier.ModifierHelper;
import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import com.pancake.equipment_benediction.compat.curios.equippable.CurioEquippable;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class ModKubeJSPlugin extends KubeJSPlugin {
    public static final RegistryInfo<IModifier> MODIFIER = RegistryInfo.of(ModModifiers.MODIFIER_KEY, IModifier.class);
    public static final RegistryInfo<IEquipmentSet> EQUIPMENT_SET = RegistryInfo.of(ModEquipmentSet.EQUIPMENT_SET_KEY, IEquipmentSet.class);

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("ModModifiers", ModModifiers.class);
        event.add("ModifierHelper", ModifierHelper.class);
        event.add("ModifierInstance", ModifierInstance.class);

        event.add("ModEquipmentSet",ModEquipmentSet.class);
        event.add("EquipmentSetHelper", EquipmentSetHelper.class);
        event.add("VanillaIEquippable", VanillaIEquippable.class);
        event.add("CurioEquippable", CurioEquippable.class);
    }

    @Override
    public void init() {
        MODIFIER.addType("modifier", KubeJSModifier.Builder.class, KubeJSModifier.Builder::new);
        EQUIPMENT_SET.addType("equipment_set", KubeJSEquipmentSet.Builder.class, KubeJSEquipmentSet.Builder::new);
    }
}