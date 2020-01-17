package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.server.MerchantsManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPRequestMerchantProfileSync extends Packet {

    private long profileId;

    public SPRequestMerchantProfileSync() {}

    public SPRequestMerchantProfileSync(long profileId) {
        this.profileId = profileId;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeLong(this.profileId);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (OxygenHelperServer.isNetworkRequestAvailable(CommonReference.getPersistentUUID(playerMP), MerchantsMain.MERCHANT_OPERATION_REQUEST_ID)) {
            final long profileId = buffer.readLong();
            MerchantsManagerServer.instance().getMerchantProfilesManager().syncProfile(playerMP, profileId);
        }
    }
}
