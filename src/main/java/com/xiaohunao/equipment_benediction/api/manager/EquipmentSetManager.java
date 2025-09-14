package com.xiaohunao.equipment_benediction.api.manager;

import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xiaohunao.equipment_benediction.common.attachment.EquipmentSetHookManager;
import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.network.EntityHookManagerSyncPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EquipmentSetManager extends EBAbstractManager<EquipmentSet> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentSetManager.class);
    private static final Gson GSON = new Gson();
    public static final String FOLDER = "equipment_set";
    private static final EquipmentSetManager INSTANCE = new EquipmentSetManager();

    private static final Multimap<EquipmentSet, Ingredient> equipmentSetIngredientMaps = HashMultimap.create();
    private static final Multimap<EquipmentSet, EquipmentSetBranch> equipmentSetDataMaps = HashMultimap.create();
    private static final BiMap<ResourceLocation, EquipmentSetBranch> equipmentSetDataRegistry = HashBiMap.create();


    protected EquipmentSetManager() {
        super(GSON, FOLDER);
    }

    public static EquipmentSetManager getInstance() {
        return INSTANCE;
    }

    public void updateSet(ServerPlayer player, LivingEquipmentChangeContext livingEquipmentChangeContext) {
        LivingEntity livingEntity = livingEquipmentChangeContext.livingEntity();
        if (livingEntity.level().isClientSide){
            return;
        }
        ItemStack to = livingEquipmentChangeContext.to();
        ItemStack from = livingEquipmentChangeContext.from();

        EquipmentSetHookManager setHookManager = livingEntity.getData(EBAttachments.ENTITY_HOOK_MANAGER).getSetHookManager();
        Multimap<EquipmentSet, EquipmentSetBranch> activatedEquipped = setHookManager.getActivatedSetBranch();
        Multimap<EquipmentSet, EquipmentSetBranch> selectedEquipped = setHookManager.getSelectedEquipped();

        List<Map.Entry<EquipmentSet, EquipmentSetBranch>> toRemove = Lists.newArrayList();
        activatedEquipped.entries().forEach(entry -> {
            if (!entry.getValue().isValid(livingEntity)) {
                toRemove.add(entry);
            }
//            PacketDistributor.sendToServer(new PostEquipOrUnequipEquipmentHookPayload(false));
        });

        toRemove.forEach(entry -> {
            setHookManager.updateEquippable(livingEntity,entry.getKey(), entry.getValue(), false);
        });




        Collection<EquipmentSet> allPossibleSets = getEquipmentSet(to);
        selectedEquipped.forEach((set, setData) -> {
            boolean contains = allPossibleSets.contains(set);
            boolean valid = setData.isValid(livingEntity);

            if (contains && valid) {
                setHookManager.updateEquippable(livingEntity, set, setData, true);
            }
        });

        setHookManager.sync(livingEntity);
        CompoundTag tag = new CompoundTag();
        tag.put("set_hook", setHookManager.serializeNBT(null));
        PacketDistributor.sendToPlayer(player, new EntityHookManagerSyncPayload(livingEntity.getId(),tag));
    }
    
    public boolean hasEquipmentSet(ItemStack stack) {
        return !getEquipmentSet(stack).isEmpty();
    }


    public Collection<EquipmentSet> getEquipmentSet(ItemStack stack) {
        List<EquipmentSet> sets = Lists.newArrayList();
        equipmentSetIngredientMaps.entries().forEach(entry -> {
            if (entry.getValue().test(stack)) {
                sets.add(entry.getKey());
            }
        });
        return ImmutableList.copyOf(sets);
    }

    public Multimap<EquipmentSet,Ingredient> getEquipmentSetIngredientMaps() {
        return equipmentSetIngredientMaps;
    }


    @Override
    protected String getManagerName() {
        return "EquipmentSet";
    }

    @Override
    protected void loadDynamicResource(ResourceLocation id, JsonElement json, ResourceManager manager, ProfilerFiller profiler) {
    }

    @Override
    protected void registerDynamic(ResourceLocation id, EquipmentSet equipmentSet) {
        super.registerDynamic(id, equipmentSet);
        registerEquipmentSet(equipmentSet);
    }

    @Override
    public void registerStatic(ResourceLocation id, EquipmentSet equipmentSet) {
        super.registerStatic(id, equipmentSet);
        registerEquipmentSet(equipmentSet);
    }

    private void registerEquipmentSet(EquipmentSet set){
        set.getEquippableGroup().equippableMaps().forEach((key, data) -> {
            equipmentSetDataMaps.put(set, data);
            equipmentSetDataRegistry.put(set.getBranchLocation(key), data);

            data.equipages().values().forEach(ingredient -> {
                if (!ingredient.isEmpty()) {
                    equipmentSetIngredientMaps.put(set, ingredient);
                }
            });
        });

    }

    public Multimap<EquipmentSet, EquipmentSetBranch> getEquipmentSetDataMaps() {
        return equipmentSetDataMaps;
    }



    public EquipmentSetBranch getBranchResource(ResourceLocation branchLocation) {
        return equipmentSetDataRegistry.get(branchLocation);
    }

    public ResourceLocation getBranchResource(EquipmentSetBranch setData) {
        return equipmentSetDataRegistry.inverse().get(setData);
    }
}
