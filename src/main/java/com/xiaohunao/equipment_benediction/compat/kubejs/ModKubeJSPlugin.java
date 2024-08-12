package com.xiaohunao.equipment_benediction.compat.kubejs;

import com.xiaohunao.equipment_benediction.api.IEquippable;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetHelper;
import com.xiaohunao.equipment_benediction.common.equippable.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
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
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.minecraftforge.fml.ModList;

public class ModKubeJSPlugin extends KubeJSPlugin {
    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("Modifier", Modifier.class);
        event.add("ModModifiers", ModModifier.class);
        event.add("ModifierHelper", ModifierHelper.class);
        event.add("ModifierInstance", ModifierInstance.class);

        event.add("EquipmentSet", EquipmentSet.class);
        event.add("ModEquipmentSet", ModEquipmentSet.class);
        event.add("EquipmentSetHelper", EquipmentSetHelper.class);
        event.add("VanillaIEquippable", VanillaEquippable.class);
        event.add("EquippableGroup", EquippableGroup.class);

        event.add("Quality", Quality.class);
        event.add("ModQuality", ModQuality.class);
        event.add("QualityHelper", QualityHelper.class);

        if (ModList.get().isLoaded("curios")){
            event.add("CurioEquippable", CurioEquippable.class);
        }
    }

    @Override
    public void registerEvents() {
        BenedictionEvents.GROUP.register();
    }

    @Override
    public void onServerReload() {
        BenedictionRegisterEventJS registerEventJS = new BenedictionRegisterEventJS();
        BenedictionEvents.REGISTRY.post(ScriptType.SERVER,registerEventJS);
    }

    @Override
    public void registerTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
        typeWrappers.register(Modifier.class,Modifier::wrap);
        typeWrappers.register(EquipmentSet.class,EquipmentSet::wrap);
        typeWrappers.register(Quality.class,Quality::wrap);
        typeWrappers.register(IEquippable.class,IEquippable::wrap);
    }
}