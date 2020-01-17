package austeretony.oxygen_merchants.server;

import java.util.Set;
import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.server.sync.DataSyncHandlerServer;
import austeretony.oxygen_merchants.common.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;

public class ProfilesSyncHandlerServer implements DataSyncHandlerServer<MerchantProfile> {

    @Override
    public int getDataId() {
        return MerchantsMain.MERCHANT_PROFILES_DATA_ID;
    }

    @Override
    public boolean allowSync(UUID playerUUID) {
        return CommonReference.isPlayerOpped(CommonReference.playerByUUID(playerUUID));
    }

    @Override
    public Set<Long> getIds(UUID playerUUID) {
        return MerchantsManagerServer.instance().getMerchantProfilesContainer().getProfilesIds();
    }

    @Override
    public MerchantProfile getEntry(UUID playerUUID, long entryId) {
        return MerchantsManagerServer.instance().getMerchantProfilesContainer().getProfile(entryId);
    }
}
