package com.pancake.equipment_benediction.common.equipment_set.equippable;

import com.pancake.equipment_benediction.api.IEquippable;


public abstract class Equippable<T> implements IEquippable<T> {
    protected T slotType;
    public Equippable(T slotType) {
        this.slotType = slotType;
    }
    @Override
    public T getSlotType() {
        return slotType;
    }
}
