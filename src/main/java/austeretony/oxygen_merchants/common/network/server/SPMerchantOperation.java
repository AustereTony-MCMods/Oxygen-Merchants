package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.EnumOxygenStatusMessage;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_merchants.common.EnumMerchantOperation;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.server.MerchantsManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPMerchantOperation extends Packet {

    private int ordinal;

    private long offerId;

    public SPMerchantOperation() {}

    public SPMerchantOperation(EnumMerchantOperation operation, long offerId) {
        this.ordinal = operation.ordinal();
        this.offerId = offerId;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeByte(this.ordinal);
        buffer.writeLong(this.offerId);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (OxygenHelperServer.isNetworkRequestAvailable(CommonReference.getPersistentUUID(playerMP), MerchantsMain.MERCHANT_OPERATION_REQUEST_ID)) {
            if (OxygenHelperServer.checkTimeOut(CommonReference.getPersistentUUID(playerMP), MerchantsMain.MERCHANT_MENU_TIMEOUT_ID) || CommonReference.isPlayerOpped(playerMP)) {
                final int ordinal = buffer.readByte();
                final long offerId = buffer.readLong();
                if (ordinal >= 0 && ordinal < EnumMerchantOperation.values().length) 
                    OxygenHelperServer.addRoutineTask(()->MerchantsManagerServer.instance().getPlayersManager().performOperation(playerMP, EnumMerchantOperation.values()[ordinal], offerId));
            } else
                OxygenHelperServer.sendStatusMessage(playerMP, OxygenMain.OXYGEN_CORE_MOD_INDEX, EnumOxygenStatusMessage.ACTION_TIMEOUT.ordinal()); 
        }
    }
}
