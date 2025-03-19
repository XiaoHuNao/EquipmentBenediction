package com.xiaohunao.equipment_benediction.common.init;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;



public class EBAttachments {
    public static final DeferredRegister<AttachmentType<?>> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EquipmentBenediction.MODID);

    public static final DeferredHolder<AttachmentType<?>,AttachmentType<EntityHookManager>> ENTITY_HOOK_MANAGER = TYPES.register("entity_hook_manager",
            () -> AttachmentType.serializable(EntityHookManager::new).build());
}