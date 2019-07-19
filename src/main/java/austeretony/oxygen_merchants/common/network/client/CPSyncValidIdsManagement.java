package austeretony.oxygen_merchants.common.network.client;

import java.util.Set;

import austeretony.oxygen.common.api.OxygenGUIHelper;
import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import austeretony.oxygen_merchants.common.main.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.server.SPSendAbsentProfilesIdsManagement;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPSyncValidIdsManagement extends ProxyPacket {

    public CPSyncValidIdsManagement() {}

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeShort(MerchantsManagerServer.instance().getMerchantProfilesManager().getProfilesAmount());
        for (long id : MerchantsManagerServer.instance().getMerchantProfilesManager().getProfilesIds())
            buffer.writeLong(id);

        buffer.writeShort(MerchantsManagerServer.instance().getBoundEntitiesManager().getBondsAmount());
        for (long id : MerchantsManagerServer.instance().getBoundEntitiesManager().getBondIds())
            buffer.writeLong(id);      
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        //merchant profiles
        long[] syncedIds = new long[buffer.readShort()];
        int 
        i = 0, 
        j = 0,
        k = 0;
        for (; i < syncedIds.length; i++)
            syncedIds[i] = buffer.readLong();
        long[] needSyncProfiles = new long[syncedIds.length];
        Set<Long> profilesIds = MerchantsManagerClient.instance().getMerchantProfilesManager().getProfilesIds();
        MerchantProfile[] validProfiles = new MerchantProfile[syncedIds.length];
        i = 0;
        for (long profileId : syncedIds)
            if (!profilesIds.contains(profileId))
                needSyncProfiles[i++] = profileId;    
            else
                validProfiles[j++] = MerchantsManagerClient.instance().getMerchantProfilesManager().getProfile(profileId);
        MerchantsManagerClient.instance().getMerchantProfilesManager().reset();
        for (MerchantProfile validProfile : validProfiles) {
            if (validProfile == null) break;
            MerchantsManagerClient.instance().getMerchantProfilesManager().addProfile(validProfile);
        }

        //bound entities
        syncedIds = new long[buffer.readShort()];
        for (; k < syncedIds.length; k++)
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

        if (i > 0)
            OxygenGUIHelper.needSync(MerchantsMain.PROFILES_MANAGEMENT_MENU_SCREEN_ID);
        if (k > 0)
            OxygenGUIHelper.needSync(MerchantsMain.ENTITIES_MANAGEMENT_MENU_SCREEN_ID);

        MerchantsMain.network().sendToServer(new SPSendAbsentProfilesIdsManagement(needSyncProfiles, i, needSyncEntities, k));
    }
}
