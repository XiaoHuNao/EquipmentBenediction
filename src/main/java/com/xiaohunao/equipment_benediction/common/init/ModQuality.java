package com.xiaohunao.equipment_benediction.common.init;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.IQuality;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.Map;
import java.util.function.Supplier;

public class ModQuality {
    public static Multimap<Item,IQuality> ITEM_QUALITY = ArrayListMultimap.create();
    public static Map<Item, SimpleWeightedRandomList<IQuality>> ITEM_WEIGHTED_QUALITY = Maps.newHashMap();
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
        ForgeRegistries.ITEMS.getEntries().forEach((entry) -> {
            Item item = entry.getValue();
            ITEM_QUALITY.putAll(item, REGISTRY.get().getEntries().stream()
                    .map(Map.Entry::getValue)
                    .filter((quality) -> quality.isViable().test(item.getDefaultInstance()))
                    ::iterator);
        });

        ITEM_QUALITY.asMap().forEach((item, qualities) -> {
            SimpleWeightedRandomList.Builder<IQuality> builder = SimpleWeightedRandomList.builder();
            qualities.forEach((quality) -> {
                builder.add(quality, quality.getRarity());
            });
            ITEM_WEIGHTED_QUALITY.put(item, builder.build());
        });
    }




}
