package austeretony.oxygen_merchants.common.network.client;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.client.EntitiesEntriesSyncProcess;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPSyncValidEntitiesIds extends ProxyPacket {

    public CPSyncValidEntitiesIds() {}

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeShort(MerchantsManagerServer.instance().getBoundEntitiesManager().getEntriesAmount());
        for (long id : MerchantsManagerServer.instance().getBoundEntitiesManager().getEntriesIds())
            buffer.writeLong(id);
    }

    @Override   
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        long[] syncedIds = new long[buffer.readShort()];
        for (int i = 0; i < syncedIds.length; i++)
            syncedIds[i] = buffer.readLong();     
        OxygenHelperClient.addTemporaryProcess(new EntitiesEntriesSyncProcess(syncedIds));
    } 
}
