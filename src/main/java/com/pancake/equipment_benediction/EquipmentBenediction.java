package com.pancake.equipment_benediction;

import com.mojang.logging.LogUtils;
import com.pancake.equipment_benediction.client.gui.hud.ModifierDeBugHudRenderer;
import com.pancake.equipment_benediction.common.init.ModEntities;
import com.pancake.equipment_benediction.common.init.ModGameRules;
import com.pancake.equipment_benediction.common.init.ModModifiers;
import com.pancake.equipment_benediction.common.network.ModMessages;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(EquipmentBenediction.MOD_ID)
public class EquipmentBenediction {
    public static final String MOD_ID = "equipment_benediction";
    public static final Logger LOGGER = LogUtils.getLogger();
    public EquipmentBenediction() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onFMLCommonSetup);
        ModModifiers.MODIFIER.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);


        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(ModifierDeBugHudRenderer::registerOverlay);
        }
    }

    @SubscribeEvent
    public void onFMLCommonSetup(FMLCommonSetupEvent event) {
        ModMessages.register();
        ModGameRules.registerRules();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static <T> ResourceKey<Registry<T>> asResourceKey(String name) {
        return ResourceKey.createRegistryKey(asResource(name));
    }
}
