package austeretony.oxygen_merchants.common.network.client;

import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPMerchantsCommand extends ProxyPacket {

    private EnumCommand command;

    public CPMerchantsCommand() {}

    public CPMerchantsCommand(EnumCommand command) {
        this.command = command;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        buffer.writeByte(this.command.ordinal());
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        this.command = EnumCommand.values()[buffer.readByte()];
        switch (this.command) {
        case OPEN_MANAGER_MENU:
            MerchantsManagerClient.instance().openManagementMenuDelegated();
            break;
        case OPEN_LAST_REQUESTED_MERCHANT_MENU:
            MerchantsManagerClient.instance().openLastRequestedMerchantMenuDelegated();
            break;
        }
    }

    public enum EnumCommand {

        OPEN_MANAGER_MENU,
        OPEN_LAST_REQUESTED_MERCHANT_MENU
    }
}
