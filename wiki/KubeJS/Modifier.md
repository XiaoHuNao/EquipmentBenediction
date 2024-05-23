# Modifier

~~~js
StartupEvents.registry("equipment_benediction:modifier",event =>{
    event.create("jump")
        .Properties(100,5)
        .addEffectModifier("minecraft:jump_boost", -1, 0)
        .color(0x7a7b78)
})
~~~


## methods
~~~
Properties(int rarity, int level) // 设置属性
addEffectModifier(MobEffect effect, int duration, int amplifier) // 添加效果词条
addAttributeModifier(Attribute attribute, String uuid, double value, AttributeModifier.Operation operation) // 添加属性词条
addEvent(String eventClass, Consumer<?> consumer) // 添加事件
addTickBiConsumer<Player, ModifierInstance> consumer // 添加tick事件
addGroup(IEquippable<?> equippable, Ingredient ingredient) // 添加白名单组
addBlacklist(IEquippable<?> equippable) // 添加黑名单组
isViable(Predicate<ItemStack> isViable) // 是否是可以给予的物品
color(int color) // 设置颜色
~~~
