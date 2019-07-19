package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPOpenMerchantMenu extends ProxyPacket {

    private int entityId;

    private long profileId; 

    public SPOpenMerchantMenu() {}

    public SPOpenMerchantMenu(int entityId, long profileId) {
        this.entityId = entityId;
        this.profileId = profileId;
    }

    @Override 
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeInt(this.entityId);
        buffer.writeLong(this.profileId);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        MerchantsManagerServer.instance().openMerchantMenu(getEntityPlayerMP(netHandler), buffer.readInt(), buffer.readLong());
    }
}
