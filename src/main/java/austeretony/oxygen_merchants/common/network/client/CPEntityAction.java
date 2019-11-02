package austeretony.oxygen_merchants.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import austeretony.oxygen_merchants.common.EnumAction;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPEntityAction extends Packet {

    private BoundEntityEntry entry;

    private int ordinal;

    public CPEntityAction() {}

    public CPEntityAction(EnumAction action, BoundEntityEntry entry) {
        this.ordinal = action.ordinal();
        this.entry = entry;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeByte(this.ordinal);
        this.entry.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final int ordinal = buffer.readByte();
        final BoundEntityEntry entry = new BoundEntityEntry();
        entry.read(buffer);
        switch (EnumAction.values()[ordinal]) {
        case CREATED:
            OxygenHelperClient.addRoutineTask(()->MerchantsManagerClient.instance().getMenuManager().entityCreated(entry));
            break;
        case UPDATED:
            OxygenHelperClient.addRoutineTask(()->MerchantsManagerClient.instance().getMenuManager().entityUpdated(entry));
            break;
        case REMOVED:
            OxygenHelperClient.addRoutineTask(()->MerchantsManagerClient.instance().getMenuManager().entityRemoved(entry));
            break;
        }
    }
}
