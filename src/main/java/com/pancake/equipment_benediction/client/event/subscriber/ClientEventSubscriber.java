package com.pancake.equipment_benediction.client.event.subscriber;


import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.client.models.entities.ModifierVillagerEntityModel;
import com.pancake.equipment_benediction.client.models.renderer.entity.ModifierVillagerEntityRenderer;
import com.pancake.equipment_benediction.common.init.ModEntities;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = EquipmentBenediction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventSubscriber {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers renderers) {
        renderers.registerEntityRenderer(ModEntities.MODIFIER_VILLAGER.get(), ModifierVillagerEntityRenderer::new);
    }
//    @SubscribeEvent
//    public static void registerBlockRenderers(EntityRenderersEvent.RegisterRenderers evt) {
//        BlockEntityRenderers.register();
//    }

//    @SubscribeEvent
//    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
//        event.registerLayerDefinition(ModelLayers.WANDERING_TRADER,() -> LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE,false),64,64));
//    }

}
