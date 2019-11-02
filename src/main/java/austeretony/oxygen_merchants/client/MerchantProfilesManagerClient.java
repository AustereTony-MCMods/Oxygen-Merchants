package austeretony.oxygen_merchants.client;

import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_merchants.common.MerchantProfile;
import austeretony.oxygen_merchants.common.network.server.SPCreateProfile;
import austeretony.oxygen_merchants.common.network.server.SPRemoveProfile;
import austeretony.oxygen_merchants.common.network.server.SPUpdateMerchantProfile;

public class MerchantProfilesManagerClient {

    private MerchantsManagerClient manager;

    protected MerchantProfilesManagerClient(MerchantsManagerClient manager) {
        this.manager = manager;
    }

    public void createProfileSynced(String name) {
        OxygenMain.network().sendToServer(new SPCreateProfile(name));
    }

    public void saveProfileChangesSynced(MerchantProfile changesBuffer) {
        OxygenMain.network().sendToServer(new SPUpdateMerchantProfile(changesBuffer));
    }

    public void removeProfileSynced(long profileId) {
        OxygenMain.network().sendToServer(new SPRemoveProfile(profileId));
    }

    public void profileCreated(MerchantProfile profile) {
        this.manager.getMerchantProfilesContainer().addProfile(profile);
        this.manager.getMerchantProfilesContainer().setChanged(true);
    }

    public void profileRemoved(MerchantProfile profile) {
        this.manager.getMerchantProfilesContainer().removeProfile(profile.getId());
        this.manager.getMerchantProfilesContainer().setChanged(true);
    }
}
