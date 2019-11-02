package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.RequestsFilterHelper;
import austeretony.oxygen_merchants.common.EnumMerchantOperation;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.server.MerchantsManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPMerchantOperation extends Packet {

    private int ordinal;

    private long profileId, offerId;

    public SPMerchantOperation() {}

    public SPMerchantOperation(EnumMerchantOperation operation, long profileId, long offerId) {
        this.ordinal = operation.ordinal();
        this.profileId = profileId;
        this.offerId = offerId;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeByte(this.ordinal);
        buffer.writeLong(this.profileId);
        buffer.writeLong(this.offerId);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        //if (RequestsFilterHelper.getLock(CommonReference.getPersistentUUID(playerMP), MerchantsMain.MERCHANT_OPERATION_REQUEST_ID)) {
        //TODO Increase lock resolution (seconds -> milliseconds)
            final int ordinal = buffer.readByte();
            final long 
            profileId = buffer.readLong(),
            offerId = buffer.readLong();
            if (ordinal >= 0 && ordinal < EnumMerchantOperation.values().length) 
                OxygenHelperServer.addRoutineTask(()->MerchantsManagerServer.instance().getPlayersManager().performOperation(playerMP, 
                        EnumMerchantOperation.values()[ordinal], profileId, offerId));
        //}
    }
}
