package com.xiaohunao.equipment_benediction.common.attachment;

import com.mojang.serialization.Codec;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.init.EBEquipmentSets;

import java.util.List;

public record EquipmentSetAttachment(List<EquipmentSet> equipmentSets) {
    public static final Codec<EquipmentSetAttachment> CODEC = Codec.list(EBEquipmentSets.EQUIPMENT_SET.byNameCodec()).xmap(EquipmentSetAttachment::new, EquipmentSetAttachment::equipmentSets);



}
