package austeretony.oxygen_merchants.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.MerchantProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPSyncProfileOpenMenu extends Packet {

    private MerchantProfile merchantProfile;

    public CPSyncProfileOpenMenu() {}

    public CPSyncProfileOpenMenu(MerchantProfile merchantProfile) {
        this.merchantProfile = merchantProfile;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        this.merchantProfile.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final MerchantProfile profile = new MerchantProfile();
        profile.read(buffer);
        OxygenHelperClient.addRoutineTask(()->MerchantsManagerClient.instance().getMenuManager().addProfileOpenMerchantMenu(profile));
    }
}
