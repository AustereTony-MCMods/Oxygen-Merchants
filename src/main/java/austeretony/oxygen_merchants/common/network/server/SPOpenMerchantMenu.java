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

public class SPOpenMerchantMenu extends Packet {

    private int entityId;

    private long profileId; 

    public SPOpenMerchantMenu() {}

    public SPOpenMerchantMenu(int entityId, long profileId) {
        this.entityId = entityId;
        this.profileId = profileId;
    }

    @Override 
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeInt(this.entityId);
        buffer.writeLong(this.profileId);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (RequestsFilterHelper.getLock(CommonReference.getPersistentUUID(playerMP), MerchantsMain.MERCHANT_OPERATION_REQUEST_ID)) {
            final int entityId = buffer.readInt();
            final long profileId = buffer.readLong();
            OxygenHelperServer.addRoutineTask(()->MerchantsManagerServer.instance().getPlayersManager().openMerchantMenu(playerMP, entityId, profileId));
        }
    }
}
