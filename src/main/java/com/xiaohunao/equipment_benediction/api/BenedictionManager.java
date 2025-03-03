package com.xiaohunao.equipment_benediction.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import com.xiaohunao.equipment_benediction.common.manager.EBAbstractManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class BenedictionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(BenedictionManager.class);

    private static final BenedictionManager Instance = new BenedictionManager();
    protected final BiMap<String,EBAbstractManager<?>> managers = HashBiMap.create();
    protected final BiMap<ResourceLocation, IBenediction<?>> allBenedictions = HashBiMap.create();


    private BenedictionManager() {
    }

    public static BenedictionManager getInstance() {
        return Instance;
    }

    public void registerBenediction(String managerID, ResourceLocation id, IBenediction<?> benediction){
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(managerID, id.toDebugFileName());
        if (allBenedictions.containsKey(resourceLocation)) {
            LOGGER.error("Benediction already registered: {}", resourceLocation);
        }
        allBenedictions.put(resourceLocation, benediction);
    }

    public void registerManager(EBAbstractManager<?> manager){
        if (managers.containsKey(manager.directory)) {
            LOGGER.error("Manager already registered: {}", manager.directory);
        }
        managers.put(manager.directory, manager);
    }

    public IBenediction<?> getBenedictionByManagerId(ResourceLocation id){
        return allBenedictions.get(id);
    }

    public IBenediction<?> getBenedictionByManager(String managerID, ResourceLocation id){
        return allBenedictions.get(ResourceLocation.fromNamespaceAndPath(managerID, id.toDebugFileName()));
    }

    public IBenediction<?> getBenedictionFromManagers(ResourceLocation id){
        for (String managerID : managers.keySet()) {
            if (managers.get(managerID).hasResource(id)) {
                return managers.get(managerID).getResource(id);
            }
        }
        return null;
    }

    public ResourceLocation getBenedictionManagerId(IBenediction<?> benediction){
        return allBenedictions.inverse().get(benediction);
    }

    public ResourceLocation getBenedictionId(String managerID, IBenediction<?> benediction){
        EBAbstractManager<?> manager = managers.get(managerID);
        if (manager == null) {
            return null;
        }

        ResourceLocation location = allBenedictions.inverse().get(benediction);
        if (location != null) {
            return location;
        }
        try {
            return getResourceLocationFromManager(manager, benediction);
        } catch (Exception e) {
            return null;
        }
    }
    public ResourceLocation getBenedictionIdFromManagers(IBenediction<?> benediction){
        ResourceLocation benedictionManagerId = getBenedictionManagerId(benediction);
        String namespace = benedictionManagerId.getNamespace();
        EBAbstractManager<?> manager = managers.get(namespace);
        return getResourceLocationFromManager(manager, benediction);
    }

    @SuppressWarnings("unchecked")
    private <T extends IBenediction<?>> ResourceLocation getResourceLocationFromManager(EBAbstractManager<T> manager, IBenediction<?> benediction) {
        return manager.getResource((T) benediction);
    }


    public BiMap<String,EBAbstractManager<?>> getManagers(){
        return ImmutableBiMap.copyOf(managers);
    }

    public BiMap<ResourceLocation, IBenediction<?>> getAllBenedictions(){
        return ImmutableBiMap.copyOf(allBenedictions);
    }


}
