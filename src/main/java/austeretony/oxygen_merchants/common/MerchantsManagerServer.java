package austeretony.oxygen_merchants.common;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.main.OperationsProcessor;
import austeretony.oxygen_merchants.common.main.OperationsProcessor.EnumOperation;
import austeretony.oxygen_merchants.common.network.client.CPMerchantsCommand;
import austeretony.oxygen_merchants.common.network.client.CPSyncDataOpenMenu;
import austeretony.oxygen_merchants.common.network.client.CPSyncValidEntitiesIds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class MerchantsManagerServer {

    private static MerchantsManagerServer instance;

    private final MerchantProfilesManagerServer profilesManager;

    private final BoundEntitiesManagerServer entitiesManager;

    private final Map<UUID, OperationsProcessor> containers = new ConcurrentHashMap<UUID, OperationsProcessor>();

    private MerchantsManagerServer() {
        this.profilesManager = new MerchantProfilesManagerServer();
        this.entitiesManager = new BoundEntitiesManagerServer();
    }

    public static void create() {
        if (instance == null) 
            instance = new MerchantsManagerServer();
    }

    public static MerchantsManagerServer instance() {
        return instance;
    }

    public MerchantProfilesManagerServer getMerchantProfilesManager() {
        return this.profilesManager;
    }

    public BoundEntitiesManagerServer getBoundEntitiesManager() {
        return this.entitiesManager;
    }

    public void createOperationsContainer(UUID playerUUID) {
        this.containers.put(playerUUID, new OperationsProcessor(playerUUID));
    }

    public void removeOperationsContainer(UUID playerUUID) {
        this.containers.remove(playerUUID);
    }

    public boolean containerExist(UUID playerUUID) {
        return this.containers.containsKey(playerUUID);
    }

    public OperationsProcessor getOperationsContainer(UUID playerUUID) {
        return this.containers.get(playerUUID);
    }

    public void runMerchantOperations() {
        for (OperationsProcessor container : this.containers.values())
            container.process();
    }

    //TODO onPlayerLoaded()
    public void onPlayerLoaded(EntityPlayer player) {
        MerchantsMain.network().sendTo(new CPSyncValidEntitiesIds(), (EntityPlayerMP) player); 
        this.createOperationsContainer(CommonReference.getPersistentUUID(player));
    }

    //TODO onPlayerUnloaded()
    public void onPlayerUnloaded(EntityPlayer player) {
        this.removeOperationsContainer(CommonReference.getPersistentUUID(player));
    }

    public void openMerchantMenu(EntityPlayerMP playerMP, int entityId, long profileId) {
        Entity entity = CommonReference.getEntityById(playerMP, entityId);
        if (entity != null) {
            if (this.entitiesManager.entryExist(CommonReference.getPersistentUUID(entity))
                    && CommonReference.isEntitiesNear(playerMP, entity, 5.0D)) {
                BoundEntityEntry entry = this.entitiesManager.getBoundEntityEntry(CommonReference.getPersistentUUID(entity));
                if (entry.getProfileId() != profileId) {
                    if (entry.getProfileId() != 0L)
                        MerchantsMain.network().sendTo(new CPSyncDataOpenMenu(entry, this.profilesManager.getProfile(entry.getProfileId())), playerMP); 
                } else
                    MerchantsMain.network().sendTo(new CPMerchantsCommand(CPMerchantsCommand.EnumCommand.OPEN_LAST_REQUESTED_MERCHANT_MENU), playerMP); 
            }
        }
    }

    public void performOperation(EntityPlayerMP playerMP, EnumOperation operation, long profileId, long offerId) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (this.containerExist(playerUUID))
            this.getOperationsContainer(playerUUID).addOperation(operation, profileId, offerId);
    }

    public void reset() {
        this.profilesManager.reset();
        this.entitiesManager.reset();
    }
}
