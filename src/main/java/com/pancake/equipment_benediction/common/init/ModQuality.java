package com.pancake.equipment_benediction.common.init;

import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.api.IEquipmentSet;
import com.pancake.equipment_benediction.api.IQuality;
import com.pancake.equipment_benediction.common.quality.CommonQuality;
import com.pancake.equipment_benediction.compat.kubejs.KubeJSQuality;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.function.Supplier;

public class ModQuality {
    public static SimpleWeightedRandomList<IQuality> WEIGHTED_QUALITY;
    public static final ResourceKey<Registry<IQuality>> KEY = EquipmentBenediction.asResourceKey("quality");
    public static final DeferredRegister<IQuality> QUALITY = DeferredRegister.create(KEY, EquipmentBenediction.MOD_ID);
    public static final Supplier<IForgeRegistry<IQuality>> REGISTRY = QUALITY.makeRegistry(RegistryBuilder::new);


//    public static final RegistryObject<IQuality> COMMON = QUALITY.register("common", CommonQuality::new);
//
//    public static final RegistryObject<IQuality> UNCOMMON = QUALITY.register("uncommon", () -> new KubeJSQuality.Builder()
//            .isViable(stack -> stack.getItem() instanceof ArmorItem)
//            .properties(100,5,2)
//            .recastingStack(Ingredient.of(ItemStack.EMPTY)));
//    );

    public static void setup() {
        SimpleWeightedRandomList.Builder<IQuality> builder = SimpleWeightedRandomList.builder();
        REGISTRY.get().getEntries().stream()
                .map(Map.Entry::getValue)
                .forEach((quality) -> {
                    builder.add(quality, quality.getRarity());
                });
        WEIGHTED_QUALITY = builder.build();
    }




}
