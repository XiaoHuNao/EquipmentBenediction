package com.xiaohunao.equipment_benediction;

import com.mojang.logging.LogUtils;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.init.*;
import com.xiaohunao.equipment_benediction.api.manager.ModifierManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

@Mod(EquipmentBenediction.MODID)
public class EquipmentBenediction{
    public static final String MODID = "equipment_benediction";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EquipmentBenediction(IEventBus modEventBus, ModContainer modContainer) {
        ModifierManager.getInstance().init(modEventBus);
        EquipmentSetManager.getInstance().init(modEventBus);


        EBModifiers.MODIFIERS.register(modEventBus);
        EBEquipmentSets.EQUIPMENT_SET.register(modEventBus);

        EBHookTypes.HOOK_TYPES.register(modEventBus);
        EBDataComponentTypes.DATA_COMPONENT_TYPE.register(modEventBus);
        EBAttachments.TYPES.register(modEventBus);
        modEventBus.addListener(EBRegistries::registerRegistries);
    }

    private void addDataPackListeners(AddReloadListenerEvent event) {
        event.addListener(ModifierManager.getInstance());
    }


    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static String asDescriptionId(String path) {
        return MODID + "." + path;
    }

    public static <T> ResourceKey<Registry<T>> asResourceKey(String path) {
        return ResourceKey.createRegistryKey(asResource(path));
    }
    public static <T> ResourceKey<T> asResourceKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return ResourceKey.create(registryKey, asResource(path));
    }

}
