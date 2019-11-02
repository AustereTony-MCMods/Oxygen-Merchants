package austeretony.oxygen_merchants.common.network.client;

import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPOpenMerchantMenu extends Packet {

    private long profileId;

    public CPOpenMerchantMenu() {}

    public CPOpenMerchantMenu(long profileId) {
        this.profileId = profileId;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeLong(this.profileId);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final long profileId = buffer.readLong();
        MerchantsManagerClient.instance().getMenuManager().openMerchantMenuDelegated(profileId);
    }
}
