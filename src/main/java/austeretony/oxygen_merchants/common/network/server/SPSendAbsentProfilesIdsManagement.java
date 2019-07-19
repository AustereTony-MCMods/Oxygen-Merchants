package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.client.CPSyncEntityEntries;
import austeretony.oxygen_merchants.common.network.client.CPSyncMerchantProfiles;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPSendAbsentProfilesIdsManagement extends ProxyPacket {

    private long[] profileIds, entityIds;

    private int profilesAmount, entitiesAmount;

    public SPSendAbsentProfilesIdsManagement() {}

    public SPSendAbsentProfilesIdsManagement(long[] profileIds, int profilesAmount, long[] entityIds, int entitiesAmount) {
        this.profileIds = profileIds;
        this.profilesAmount = profilesAmount;
        this.entityIds = entityIds;
        this.entitiesAmount = entitiesAmount;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeShort(this.profilesAmount);
        for (long entryId : this.profileIds) {
            if (entryId == 0) break;
            buffer.writeLong(entryId);
        }

        buffer.writeShort(this.entitiesAmount);
        for (long entryId : this.entityIds) {
            if (entryId == 0) break;
            buffer.writeLong(entryId);
        }
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);

        int amount = buffer.readShort();
        if (amount > 0) {
            long[] needSync = new long[amount];
            int index = 0;
            for (int i = 0; i < amount; i++)
                needSync[index++] = buffer.readLong();
            MerchantsMain.network().sendTo(new CPSyncMerchantProfiles(needSync), playerMP);
        }

        amount = buffer.readShort();
        if (amount > 0) {
            long[] needSync = new long[amount];
            int index = 0;
            for (int i = 0; i < amount; i++)
                needSync[index++] = buffer.readLong();  
            MerchantsMain.network().sendTo(new CPSyncEntityEntries(needSync), playerMP);
        }

        MerchantsManagerServer.instance().openManagementMenuSynced(playerMP);
        OxygenHelperServer.setSyncing(CommonReference.getPersistentUUID(playerMP), false);
    }
}
