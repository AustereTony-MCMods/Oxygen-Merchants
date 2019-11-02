package austeretony.oxygen_merchants.client;

import java.util.Set;

import austeretony.oxygen_core.client.sync.DataSyncHandlerClient;
import austeretony.oxygen_core.client.sync.DataSyncListener;
import austeretony.oxygen_merchants.common.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;

public class ProfilesSyncHandlerClient implements DataSyncHandlerClient<MerchantProfile> {

    @Override
    public int getDataId() {
        return MerchantsMain.MERCHANT_PROFILES_DATA_ID;
    }

    @Override
    public Class<MerchantProfile> getDataContainerClass() {
        return MerchantProfile.class;
    }

    @Override
    public Set<Long> getIds() {
        return MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfilesIds();
    }

    @Override
    public void clearData() {
        MerchantsManagerClient.instance().getMerchantProfilesContainer().reset();
    }

    @Override
    public MerchantProfile getEntry(long entryId) {
        return MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfile(entryId);
    }

    @Override
    public void addEntry(MerchantProfile entry) {
        MerchantsManagerClient.instance().getMerchantProfilesContainer().addProfile(entry);
    }

    @Override
    public void save() {
        MerchantsManagerClient.instance().getMerchantProfilesContainer().setChanged(true);
    }

    @Override
    public DataSyncListener getSyncListener() {
        return (updated)->MerchantsManagerClient.instance().getMenuManager().profilesSynchronized();
    }
}
