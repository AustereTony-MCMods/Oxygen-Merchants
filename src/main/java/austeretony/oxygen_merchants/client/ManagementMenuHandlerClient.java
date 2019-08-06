package austeretony.oxygen_merchants.client;

import java.util.Set;

import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.sync.gui.api.IComplexGUIHandlerClient;
import austeretony.oxygen.common.api.network.OxygenNetwork;
import austeretony.oxygen_merchants.client.gui.management.ManagementMenuGUIScreen;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import austeretony.oxygen_merchants.common.main.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import net.minecraft.network.PacketBuffer;

public class ManagementMenuHandlerClient implements IComplexGUIHandlerClient<MerchantProfile, BoundEntityEntry> {

    @Override
    public void open() {
        ClientReference.displayGuiScreen(new ManagementMenuGUIScreen());
    }

    @Override
    public OxygenNetwork getNetwork() {
        return MerchantsMain.network();
    }

    @Override
    public Set<Long> getIdentifiersFirst() {
        return MerchantsManagerClient.instance().getMerchantProfilesManager().getProfilesIds();
    }

    @Override
    public Set<Long> getIdentifiersSecond() {
        return MerchantsManagerClient.instance().getBoundEntitiesManager().getEntriesIds();
    }

    @Override
    public MerchantProfile getEntryFirst(long entryId) {
        return MerchantsManagerClient.instance().getMerchantProfilesManager().getProfile(entryId);
    }

    @Override
    public BoundEntityEntry getEntrySecond(long entryId) {
        return MerchantsManagerClient.instance().getBoundEntitiesManager().getBoundEntityEntry(entryId);
    }

    @Override
    public void clearDataFirst() {
        MerchantsManagerClient.instance().getMerchantProfilesManager().reset();
    }

    @Override
    public void clearDataSecond() {
        MerchantsManagerClient.instance().getBoundEntitiesManager().reset();
    }

    @Override
    public void addValidEntryFirst(MerchantProfile entry) {
        MerchantsManagerClient.instance().getMerchantProfilesManager().addProfile(entry);
    }

    @Override
    public void addValidEntrySecond(BoundEntityEntry entry) {
        MerchantsManagerClient.instance().getBoundEntitiesManager().addBoundEntityEntry(entry);
    }

    @Override
    public void readEntries(PacketBuffer buffer, int firstAmount, int secondAmount) {
        int i = 0;
        for (; i < firstAmount; i++)
            MerchantsManagerClient.instance().getMerchantProfilesManager().addProfile(MerchantProfile.read(buffer));
        for (i = 0; i < secondAmount; i++)
            MerchantsManagerClient.instance().getBoundEntitiesManager().addBoundEntityEntry(BoundEntityEntry.read(buffer));
    }
}
