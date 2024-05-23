# Quality

~~~js
StartupEvents.registry("equipment_benediction:quality",event => {
    event.create("common")
        .isViable(stack => {
            const $armorItem = Java.loadClass("net.minecraft.world.item.ArmorItem");
            return stack.getItem() instanceof $armorItem
        })
        .properties(100, 5, 2)
        .recastingStack(Ingredient.of("minecraft:iron_ingot"))
        .color(0x7a7b78)
})
~~~

## methods
~~~js
isViable(Predicate<ItemStack> stack) // 是否是可以给予该品质的物品
properties(int rarity, int level, int maxModifierCount) // 设置该品质的属性
recastingStack(Ingredient ingredient) // 重铸所需的材料
color(int color) // 设置品质的颜色
addFixedModifier(ModifierInstance modifier) // 添加固定的词条
~~~
