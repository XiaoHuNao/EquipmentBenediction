package com.xiaohunao.equipment_benediction.common.event.subscriber;

import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.xiaohunao.equipment_benediction.api.manager.BenedictionManager;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.common.equippable.IEquippable;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.init.EBEquipmentSets;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;


@EventBusSubscriber
public class PlayerEventSubscriber {
    @SubscribeEvent
    public static void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        InteractionHand hand = event.getHand();
        Player player = event.getEntity();
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND) {
            return;
        }

        if (player.isShiftKeyDown() && !FMLEnvironment.production){
            EntityHookManager data = player.getData(EBAttachments.ENTITY_HOOK_MANAGER);
            HookMapManager hookMapManager = HookMapManager.getInstance();
            System.out.println(data);
            System.out.println(hookMapManager);

            EquipmentSet equipmentSet = EBEquipmentSets.DIAMOND_SET.get();

            IEquippable.CODEC.encodeStart(JsonOps.INSTANCE, VanillaEquippable.HEAD)
                .resultOrPartial(error -> System.out.println("Failed to encode IEquippable: " + error))
                .ifPresent(jsonElement -> System.out.println("IEquippable encoded: " + jsonElement));

            Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, Ingredient.of(Items.DIAMOND_HELMET))
                .resultOrPartial(error -> System.out.println("Failed to encode Ingredient: " + error))
                .ifPresent(jsonElement -> System.out.println("Ingredient encoded: " + jsonElement));

            HashMap<IEquippable, Ingredient> map = Maps.newHashMap();
            map.put(VanillaEquippable.HEAD, Ingredient.of(Items.DIAMOND_HELMET));

            Codec.unboundedMap(IEquippable.CODEC, Ingredient.CODEC)
                .encodeStart(JsonOps.INSTANCE, map)
                .resultOrPartial(error -> System.out.println("Failed to encode map: " + error))
                .ifPresent(jsonElement -> System.out.println("Map encoded: " + jsonElement));
        }

    }



}