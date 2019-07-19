package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.client.CPSyncEntityEntries;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPSendAbsentEntitiesIds extends ProxyPacket {

    private long[] entityIds;

    private int entitiesAmount;

    public SPSendAbsentEntitiesIds() {}

    public SPSendAbsentEntitiesIds(long[] entityIds, int entitiesAmount) {
        this.entityIds = entityIds;
        this.entitiesAmount = entitiesAmount;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeShort(this.entitiesAmount);
        for (long entryId : this.entityIds) {
            if (entryId == 0) break;
            buffer.writeLong(entryId);
        }
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        int amount = buffer.readShort();
        if (amount > 0) {
            long[] needSync = new long[amount]; 
            int index = 0;
            for (int i = 0; i < amount; i++)
                needSync[index++] = buffer.readLong();  
            MerchantsMain.network().sendTo(new CPSyncEntityEntries(needSync), getEntityPlayerMP(netHandler));
        }
    }
}
