package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen.util.PacketBufferUtils;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPCreateBond extends ProxyPacket {

    private long bondId;

    private int entityId;

    private String name, profession;

    private long profileId;

    public SPCreateBond() {}

    public SPCreateBond(long bondId, int entityId, String name, String profession, long profileId) {
        this.bondId = bondId;
        this.entityId = entityId;
        this.name = name;
        this.profession = profession;
        this.profileId = profileId;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeLong(this.bondId);
        buffer.writeInt(this.entityId);
        PacketBufferUtils.writeString(this.name, buffer);
        PacketBufferUtils.writeString(this.profession, buffer);
        buffer.writeLong(this.profileId);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        MerchantsManagerServer.instance().getBoundEntitiesManager().createEntry(getEntityPlayerMP(netHandler), 
                buffer.readLong(), buffer.readInt(), PacketBufferUtils.readString(buffer), PacketBufferUtils.readString(buffer), buffer.readLong());
    }
}
