package com.xiaohunao.equipment_benediction.common.interfaces;

import net.minecraft.nbt.Tag;

public interface ISerializable<T extends Tag> {
    T serializeNBT();

    void deserializeNBT(T tag);
}
