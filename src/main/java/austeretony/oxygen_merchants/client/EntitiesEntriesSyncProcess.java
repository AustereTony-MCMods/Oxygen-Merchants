package austeretony.oxygen_merchants.client;

import java.util.Set;

import austeretony.oxygen.common.api.process.AbstractTemporaryProcess;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.server.SPSendAbsentEntitiesIds;

public class EntitiesEntriesSyncProcess extends AbstractTemporaryProcess {

    private final long[] ids;

    public EntitiesEntriesSyncProcess(long[] ids) {
        this.ids = ids;
    }

    @Override
    public int getExpireTime() {
        return 5;//five seconds
    }

    @Override
    public void process() {}

    @Override
    public void expired() {
        int k, j;
        long[] needSyncEntities = new long[this.ids.length];
        Set<Long> entitiesIds = MerchantsManagerClient.instance().getBoundEntitiesManager().getEntriesIds();
        BoundEntityEntry[] validEntities = new BoundEntityEntry[this.ids.length];
        k = j = 0;
        for (long entityId : this.ids)
            if (!entitiesIds.contains(entityId))
                needSyncEntities[k++] = entityId;    
            else
                validEntities[j++] = MerchantsManagerClient.instance().getBoundEntitiesManager().getBoundEntityEntry(entityId);
        MerchantsManagerClient.instance().getBoundEntitiesManager().reset();
        for (BoundEntityEntry validEntity : validEntities) {
            if (validEntity == null) break;
            MerchantsManagerClient.instance().getBoundEntitiesManager().addBoundEntityEntry(validEntity);
        }

        MerchantsMain.network().sendToServer(new SPSendAbsentEntitiesIds(needSyncEntities, k));
    }
}
