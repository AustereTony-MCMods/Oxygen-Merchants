package austeretony.oxygen_merchants.server;

import java.util.Set;
import java.util.UUID;

import austeretony.oxygen_core.server.sync.DataSyncHandlerServer;
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import austeretony.oxygen_merchants.common.main.MerchantsMain;

public class EntitiesSyncHandlerServer implements DataSyncHandlerServer<BoundEntityEntry> {

    @Override
    public int getDataId() {
        return MerchantsMain.ENTITIES_DATA_ID;
    }

    @Override
    public boolean allowSync(UUID playerUUID) {
        return true;
    }

    @Override
    public Set<Long> getIds(UUID playerUUID) {
        return MerchantsManagerServer.instance().getBoundEntitiesContainer().getEntriesIds();
    }

    @Override
    public BoundEntityEntry getEntry(UUID playerUUID, long entryId) {
        return MerchantsManagerServer.instance().getBoundEntitiesContainer().getBoundEntityEntry(entryId);
    }
}
