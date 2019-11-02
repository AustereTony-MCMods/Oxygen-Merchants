package austeretony.oxygen_merchants.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.EnumMerchantOperation;
import austeretony.oxygen_merchants.common.MerchantOffer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPMerchantAction extends Packet {

    private int ordinal;

    private MerchantOffer offer;

    private long balance;

    public CPMerchantAction() {}

    public CPMerchantAction(EnumMerchantOperation operation, MerchantOffer offer, long balance) {
        this.ordinal = operation.ordinal();
        this.offer = offer;
        this.balance = balance;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeByte(this.ordinal);
        this.offer.write(buffer);
        buffer.writeLong(this.balance);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final int ordinal = buffer.readByte();
        final EnumMerchantOperation operation = EnumMerchantOperation.values()[ordinal];
        final MerchantOffer offer = MerchantOffer.read(buffer);
        final long balance = buffer.readLong();
        switch (EnumMerchantOperation.values()[ordinal]) {
        case BUY:
            OxygenHelperClient.addRoutineTask(()->MerchantsManagerClient.instance().getMenuManager().bought(offer, balance));
            break;
        case SELLING:
            OxygenHelperClient.addRoutineTask(()->MerchantsManagerClient.instance().getMenuManager().sold(offer, balance));
            break;
        }
    }
}
