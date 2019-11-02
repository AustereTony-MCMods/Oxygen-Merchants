package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.RequestsFilterHelper;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.server.MerchantsManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPRemoveProfile extends Packet {

    private long profileId;

    public SPRemoveProfile() {}

    public SPRemoveProfile(long id) {
        this.profileId = id;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeLong(this.profileId);
    }

    @Override   
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (RequestsFilterHelper.getLock(CommonReference.getPersistentUUID(playerMP), MerchantsMain.PROFILE_MANAGEMENT_REQUEST_ID)) {
            final long id = buffer.readLong();
            OxygenHelperServer.addRoutineTask(()->MerchantsManagerServer.instance().getMerchantProfilesManager().removeProfile(playerMP, id));
        }
    }
}
