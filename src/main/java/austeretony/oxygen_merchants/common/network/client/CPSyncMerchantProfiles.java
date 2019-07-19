package austeretony.oxygen_merchants.common.network.client;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.common.api.OxygenGUIHelper;
import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import austeretony.oxygen_merchants.common.main.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPSyncMerchantProfiles extends ProxyPacket {

    private long[] ids;

    public CPSyncMerchantProfiles() {}

    public CPSyncMerchantProfiles(long... ids) {
        this.ids = ids;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeShort(this.ids.length);
        for (long id : this.ids)
            MerchantsManagerServer.instance().getMerchantProfilesManager().getProfile(id).write(buffer);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        int amount = buffer.readShort();        
        for (int i = 0; i < amount; i++)
            MerchantsManagerClient.instance().getMerchantProfilesManager().addProfile(MerchantProfile.read(buffer));
        OxygenHelperClient.savePersistentDataDelegated(MerchantsManagerClient.instance().getMerchantProfilesManager());

        OxygenGUIHelper.dataRecieved(MerchantsMain.PROFILES_MANAGEMENT_MENU_SCREEN_ID);
    }
}
