package com.pancake.equipment_benediction.common.init;

import com.pancake.equipment_benediction.EquipmentBenediction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EquipmentBenediction.MOD_ID);

     public static final RegistryObject<Item> REFORGED_ITEM = ITEMS.register("reforged_item", () ->new BlockItem(ModBlock.REFORGED_BLOCK.get(), new Item.Properties()));
}
