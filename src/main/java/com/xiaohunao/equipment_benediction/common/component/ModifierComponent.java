package com.xiaohunao.equipment_benediction.common.component;

import com.mojang.serialization.Codec;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ModifierComponent(List<ModifierInstance> modifierInstances) implements DataComponentType<ModifierComponent> {
    public static final Codec<ModifierComponent> CODEC = Codec.list(ModifierInstance.CODEC).xmap(ModifierComponent::new, ModifierComponent::modifierInstances);
    public static final StreamCodec<ByteBuf, ModifierComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);





    @Override
    public @Nullable Codec<ModifierComponent> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public StreamCodec<? super RegistryFriendlyByteBuf, ModifierComponent> streamCodec() {
        return STREAM_CODEC;
    }
}
