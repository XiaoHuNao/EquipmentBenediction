package com.pancake.equipment_benediction.compat.kubejs;

import com.pancake.equipment_benediction.api.IEquipmentSet;
import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.api.IQuality;
import com.pancake.equipment_benediction.common.equipment_set.EquipmentSet;
import com.pancake.equipment_benediction.common.equipment_set.EquipmentSetHelper;
import com.pancake.equipment_benediction.common.equippable.Equippable;
import com.pancake.equipment_benediction.common.equippable.EquippableGroup;
import com.pancake.equipment_benediction.common.equippable.VanillaIEquippable;
import com.pancake.equipment_benediction.common.init.ModEquipmentSet;
import com.pancake.equipment_benediction.common.init.ModModifier;
import com.pancake.equipment_benediction.common.init.ModQuality;
import com.pancake.equipment_benediction.common.modifier.Modifier;
import com.pancake.equipment_benediction.common.modifier.ModifierHelper;
import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import com.pancake.equipment_benediction.common.quality.Quality;
import com.pancake.equipment_benediction.common.quality.QualityHelper;
import com.pancake.equipment_benediction.compat.curios.equippable.CurioEquippable;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.minecraftforge.fml.ModList;

public class ModKubeJSPlugin extends KubeJSPlugin {
    public static final RegistryInfo<IModifier> MODIFIER = RegistryInfo.of(ModModifier.KEY, IModifier.class);
    public static final RegistryInfo<IEquipmentSet> EQUIPMENT_SET = RegistryInfo.of(ModEquipmentSet.KEY, IEquipmentSet.class);
    public static final RegistryInfo<IQuality> QUALITY = RegistryInfo.of(ModQuality.KEY, IQuality.class);

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("Modifier", Modifier.class);
        event.add("ModModifiers", ModModifier.class);
        event.add("ModifierHelper", ModifierHelper.class);
        event.add("ModifierInstance", ModifierInstance.class);

        event.add("EquipmentSet", EquipmentSet.class);
        event.add("ModEquipmentSet",ModEquipmentSet.class);
        event.add("EquipmentSetHelper", EquipmentSetHelper.class);
        event.add("VanillaIEquippable", VanillaIEquippable.class);
        event.add("Equippable", Equippable.class);
        event.add("EquippableGroup", EquippableGroup.class);

        event.add("Quality", Quality.class);
        event.add("ModQuality", ModQuality.class);
        event.add("QualityHelper", QualityHelper.class);

        if (ModList.get().isLoaded("curios")){
            event.add("CurioEquippable", CurioEquippable.class);
        }
    }

    @Override
    public void init() {
        MODIFIER.addType("modifier", KubeJSModifier.Builder.class, KubeJSModifier.Builder::new);
        EQUIPMENT_SET.addType("equipment_set", KubeJSEquipmentSet.Builder.class, KubeJSEquipmentSet.Builder::new);
        QUALITY.addType("quality", KubeJSQuality.Builder.class, KubeJSQuality.Builder::new);
    }
}