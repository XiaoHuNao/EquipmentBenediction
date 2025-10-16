package com.xiaohunao.equipment_benediction.common.equipment_set;


import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collection;
import java.util.Map;


public abstract class EquipmentSet implements IBenediction {
    private final static EquipmentSetManager manager = EquipmentSetManager.getInstance();

    protected HookMap hookMap;
    protected EquippableGroup equippableGroup;

    //代表图标物品
    protected Ingredient iconIngredient;

    protected EquipmentSet() {
        HookMap.Builder hookBuilder = HookMap.builder();
        EquippableGroup.Builder equippableGroupBuilder = new EquippableGroup.Builder();

        init(hookBuilder, equippableGroupBuilder);

        this.hookMap = hookBuilder.build();
        this.equippableGroup = equippableGroupBuilder.build();

        HookMapManager.getInstance().register(this, hookMap);
    }

    protected abstract void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup);

    public EquippableGroup getEquippableGroup() {
        return equippableGroup;
    }

    public ResourceLocation getBranchLocation(String branchName) {
        if (getEquippableGroup().equippableMaps().containsKey(branchName)) {
            ResourceLocation resource = manager.getResource(this);
            return ResourceLocation.fromNamespaceAndPath(resource.getNamespace(), resource.getPath() + "_" + branchName);
        }
        return null;
    }

    public Ingredient getIconIngredient() {
        return iconIngredient != null ? iconIngredient :
                equippableGroup.exclusivityMaps()
                .keySet().stream()
                .findFirst()
                .flatMap(equippableGroup -> equippableGroup.equipages().entrySet().stream().findFirst())
                .map(Map.Entry::getValue)
                .orElse(Ingredient.EMPTY);
    }


    public Collection<EquipmentSetBranch> allBranch() {
        return equippableGroup.equippableMaps().values();
    }

    public ResourceLocation getName() {
        return manager.getResource(this);
    }
}
