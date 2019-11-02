package austeretony.oxygen_merchants.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import austeretony.oxygen_merchants.common.MerchantProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPSyncDataOpenMenu extends Packet {

    private BoundEntityEntry entry;

    private MerchantProfile merchantProfile;

    public CPSyncDataOpenMenu() {}

    public CPSyncDataOpenMenu(BoundEntityEntry entry, MerchantProfile merchantProfile) {
        this.entry = entry;
        this.merchantProfile = merchantProfile;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        this.entry.write(buffer);
        this.merchantProfile.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final BoundEntityEntry entry = new BoundEntityEntry();
        entry.read(buffer);
        final MerchantProfile profile = new MerchantProfile();
        profile.read(buffer);
        OxygenHelperClient.addRoutineTask(()->MerchantsManagerClient.instance().getMenuManager().updateDataOpenMerchantMenu(entry, profile));
    }
}
