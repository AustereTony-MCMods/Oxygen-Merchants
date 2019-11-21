package austeretony.oxygen_merchants.server;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import austeretony.oxygen_merchants.common.EnumMerchantOperation;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.client.CPOpenMerchantMenu;
import austeretony.oxygen_merchants.common.network.client.CPSyncDataOpenMenu;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

public class PlayersManagerServer {

    private final Map<UUID, OperationsProcessor> containers = new ConcurrentHashMap<>();

    private final MerchantsManagerServer manager;

    protected PlayersManagerServer(MerchantsManagerServer manager) {
        this.manager = manager;
        OxygenManagerServer.instance().getExecutionManager().getExecutors().getSchedulerExecutorService().scheduleAtFixedRate(
                ()->this.processOperations(), 1000L, 250L, TimeUnit.MILLISECONDS);
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

    public void processOperations() {
        OxygenHelperServer.addRoutineTask(()->{
            for (OperationsProcessor container : this.containers.values())
                container.process();
        });
    }

    public void onPlayerLoaded(EntityPlayerMP playerMP) {
        OxygenManagerServer.instance().getDataSyncManager().syncData(playerMP, MerchantsMain.ENTITIES_DATA_ID);
        this.createOperationsContainer(CommonReference.getPersistentUUID(playerMP));
    }

    public void onPlayerUnloaded(EntityPlayerMP playerMP) {
        this.removeOperationsContainer(CommonReference.getPersistentUUID(playerMP));
    }

    public void openMerchantMenu(EntityPlayerMP playerMP, int entityId, long profileId) {
        Entity entity = CommonReference.getEntityById(playerMP, entityId);
        if (entity != null) {
            if (this.manager.getBoundEntitiesContainer().entryExist(CommonReference.getPersistentUUID(entity))
                    && CommonReference.isEntitiesNear(playerMP, entity, 5.0D)) {
                BoundEntityEntry entry = this.manager.getBoundEntitiesContainer().getBoundEntityEntry(CommonReference.getPersistentUUID(entity));
                if (entry.getMerchantProfileId() != profileId) {
                    if (entry.getMerchantProfileId() != 0L)
                        OxygenMain.network().sendTo(new CPSyncDataOpenMenu(entry, this.manager.getMerchantProfilesContainer().getProfile(entry.getMerchantProfileId())), playerMP); 
                } else
                    OxygenMain.network().sendTo(new CPOpenMerchantMenu(profileId), playerMP); 
            }
        }
    }

    public void performOperation(EntityPlayerMP playerMP, EnumMerchantOperation operation, long profileId, long offerId) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (this.containerExist(playerUUID))
            this.getOperationsContainer(playerUUID).addOperation(operation, profileId, offerId);
    }
}
