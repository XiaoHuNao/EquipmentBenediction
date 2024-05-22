package com.pancake.equipment_benediction.common.event.subscriber;

import com.mojang.datafixers.util.Either;
import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.api.IEquipmentSet;
import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.client.renderer.ReforgedRenderer;
import com.pancake.equipment_benediction.client.tooltip.EquipmentSetTooltipComponent;
import com.pancake.equipment_benediction.common.config.ModConfig;
import com.pancake.equipment_benediction.common.equipment_set.EquipmentSetHelper;
import com.pancake.equipment_benediction.common.init.ModBlockEntity;
import com.pancake.equipment_benediction.common.modifier.ModifierHelper;
import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = EquipmentBenediction.MOD_ID, value = Dist.CLIENT)
public class ClientForgeEvent {
    private final static String shiftKey = "equipment_benediction.description";

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!ModConfig.enableEquipmentSetTooltip.get()){
            return;
        }

        ItemStack itemStack = event.getItemStack();
        List<Component> toolTip = event.getToolTip();
        ModifierHelper.getItemStackListTag(itemStack).forEach((nbt) -> {
            ModifierInstance.CODEC.parse(NbtOps.INSTANCE,nbt)
                    .resultOrPartial(EquipmentBenediction.LOGGER::error)
                    .ifPresent((instance) -> {
                        IModifier modifier = instance.getModifier();
                        if (modifier == null || modifier.getDescription() == null || modifier.getDisplayName() == null) return;
                        if (Screen.hasShiftDown()) {
                            toolTip.add(modifier.getDisplayName());
                            toolTip.add(modifier.getDescription());
                        } else {
                            toolTip.add(modifier.getDisplayName());
                        }
                    });
        });


    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @OnlyIn(Dist.CLIENT)
    public static void onRenderTooltips(RenderTooltipEvent.GatherComponents event) {
        if (!ModConfig.enableEquipmentSetTooltip.get()){
            return;
        }

        List<Either<FormattedText, TooltipComponent>> tooltipElements = event.getTooltipElements();
        ItemStack itemStack = event.getItemStack();
        if (EquipmentSetHelper.hasSet(itemStack)) {
            Object[] array = EquipmentSetHelper.getSet(itemStack).toArray();
            for (int i = 0; i < array.length; i++) {
                int size = tooltipElements.size();
                IEquipmentSet set = (IEquipmentSet) array[i];

                if (set.getDescription() == null || set.getDisplayName() == null) return;

                if (Screen.hasShiftDown()){
                    tooltipElements.add(Either.left(set.getDisplayName()));
                    tooltipElements.add(Either.right(new EquipmentSetTooltipComponent(i * 10, 10, set)));
                    tooltipElements.add(Either.left(set.getDescription()));
                }else {
                    tooltipElements.add(Either.left(set.getDisplayName()));
                    tooltipElements.add(Either.right(new EquipmentSetTooltipComponent(i * 10, 10, set)));
                }
            }
        }

        if (ModifierHelper.hasModifier(itemStack) || EquipmentSetHelper.hasSet(itemStack)) {
            tooltipElements.add(Either.left(Component.translatable(shiftKey).withStyle(style -> style.withColor(0xb1b1b1))));
        }
    }


    @Mod.EventBusSubscriber(modid = EquipmentBenediction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ForgeEvent {
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntity.REFORGED_BLOCK_ENTITY.get(), ReforgedRenderer::new);
        }
    }
}