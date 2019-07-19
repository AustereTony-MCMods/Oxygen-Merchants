package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import austeretony.oxygen_merchants.common.main.MerchantProfile;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPSendMerchantProfile extends ProxyPacket {

    private long oldProfileId;

    public SPSendMerchantProfile() {}

    public SPSendMerchantProfile(long oldId) {
        this.oldProfileId = oldId;
    }   

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeLong(this.oldProfileId);
        MerchantsManagerClient.instance().getMerchantProfilesManager().getProfile(this.oldProfileId + 1L).write(buffer);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        MerchantsManagerServer.instance().getMerchantProfilesManager().editProfile(getEntityPlayerMP(netHandler), buffer.readLong(), MerchantProfile.read(buffer));   
    }
}       
