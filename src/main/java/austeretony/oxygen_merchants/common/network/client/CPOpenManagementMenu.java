package austeretony.oxygen_merchants.common.network.client;

import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPOpenManagementMenu extends Packet {

    public CPOpenManagementMenu() {}

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {}

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        MerchantsManagerClient.instance().getMenuManager().openManagementMenuDelegated();
    }
}
