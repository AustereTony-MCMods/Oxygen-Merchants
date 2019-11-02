package austeretony.oxygen_merchants.client;

import java.util.Set;

import austeretony.oxygen_core.client.sync.DataSyncHandlerClient;
import austeretony.oxygen_core.client.sync.DataSyncListener;
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import austeretony.oxygen_merchants.common.main.MerchantsMain;

public class EntitiesSyncHandlerClient implements DataSyncHandlerClient<BoundEntityEntry> {

    @Override
    public int getDataId() {
        return MerchantsMain.ENTITIES_DATA_ID;
    }

    @Override
    public Class<BoundEntityEntry> getDataContainerClass() {
        return BoundEntityEntry.class;
    }

    @Override
    public Set<Long> getIds() {
        return MerchantsManagerClient.instance().getBoundEntitiesContainer().getEntriesIds();
    }

    @Override
    public void clearData() {
        MerchantsManagerClient.instance().getBoundEntitiesContainer().reset();
    }

    @Override
    public BoundEntityEntry getEntry(long entryId) {
        return MerchantsManagerClient.instance().getBoundEntitiesContainer().getBoundEntityEntry(entryId);
    }

    @Override
    public void addEntry(BoundEntityEntry entry) {
        MerchantsManagerClient.instance().getBoundEntitiesContainer().addEntry(entry);
    }

    @Override
    public void save() {
        MerchantsManagerClient.instance().getBoundEntitiesContainer().setChanged(true);
    }

    @Override
    public DataSyncListener getSyncListener() {
        return (updated)->MerchantsManagerClient.instance().getMenuManager().entitiesSynchronized();
    }
}
