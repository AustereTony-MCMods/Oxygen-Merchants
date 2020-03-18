package austeretony.oxygen_merchants.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPTryOpenMerchantMenu extends Packet {

    private long profileId;

    private boolean debug;

    public CPTryOpenMerchantMenu() {}

    public CPTryOpenMerchantMenu(long profileId, boolean debug) {
        this.profileId = profileId;
        this.debug = debug;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeLong(this.profileId);
        buffer.writeBoolean(this.debug);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final long profileId = buffer.readLong();
        final boolean debug = buffer.readBoolean();
        OxygenHelperClient.addRoutineTask(()->MerchantsManagerClient.instance().getMenuManager().tryOpenMerchantMenu(profileId, debug));
    }
}
