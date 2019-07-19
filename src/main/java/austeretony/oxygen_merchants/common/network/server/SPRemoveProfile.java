package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPRemoveProfile extends ProxyPacket {

    private long id;

    public SPRemoveProfile() {}

    public SPRemoveProfile(long id) {
        this.id = id;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeLong(this.id);
    }

    @Override   
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        MerchantsManagerServer.instance().getMerchantProfilesManager().removeProfile(getEntityPlayerMP(netHandler), buffer.readLong());
    }
}
