package com.xiaohunao.equipment_benediction.common.event;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.EntityEvent;

public class AfterEquipmentBenedictionUpdatedEvent extends EntityEvent {
    public AfterEquipmentBenedictionUpdatedEvent(Entity entity) {
        super(entity);
    }
}
