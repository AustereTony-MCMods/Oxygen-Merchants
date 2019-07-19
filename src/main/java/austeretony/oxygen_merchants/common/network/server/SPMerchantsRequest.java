package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class SPMerchantsRequest extends ProxyPacket {

    private EnumRequest request;

    public SPMerchantsRequest() {}

    public SPMerchantsRequest(EnumRequest request) {
        this.request = request;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeByte(this.request.ordinal());
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        this.request = EnumRequest.values()[buffer.readByte()];
        switch (this.request) {
        case OPEN_MANAGEMENT_MENU:
            MerchantsManagerServer.instance().openManagementMenu(getEntityPlayerMP(netHandler));
            break;
        }
    }

    public enum EnumRequest {

        OPEN_MANAGEMENT_MENU
    }
}
