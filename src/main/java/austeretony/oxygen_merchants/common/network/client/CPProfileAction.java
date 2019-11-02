package austeretony.oxygen_merchants.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.EnumAction;
import austeretony.oxygen_merchants.common.MerchantProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPProfileAction extends Packet {

    private MerchantProfile profile;

    private int ordinal;

    public CPProfileAction() {}

    public CPProfileAction(EnumAction action, MerchantProfile profile) {
        this.ordinal = action.ordinal();
        this.profile = profile;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeByte(this.ordinal);
        this.profile.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final int ordinal = buffer.readByte();
        final MerchantProfile profile = new MerchantProfile();
        profile.read(buffer);
        switch (EnumAction.values()[ordinal]) {
        case CREATED:
            OxygenHelperClient.addRoutineTask(()->MerchantsManagerClient.instance().getMenuManager().profileCreated(profile));
            break;
        case UPDATED:
            OxygenHelperClient.addRoutineTask(()->MerchantsManagerClient.instance().getMenuManager().profileUpdated(profile));
            break;
        case REMOVED:
            OxygenHelperClient.addRoutineTask(()->MerchantsManagerClient.instance().getMenuManager().profileRemoved(profile));
            break;
        }
    }
}
