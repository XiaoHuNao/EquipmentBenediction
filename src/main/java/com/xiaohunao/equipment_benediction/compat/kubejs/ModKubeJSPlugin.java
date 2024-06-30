package com.xiaohunao.equipment_benediction.compat.kubejs;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetHelper;
import com.xiaohunao.equipment_benediction.common.equipment_set.equippable.Equippable;
import com.xiaohunao.equipment_benediction.common.equipment_set.equippable.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.equippable.VanillaIEquippable;
import com.xiaohunao.equipment_benediction.common.init.ModEquipmentSet;
import com.xiaohunao.equipment_benediction.common.init.ModModifier;
import com.xiaohunao.equipment_benediction.common.init.ModQuality;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierHelper;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import com.xiaohunao.equipment_benediction.common.quality.Quality;
import com.xiaohunao.equipment_benediction.common.quality.QualityHelper;
import com.xiaohunao.equipment_benediction.compat.curios.equippable.CurioEquippable;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.minecraftforge.fml.ModList;

public class ModKubeJSPlugin extends KubeJSPlugin {
    public static final RegistryInfo MODIFIER = RegistryInfo.of(ModModifier.KEY);
    public static final RegistryInfo EQUIPMENT_SET = RegistryInfo.of(ModEquipmentSet.KEY);
    public static final RegistryInfo QUALITY = RegistryInfo.of(ModQuality.KEY);

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