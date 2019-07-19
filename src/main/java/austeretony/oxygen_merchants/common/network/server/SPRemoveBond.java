package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPRemoveBond extends ProxyPacket {

    private long bondId;

    public SPRemoveBond() {}

    public SPRemoveBond(long bondId) {
        this.bondId = bondId;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeLong(this.bondId);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        MerchantsManagerServer.instance().getBoundEntitiesManager().removeBond(getEntityPlayerMP(netHandler), buffer.readLong());
    }
}
