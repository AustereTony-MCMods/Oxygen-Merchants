package austeretony.oxygen_merchants.server;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Nullable;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.main.EnumOxygenStatusMessage;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_merchants.common.config.MerchantsConfig;
import austeretony.oxygen_merchants.common.main.EnumMerchantsStatusMessage;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.merchant.MerchantOffer;
import austeretony.oxygen_merchants.common.merchant.MerchantProfile;
import austeretony.oxygen_merchants.common.network.client.CPSyncProfileOpenMenu;
import austeretony.oxygen_merchants.common.network.client.CPTryOpenMerchantMenu;
import net.minecraft.entity.player.EntityPlayerMP;

public class MerchantProfilesManagerServer {  

    private final MerchantsManagerServer manager;

    protected MerchantProfilesManagerServer(MerchantsManagerServer manager) {
        this.manager = manager;
    }

    public void reloadOffers(@Nullable EntityPlayerMP playerMP) {
        if (MerchantsConfig.ALLOW_MANAGEMENT_INGAME.asBoolean())
            OxygenHelperServer.addRoutineTask(()->this.reload(playerMP));
    }

    private void reload(@Nullable EntityPlayerMP playerMP) {
        OxygenMain.LOGGER.info("[Merchants] Reloading merchant profiles...");        
        Future future = this.manager.getMerchantProfilesContainer().loadAsync();
        try {
            future.get();
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
        }
        if (playerMP != null)
            this.manager.sendStatusMessage(playerMP, EnumMerchantsStatusMessage.PROFILES_RELOADED);
        OxygenMain.LOGGER.info("[Merchants] Merchant profiles reloaded.");
    }

    @Nullable
    public MerchantProfile openMerchantMenu(EntityPlayerMP targetMP, long persistentId, boolean debug) {
        MerchantProfile profile = this.manager.getMerchantProfilesContainer().getProfileByPersistentId(persistentId);
        if (profile != null) {
            OxygenHelperServer.resetTimeOut(CommonReference.getPersistentUUID(targetMP), MerchantsMain.MERCHANT_MENU_TIMEOUT_ID);
            this.manager.getPlayersManager().getOperationsProcessor(CommonReference.getPersistentUUID(targetMP)).setMerchantProfile(profile);
            OxygenMain.network().sendTo(new CPTryOpenMerchantMenu(profile.getId(), debug), targetMP);

            return profile;
        }
        return null;
    }

    public void syncProfile(EntityPlayerMP playerMP, long profileId) {
        if (OxygenHelperServer.checkTimeOut(CommonReference.getPersistentUUID(playerMP), MerchantsMain.MERCHANT_MENU_TIMEOUT_ID) || CommonReference.isPlayerOpped(playerMP)) {
            MerchantProfile profile = this.manager.getMerchantProfilesContainer().getProfile(profileId);
            if (profile != null)
                OxygenMain.network().sendTo(new CPSyncProfileOpenMenu(profile), playerMP); 
        } else
            OxygenHelperServer.sendStatusMessage(playerMP, OxygenMain.OXYGEN_CORE_MOD_INDEX, EnumOxygenStatusMessage.ACTION_TIMEOUT.ordinal());
    }

    public boolean createProfile(@Nullable EntityPlayerMP playerMP, long id, String fileName, String displayName) {
        if (MerchantsConfig.ALLOW_MANAGEMENT_INGAME.asBoolean()) {
            displayName = displayName.trim();
            if (displayName.length() > MerchantProfile.MAX_PROFILE_NAME_LENGTH)
                displayName = displayName.substring(0, MerchantProfile.MAX_PROFILE_NAME_LENGTH);
            if (displayName.isEmpty()) return false;

            MerchantProfile profile = new MerchantProfile(id, fileName + ".json", displayName);
            profile.setId(this.manager.getMerchantProfilesContainer().createId(id));
            profile.setCurrencyIndex(OxygenMain.COMMON_CURRENCY_INDEX);
            this.manager.getMerchantProfilesContainer().addProfile(profile);

            if (playerMP != null) 
                this.manager.sendStatusMessage(playerMP, EnumMerchantsStatusMessage.PROFILE_CREATED);

            return true;
        }
        return false;
    }

    private void updateProfileId(MerchantProfile profile) {
        long oldId = profile.getId();
        profile.setId(this.manager.getMerchantProfilesContainer().createId(oldId));
        this.manager.getMerchantProfilesContainer().removeProfile(oldId);
        this.manager.getMerchantProfilesContainer().addProfile(profile);
    }

    public boolean editProfileCurrency(@Nullable EntityPlayerMP playerMP, long persistentId, @Nullable ItemStackWrapper stackWrapper, int currencyIndex) {
        if (MerchantsConfig.ALLOW_MANAGEMENT_INGAME.asBoolean()) {
            MerchantProfile profile = this.manager.getMerchantProfilesContainer().getProfileByPersistentId(persistentId);
            if (profile != null) {
                if (stackWrapper != null)
                    profile.setCurrencyStack(stackWrapper);
                else
                    profile.setCurrencyIndex(currencyIndex);
                this.updateProfileId(profile);

                if (playerMP != null) 
                    this.manager.sendStatusMessage(playerMP, EnumMerchantsStatusMessage.PROFILE_UPDATED);

                return true;
            }
        }
        return false;
    }   

    public boolean editProfileName(@Nullable EntityPlayerMP playerMP, long persistentId, String displayName) {
        if (MerchantsConfig.ALLOW_MANAGEMENT_INGAME.asBoolean()) {
            displayName = displayName.trim();
            if (displayName.length() > MerchantProfile.MAX_PROFILE_NAME_LENGTH)
                displayName = displayName.substring(0, MerchantProfile.MAX_PROFILE_NAME_LENGTH);
            if (displayName.isEmpty()) return false;

            MerchantProfile profile = this.manager.getMerchantProfilesContainer().getProfileByPersistentId(persistentId);
            if (profile != null) {
                profile.setDisplayName(displayName);
                this.updateProfileId(profile);

                if (playerMP != null) 
                    this.manager.sendStatusMessage(playerMP, EnumMerchantsStatusMessage.PROFILE_UPDATED);

                return true;
            }
        }
        return false;
    } 

    public boolean addMerchantOffer(@Nullable EntityPlayerMP playerMP, long persistentId, MerchantOffer offer) {
        if (MerchantsConfig.ALLOW_MANAGEMENT_INGAME.asBoolean()) {
            MerchantProfile profile = this.manager.getMerchantProfilesContainer().getProfileByPersistentId(persistentId);
            if (profile != null) {
                profile.addOffer(offer);
                this.updateProfileId(profile);

                if (playerMP != null) 
                    this.manager.sendStatusMessage(playerMP, EnumMerchantsStatusMessage.PROFILE_UPDATED);

                return true;
            }
        }
        return false;
    } 

    public boolean removeMerchantOffer(@Nullable EntityPlayerMP playerMP, long persistentId, long offerId) {
        if (MerchantsConfig.ALLOW_MANAGEMENT_INGAME.asBoolean()) {
            MerchantProfile profile = this.manager.getMerchantProfilesContainer().getProfileByPersistentId(persistentId);
            if (profile != null) {
                profile.removeOffer(offerId);
                this.updateProfileId(profile);

                if (playerMP != null) 
                    this.manager.sendStatusMessage(playerMP, EnumMerchantsStatusMessage.PROFILE_UPDATED);

                return true;
            }
        }
        return false;
    } 

    public boolean saveProfile(@Nullable EntityPlayerMP playerMP, long persistentId) {
        if (MerchantsConfig.ALLOW_MANAGEMENT_INGAME.asBoolean()) {
            MerchantProfile profile = this.manager.getMerchantProfilesContainer().getProfileByPersistentId(persistentId);
            if (profile != null) {
                this.manager.getMerchantProfilesContainer().saveProfileAsync(persistentId);

                if (playerMP != null) 
                    this.manager.sendStatusMessage(playerMP, EnumMerchantsStatusMessage.PROFILE_SAVED);

                return true;
            }
        }
        return false;
    }

    public boolean removeProfile(@Nullable EntityPlayerMP playerMP, long persistentId) {
        if (MerchantsConfig.ALLOW_MANAGEMENT_INGAME.asBoolean()) {
            MerchantProfile profile = this.manager.getMerchantProfilesContainer().getProfileByPersistentId(persistentId);
            if (profile != null) {
                this.manager.getMerchantProfilesContainer().removeProfileFileAsync(persistentId);
                this.manager.getMerchantProfilesContainer().removeProfile(persistentId);

                if (playerMP != null) 
                    this.manager.sendStatusMessage(playerMP, EnumMerchantsStatusMessage.PROFILE_REMOVED);

                return true;
            }
        }
        return false;
    }
}
