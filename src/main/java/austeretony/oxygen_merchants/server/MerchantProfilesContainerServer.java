package austeretony.oxygen_merchants.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.persistent.AbstractPersistentData;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_merchants.common.MerchantProfile;
import austeretony.oxygen_merchants.common.config.MerchantsConfig;

public class MerchantProfilesContainerServer extends AbstractPersistentData {

    private final Map<Long, MerchantProfile> profiles = new ConcurrentHashMap<>();

    public int getProfilesAmount() {
        return this.profiles.size();
    }

    public Set<Long> getProfilesIds() {
        return this.profiles.keySet();
    }

    public Collection<MerchantProfile> getProfiles() {
        return this.profiles.values();
    }

    public boolean profileExist(long profileId) {
        return this.profiles.containsKey(profileId);
    }

    public MerchantProfile getProfile(long profileId) {
        return this.profiles.get(profileId);
    }

    public void addProfile(MerchantProfile profile) {
        this.profiles.put(profile.getId(), profile);
    }

    public void removeProfile(long profileId) {
        this.profiles.remove(profileId);
    }

    public long createId(long seed) {
        while (this.profiles.containsKey(seed))
            seed++;
        return seed;
    }

    @Override
    public String getDisplayName() {
        return "merchants:merchant_profiles";
    }

    @Override
    public String getPath() {
        return CommonReference.getGameFolder() + "/config/oxygen/data/server/merchants/profiles.dat";
    }

    @Override
    public long getSaveDelayMinutes() {
        return MerchantsConfig.DATA_SAVE_DELAY_MINUTES.getIntValue();
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write((short) this.profiles.size(), bos);
        for (MerchantProfile profile : this.profiles.values())
            profile.write(bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        int amount = StreamUtils.readShort(bis);
        MerchantProfile profile;
        for (int i = 0; i < amount; i++) {
            profile = new MerchantProfile();
            profile.read(bis);
            this.profiles.put(profile.getId(), profile);
        }
    }

    @Override
    public void reset() {
        this.profiles.clear();
    }   
}
