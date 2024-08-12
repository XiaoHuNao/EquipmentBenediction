package com.xiaohunao.equipment_benediction.api;


import com.google.common.collect.Multimap;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.compat.curios.equippable.CurioEquippable;
import dev.latvian.mods.rhino.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;


public interface IEquippable {
    boolean checkEquippable(LivingEntity player, Ingredient ingredient);
    boolean equals(Object obj);
    static IEquippable wrap(Context context, Object object) {
        if (object == null) {
            return null;
        }else if (object instanceof IEquippable) {
            return (IEquippable) object;
        }else if (object instanceof String) {
            ResourceLocation location = ResourceLocation.tryParse((String) object);
            if (location != null) {
                String namespace = location.getNamespace();
                if (namespace.equals("minecraft")) {
                    return VanillaEquippable.of(location.getPath());
                }
                if (namespace.equals("curios") && ModList.get().isLoaded("curios")) {
                    return CurioEquippable.of(location.getPath());
                }
            }
        }
        return null;
    }
}
