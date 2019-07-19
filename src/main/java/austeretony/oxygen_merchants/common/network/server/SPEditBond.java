package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen.util.PacketBufferUtils;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPEditBond extends ProxyPacket {

    private long oldBondId, profileId;

    private String name, profession;

    public SPEditBond() {}

    public SPEditBond(long bondId, String name, String profession, long profileId) {
        this.oldBondId = bondId;
        this.name = name;
        this.profession = profession;
        this.profileId = profileId;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeLong(this.oldBondId);
        PacketBufferUtils.writeString(this.name, buffer);
        PacketBufferUtils.writeString(this.profession, buffer);
        buffer.writeLong(this.profileId);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        MerchantsManagerServer.instance().getBoundEntitiesManager().editBond(getEntityPlayerMP(netHandler), 
                buffer.readLong(), PacketBufferUtils.readString(buffer), PacketBufferUtils.readString(buffer), buffer.readLong());
    }
}
