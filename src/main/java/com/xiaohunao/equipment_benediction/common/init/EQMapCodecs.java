package com.xiaohunao.equipment_benediction.common.init;

import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.bonus.*;
import com.xiaohunao.equipment_benediction.common.verifier.ClassVerifier;
import com.xiaohunao.equipment_benediction.common.verifier.IDVerifier;
import com.xiaohunao.equipment_benediction.common.verifier.IVerifier;
import com.xiaohunao.equipment_benediction.common.verifier.TagVerifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EQMapCodecs {
    public static final DeferredRegister<MapCodec<? extends IVerifier>> VERIFIER_CODEC = DeferredRegister.create(EQRegistries.Keys.VERIFIER_CODEC, EquipmentBenediction.MODID);
    public static final DeferredRegister<MapCodec<? extends IBonus>> BONUS_CODEC = DeferredRegister.create(EQRegistries.Keys.BONUS_CODEC, EquipmentBenediction.MODID);


    public static final DeferredHolder<MapCodec<? extends IVerifier>, MapCodec<? extends IVerifier>> ID_VERIFIER_CODEC = VERIFIER_CODEC.register("id", () -> IDVerifier.CODEC);
    public static final DeferredHolder<MapCodec<? extends IVerifier>, MapCodec<? extends IVerifier>> TAG_VERIFIER_CODEC = VERIFIER_CODEC.register("tag", () -> TagVerifier.CODEC);
    public static final DeferredHolder<MapCodec<? extends IVerifier>, MapCodec<? extends IVerifier>> CLASS_VERIFIER_CODEC = VERIFIER_CODEC.register("class", () -> ClassVerifier.CODEC);


    public static final DeferredHolder<MapCodec<? extends IBonus>, MapCodec<? extends IBonus>> MOB_EFFECT_BONUS_CODEC = BONUS_CODEC.register("mob_effect", () -> MobEffectBonus.CODEC);
    public static final DeferredHolder<MapCodec<? extends IBonus>, MapCodec<? extends IBonus>> ATTRIBUTE_BONUS_CODEC = BONUS_CODEC.register("attribute", () -> AttributeBonus.CODEC);
    public static final DeferredHolder<MapCodec<? extends IBonus>, MapCodec<? extends IBonus>> MOB_EFFECT_IMMUNITIES_BONUS_CODEC = BONUS_CODEC.register("mob_effect_immunities", () -> MobEffectImmunitiesBonus.CODEC);
    public static final DeferredHolder<MapCodec<? extends IBonus>, MapCodec<? extends IBonus>> DAMAGE_IMMUNITIES_BONUS_CODEC = BONUS_CODEC.register("damage_immunities", () -> DamageTypesImmunitiesBonus.CODEC);



}
