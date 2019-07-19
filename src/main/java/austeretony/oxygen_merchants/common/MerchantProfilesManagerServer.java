package austeretony.oxygen_merchants.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen.common.api.IPersistentData;
import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.util.StreamUtils;
import austeretony.oxygen_merchants.common.config.MerchantsConfig;
import austeretony.oxygen_merchants.common.main.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import net.minecraft.entity.player.EntityPlayerMP;

public class MerchantProfilesManagerServer implements IPersistentData {

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

    public void createProfile(EntityPlayerMP playerMP, long profileId, String name) {
        if (CommonReference.isOpped(playerMP) 
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.getBooleanValue()) {
            MerchantProfile profile = new MerchantProfile();
            profile.setId(profileId);
            profile.setName(name);
            profile.setUseCurrency(true);
            this.merchantProfiles.put(profileId, profile);
            OxygenHelperServer.savePersistentDataDelegated(this);
        }
    }

    public void editProfile(EntityPlayerMP playerMP, long oldProfileId, MerchantProfile profile) {
        if (CommonReference.isOpped(playerMP)
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.getBooleanValue()) {
            this.merchantProfiles.remove(oldProfileId);
            this.merchantProfiles.put(profile.getId(), profile);
            MerchantsManagerServer.instance().getBoundEntitiesManager().merchantProfileEdited(oldProfileId);

            OxygenHelperServer.savePersistentDataDelegated(this);
        }
    }   

    public void removeProfile(EntityPlayerMP playerMP, long profileId) {
        if (CommonReference.isOpped(playerMP)
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.getBooleanValue()) {
            if (this.profileExist(profileId)) {
                this.merchantProfiles.remove(profileId);
                MerchantsManagerServer.instance().getBoundEntitiesManager().merchantProfileRemoved(profileId);

                OxygenHelperServer.savePersistentDataDelegated(this);
            }
        }
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
