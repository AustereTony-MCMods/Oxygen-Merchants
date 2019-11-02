package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.RequestsFilterHelper;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.server.MerchantsManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPCreateBond extends Packet {

    private int entityId;

    private String name, profession;

    private long profileId;

    public SPCreateBond() {}

    public SPCreateBond(int entityId, String name, String profession, long profileId) {
        this.entityId = entityId;
        this.name = name;
        this.profession = profession;
        this.profileId = profileId;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeInt(this.entityId);
        ByteBufUtils.writeString(this.name, buffer);
        ByteBufUtils.writeString(this.profession, buffer);
        buffer.writeLong(this.profileId);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (RequestsFilterHelper.getLock(CommonReference.getPersistentUUID(playerMP), MerchantsMain.ENTITY_MANAGEMENT_REQUEST_ID)) {
            final int entityId = buffer.readInt();
            final String 
            name = ByteBufUtils.readString(buffer),
            profession = ByteBufUtils.readString(buffer);
            final long profileId = buffer.readLong();
            OxygenHelperServer.addRoutineTask(()->MerchantsManagerServer.instance().getBoundEntitiesManager().createEntry(playerMP, entityId, name, profession, profileId));
        }
    }
}
