package com.pancake.equipment_benediction.compat.kubejs;

import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.common.init.ModModifiers;
import com.pancake.equipment_benediction.common.modifier.ModifierHelper;
import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class ModKubeJSPlugin extends KubeJSPlugin {
    public static final RegistryInfo<IModifier> MODIFIER = RegistryInfo.of(ModModifiers.MODIFIER_KEY, IModifier.class);

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("ModModifiers", ModModifiers.class);
        event.add("ModifierHelper", ModifierHelper.class);
        event.add("ModifierInstance", ModifierInstance.class);
    }

    @Override
    public void init() {
        MODIFIER.addType("modifier", KubeJSModifier.Builder.class, KubeJSModifier.Builder::new);
    }
}