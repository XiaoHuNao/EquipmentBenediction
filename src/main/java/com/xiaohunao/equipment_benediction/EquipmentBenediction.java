package com.xiaohunao.equipment_benediction;

import com.mojang.logging.LogUtils;
import com.xiaohunao.equipment_benediction.common.init.EQMapCodecs;
import com.xiaohunao.equipment_benediction.common.init.EQRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(EquipmentBenediction.MODID)
public class EquipmentBenediction{
    public static final String MODID = "equipment_benediction";
    public static final Logger LOGGER = LogUtils.getLogger();
    public EquipmentBenediction(IEventBus modEventBus, ModContainer modContainer) {
//        NeoForge.EVENT_BUS.register(this);

        EQMapCodecs.VERIFIER_CODEC.register(modEventBus);
        modEventBus.addListener(EQRegistries::registerRegistries);
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

//    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
//    public static class ClientModEvents {
//    }
}
