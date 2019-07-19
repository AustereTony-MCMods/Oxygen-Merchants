package austeretony.oxygen_merchants.common.network.client;

import java.util.Set;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.common.api.IOxygenTask;
import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.server.SPSendAbsentEntitiesIds;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPSyncValidEntitiesIds extends ProxyPacket {

    public CPSyncValidEntitiesIds() {}

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeShort(MerchantsManagerServer.instance().getBoundEntitiesManager().getBondsAmount());
        for (long id : MerchantsManagerServer.instance().getBoundEntitiesManager().getBondIds())
            buffer.writeLong(id);
    }

    @Override   
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        OxygenHelperClient.addRoutineTask(new IOxygenTask() {

            @Override
            public void execute() {
                try {
                    Thread.sleep(3000);//Just because client data loading fired ***AFTER*** this packet arrives at client, nasty hack =/
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                int k, j;
                long[] syncedIds = new long[buffer.readShort()];
                for (k = 0; k < syncedIds.length; k++)
                    syncedIds[k] = buffer.readLong();
                long[] needSyncEntities = new long[syncedIds.length];
                Set<Long> entitiesIds = MerchantsManagerClient.instance().getBoundEntitiesManager().getBondIds();
                BoundEntityEntry[] validEntities = new BoundEntityEntry[syncedIds.length];
                k = j = 0;
                for (long entityId : syncedIds)
                    if (!entitiesIds.contains(entityId))
                        needSyncEntities[k++] = entityId;    
                    else
                        validEntities[j++] = MerchantsManagerClient.instance().getBoundEntitiesManager().getBond(entityId);
                MerchantsManagerClient.instance().getBoundEntitiesManager().reset();
                for (BoundEntityEntry validEntity : validEntities) {
                    if (validEntity == null) break;
                    MerchantsManagerClient.instance().getBoundEntitiesManager().addBoundEntityEntry(validEntity);
                }

                MerchantsMain.network().sendToServer(new SPSendAbsentEntitiesIds(needSyncEntities, k));
            }
        }); 
    } 
}
