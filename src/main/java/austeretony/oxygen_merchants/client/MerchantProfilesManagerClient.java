package austeretony.oxygen_merchants.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.common.api.IPersistentData;
import austeretony.oxygen.util.StreamUtils;
import austeretony.oxygen_merchants.common.main.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.server.SPCreateProfile;
import austeretony.oxygen_merchants.common.network.server.SPRemoveProfile;
import austeretony.oxygen_merchants.common.network.server.SPSendMerchantProfile;

public class MerchantProfilesManagerClient implements IPersistentData {

    private final Map<Long, MerchantProfile> merchantProfiles = new ConcurrentHashMap<Long, MerchantProfile>();  

    public int getProfilesAmount() {
        return this.merchantProfiles.size();
    }

    public Set<Long> getProfilesIds() {
        return this.merchantProfiles.keySet();
    }

    public Collection<MerchantProfile> getProfiles() {
        return this.merchantProfiles.values();
    }

    public boolean profileExist(long profileId) {
        return this.merchantProfiles.containsKey(profileId);
    }

    public MerchantProfile getProfile(long profileId) {
        return this.merchantProfiles.get(profileId);
    }

    public void addProfile(MerchantProfile profile) {
        this.merchantProfiles.put(profile.getId(), profile);
    }

    public void removeProfile(long profileId) {
        this.merchantProfiles.remove(profileId);
    }

    public void createProfileSynced(String name) {
        MerchantProfile profile = new MerchantProfile();
        profile.createId();
        profile.setName(name);
        profile.setUseCurrency(true);
        this.merchantProfiles.put(profile.getId(), profile);
        MerchantsMain.network().sendToServer(new SPCreateProfile(profile.getId(), name));
        OxygenHelperClient.savePersistentDataDelegated(this);
    }

    public void saveProfileChangesSynced(MerchantProfile changesBuffer) {
        long oldProfileId = changesBuffer.getId();
        this.removeProfile(oldProfileId);
        changesBuffer.setId(oldProfileId + 1L);
        this.addProfile(changesBuffer);
        MerchantsManagerClient.instance().getBoundEntitiesManager().merchantProfileEdited(oldProfileId);
        MerchantsMain.network().sendToServer(new SPSendMerchantProfile(oldProfileId));
        OxygenHelperClient.savePersistentDataDelegated(this);        
    }

    public void removeProfileSynced(long profileId) {
        this.removeProfile(profileId);
        MerchantsManagerClient.instance().getBoundEntitiesManager().merchantProfileRemoved(profileId);
        MerchantsMain.network().sendToServer(new SPRemoveProfile(profileId));
        OxygenHelperClient.savePersistentDataDelegated(this);       
    }

    @Override
    public String getName() {
        return "merchant_profiles";
    }

    @Override
    public String getModId() {
        return MerchantsMain.MODID;
    }

    @Override
    public String getPath() {
        return "world/merchants/profiles.dat";
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write((short) this.merchantProfiles.size(), bos);
        for (MerchantProfile profile : this.merchantProfiles.values())
            profile.write(bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException { 
        int amount = StreamUtils.readShort(bis);
        MerchantProfile profile;
        for (int i = 0; i < amount; i++) {
            profile = MerchantProfile.read(bis);
            this.merchantProfiles.put(profile.getId(), profile);
        }
    }

    public void reset() {
        this.merchantProfiles.clear();
    }
}
