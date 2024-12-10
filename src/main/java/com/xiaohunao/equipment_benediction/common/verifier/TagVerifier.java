package com.xiaohunao.equipment_benediction.common.verifier;

import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.common.init.EQMapCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record TagVerifier(TagKey<Item> tagKey) implements IVerifier {
    public final static MapCodec<TagVerifier> CODEC = TagKey.codec(Registries.ITEM).fieldOf("verifier").xmap(TagVerifier::new, TagVerifier::tagKey);


    @Override
    public boolean verify(ItemStack stack) {
        return stack.is(tagKey);
    }
    @Override
    public MapCodec<? extends IVerifier> mapCodec() {
        return EQMapCodecs.TAG_VERIFIER_CODEC.get();
    }
}
