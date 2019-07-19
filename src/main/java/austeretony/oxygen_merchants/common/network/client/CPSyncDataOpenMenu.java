package austeretony.oxygen_merchants.common.network.client;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.common.network.ProxyPacket;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import austeretony.oxygen_merchants.common.main.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public class CPSyncDataOpenMenu extends ProxyPacket {

    private BoundEntityEntry entry;

    private MerchantProfile merchantProfile;

    public CPSyncDataOpenMenu() {}

    public CPSyncDataOpenMenu(BoundEntityEntry entry, MerchantProfile merchantProfile) {
        this.entry = entry;
        this.merchantProfile = merchantProfile;
    }

    @Override
    public void write(PacketBuffer buffer, INetHandler netHandler) {
        this.entry.write(buffer);
        this.merchantProfile.write(buffer);
    }

    @Override
    public void read(PacketBuffer buffer, INetHandler netHandler) {
        this.entry = BoundEntityEntry.read(buffer);
        MerchantsManagerClient.instance().getBoundEntitiesManager().addBoundEntityEntry(this.entry);
        this.merchantProfile = MerchantProfile.read(buffer);
        MerchantsManagerClient.instance().getMerchantProfilesManager().addProfile(this.merchantProfile);
        MerchantsManagerClient.instance().openMerchantMenuDelegated(this.merchantProfile.getId());

        OxygenHelperClient.savePersistentDataDelegated(MerchantsManagerClient.instance().getBoundEntitiesManager());
        OxygenHelperClient.savePersistentDataDelegated(MerchantsManagerClient.instance().getMerchantProfilesManager());

        MerchantsMain.LOGGER.info("Synchronized merchant profile <{}>.", this.merchantProfile.getName());
    }
}
