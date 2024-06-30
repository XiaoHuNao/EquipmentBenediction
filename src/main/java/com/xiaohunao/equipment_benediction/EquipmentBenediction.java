package com.xiaohunao.equipment_benediction;

import com.mojang.logging.LogUtils;
import com.xiaohunao.equipment_benediction.client.gui.hud.ModifierDeBugHudRenderer;
import com.xiaohunao.equipment_benediction.client.tooltip.EquipmentSetTooltipComponent;
import com.xiaohunao.equipment_benediction.common.config.ModConfig;
import com.xiaohunao.equipment_benediction.common.init.*;
import com.xiaohunao.equipment_benediction.compat.curios.event.subscriber.CurioPlayerEventSubscriber;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(EquipmentBenediction.MOD_ID)
public class EquipmentBenediction {
    public static final String MOD_ID = "equipment_benediction";
    public static final Logger LOGGER = LogUtils.getLogger();


    public static final CreativeModeTab CREATIVE_TABS = new CreativeModeTab(MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItem.REFORGED_HAMMER.get());
        }
    };



    public EquipmentBenediction() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onFMLCommonSetup);
        modEventBus.addListener(this::onRegisterClientTooltipComponentFactories);
        ModModifier.MODIFIER.register(modEventBus);
        ModEquipmentSet.EQUIPMENT_SET.register(modEventBus);
        ModItem.ITEMS.register(modEventBus);
        ModBlock.BLOCKS.register(modEventBus);
        ModBlockEntity.BLOCK_ENTITY.register(modEventBus);
        ModQuality.QUALITY.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);



        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);

        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(ModifierDeBugHudRenderer::registerOverlay);
        }

        if (ModList.get().isLoaded("curios")){
            MinecraftForge.EVENT_BUS.register(CurioPlayerEventSubscriber.class);
        }
    }

    @SubscribeEvent
    public void onFMLCommonSetup(FMLCommonSetupEvent event) {
        ModEquipmentSet.setup();
        ModModifier.setup();
        ModQuality.setup();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static <T> ResourceKey<Registry<T>> asResourceKey(String name) {
        return ResourceKey.createRegistryKey(asResource(name));
    }

    @SubscribeEvent
    public void onRegisterClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(EquipmentSetTooltipComponent.class, equipmentSetTooltipComponent -> equipmentSetTooltipComponent);
    }

//    @SubscribeEvent
//    public void onCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
//        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
//            event.accept(ModBlock.REFORGED_BLOCK);
//        }
//    }

//    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_TABS.register("equipment_benediction", () -> CreativeModeTab.builder()
//            .icon(() -> ModItem.REFORGED_HAMMER.get().getDefaultInstance())
//            .title(Component.translatable("itemGroup.equipment_benediction"))
//            .displayItems((parameters, output) -> {
//                output.accept(ModItem.REFORGED_ITEM.get());
//                output.accept(ModItem.REFORGED_HAMMER.get());
//            })
//            .build());
}
