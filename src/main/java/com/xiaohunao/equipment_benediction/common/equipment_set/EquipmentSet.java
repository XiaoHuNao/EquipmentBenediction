package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.entity.LivingEntity;

public class EquipmentSet  {
    protected final HookMap hookMap;
    protected final EquippableGroup equippableGroup;
    protected final WearBonus wearBonus;

    public EquipmentSet() {
        HookMap.Builder hookBuilder = HookMap.builder();
        EquippableGroup.Builder equippableGroupBuilder = new EquippableGroup.Builder();
        WearBonus.Builder bonusBuilder = new WearBonus.Builder();
        init(hookBuilder,equippableGroupBuilder,bonusBuilder);

        WearBonus wearBonus = bonusBuilder.build();
        hookBuilder.addHook(EBHookTypes.EQUIP_EQUIPMENT.get(), wearBonus);
        hookBuilder.addHook(EBHookTypes.UNEQUIP_EQUIPMENT.get(),wearBonus);
        hookBuilder.addHook(EBHookTypes.LIVING_INCOMING_DAMAGE.get(),wearBonus);
        hookBuilder.addHook(EBHookTypes.MOB_EFFECT_APPLICABLE.get(),wearBonus);

        this.hookMap = hookBuilder.build();
        this.equippableGroup = equippableGroupBuilder.build();
        this.wearBonus = wearBonus;
    }
    protected EquipmentSet(HookMap hooks, EquippableGroup equippableGroup, WearBonus wearBonus) {
        this.hookMap = hooks;
        this.equippableGroup = equippableGroup;
        this.wearBonus = wearBonus;
    }

    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup, WearBonus.Builder wearBonus) {

    }

    public boolean isValid(LivingEntity livingEntity) {
        return equippableGroup.isValid(livingEntity);
    }
}
