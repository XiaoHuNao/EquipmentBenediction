package com.pancake.equipment_benediction.api;

import net.minecraftforge.eventbus.api.Event;

import java.util.function.Consumer;

@FunctionalInterface
public interface ForgeEventConsumer<T> extends Consumer<T> {
}