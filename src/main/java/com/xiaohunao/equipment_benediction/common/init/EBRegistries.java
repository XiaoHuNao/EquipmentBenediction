package com.xiaohunao.equipment_benediction.common.init;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.hook.HookType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;


public class EBRegistries {
    public static final Registry<HookType<?>> HOOK_TYPES = new RegistryBuilder<>(Keys.HOOK_TYPES).create();


    public static class Keys {
        public static final ResourceKey<Registry<HookType<?>>> HOOK_TYPES = EquipmentBenediction.asResourceKey("hook_type");
    }

    public static void registerRegistries(NewRegistryEvent event) {
        event.register(HOOK_TYPES);
    }

}
