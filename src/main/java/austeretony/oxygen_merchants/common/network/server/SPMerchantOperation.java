package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import austeretony.oxygen_merchants.common.main.OperationsProcessor;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPMerchantOperation extends ProxyPacket {

    private OperationsProcessor.EnumOperation operation;

    private long profileId, offerId;

    public SPMerchantOperation() {}

    public SPMerchantOperation(OperationsProcessor.EnumOperation operation, long profileId, long offerId) {
        this.operation = operation;
        this.profileId = profileId;
        this.offerId = offerId;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeByte(this.operation.ordinal());
        buffer.writeLong(this.profileId);
        buffer.writeLong(this.offerId);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        MerchantsManagerServer.instance().performOperation(getEntityPlayerMP(netHandler), 
                OperationsProcessor.EnumOperation.values()[buffer.readByte()], buffer.readLong(), buffer.readLong());
    }
}
