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

public class SPEditBond extends Packet {

    private long bondId, profileId;

    private String name, profession;

    public SPEditBond() {}

    public SPEditBond(long bondId, String name, String profession, long profileId) {
        this.bondId = bondId;
        this.name = name;
        this.profession = profession;
        this.profileId = profileId;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeLong(this.bondId);
        buffer.writeLong(this.profileId);
        ByteBufUtils.writeString(this.name, buffer);
        ByteBufUtils.writeString(this.profession, buffer);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (RequestsFilterHelper.getLock(CommonReference.getPersistentUUID(playerMP), MerchantsMain.ENTITY_MANAGEMENT_REQUEST_ID)) {
            final long 
            bondId = buffer.readLong(),
            profileId = buffer.readLong();
            final String 
            name = ByteBufUtils.readString(buffer),
            profession = ByteBufUtils.readString(buffer);
            OxygenHelperServer.addRoutineTask(()->MerchantsManagerServer.instance().getBoundEntitiesManager().editEntry(playerMP, bondId, name, profession, profileId));
        }
    }
}
