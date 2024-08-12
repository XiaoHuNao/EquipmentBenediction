package com.xiaohunao.equipment_benediction.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.registry.RegistryEventJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface BenedictionEvents {
    EventGroup GROUP = EventGroup.of("BenedictionEvents");

    EventHandler REGISTRY = GROUP.server("registry", () -> BenedictionRegisterEventJS.class);


}
