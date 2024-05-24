package com.xiaohunao.equipment_benediction.common.bonus;

import com.xiaohunao.equipment_benediction.api.IBonus;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.function.Consumer;

public class EventBonus implements IBonus {
    private final Class eventClass;
    private final Consumer consumer;

    public EventBonus(Class<?> type, Consumer<?> consumer) {
        this.eventClass = type;
        this.consumer = consumer;
    }

    public Class getEventClass() {
        return eventClass;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    @Override
    public void apply(Player player) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, eventClass, consumer);
    }

    @Override
    public void clear(Player player) {
        MinecraftForge.EVENT_BUS.unregister(consumer);
    }
}
