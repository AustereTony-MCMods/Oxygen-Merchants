package austeretony.oxygen_merchants.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.EnumOxygenStatusMessage;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_merchants.common.EnumAction;
import austeretony.oxygen_merchants.common.MerchantProfile;
import austeretony.oxygen_merchants.common.config.MerchantsConfig;
import austeretony.oxygen_merchants.common.main.EnumMerchantsStatusMessage;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.client.CPProfileAction;
import austeretony.oxygen_merchants.common.network.client.CPSyncProfileOpenMenu;
import net.minecraft.entity.player.EntityPlayerMP;

public class MerchantProfilesManagerServer {  

    private final MerchantsManagerServer manager;

    protected MerchantProfilesManagerServer(MerchantsManagerServer manager) {
        this.manager = manager;
    }

    public void informPlayer(EntityPlayerMP playerMP, EnumMerchantsStatusMessage status) {
        OxygenHelperServer.sendStatusMessage(playerMP, MerchantsMain.MERCHANTS_MOD_INDEX, status.ordinal());
    }

    public void syncProfile(EntityPlayerMP playerMP, long profileId) {
        if (OxygenHelperServer.checkTimeOut(CommonReference.getPersistentUUID(playerMP), MerchantsMain.MERCHANT_MENU_TIMEOUT_ID) || CommonReference.isPlayerOpped(playerMP)) {
            MerchantProfile profile = this.manager.getMerchantProfilesContainer().getProfile(profileId);
            if (profile != null)
                OxygenMain.network().sendTo(new CPSyncProfileOpenMenu(profile), playerMP); 
        } else
            OxygenHelperServer.sendStatusMessage(playerMP, OxygenMain.OXYGEN_CORE_MOD_INDEX, EnumOxygenStatusMessage.ACTION_TIMEOUT.ordinal());
    }

    public void createProfile(EntityPlayerMP playerMP, String name) {
        if (CommonReference.isPlayerOpped(playerMP) 
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.asBoolean()) {
            name = name.trim();
            if (name.length() > MerchantProfile.MAX_PROFILE_NAME_LENGTH)
                name = name.substring(0, MerchantProfile.MAX_PROFILE_NAME_LENGTH);
            if (name.isEmpty()) return;
            MerchantProfile profile = new MerchantProfile();
            profile.setId(this.manager.getMerchantProfilesContainer().createId(System.currentTimeMillis()));
            profile.setPersistentId(profile.getId());
            profile.setName(name);
            profile.setCurrencyIndex(OxygenMain.COMMON_CURRENCY_INDEX);
            this.manager.getMerchantProfilesContainer().addProfile(profile);

            OxygenMain.network().sendTo(new CPProfileAction(EnumAction.CREATED, profile), playerMP); 
            this.informPlayer(playerMP, EnumMerchantsStatusMessage.PROFILE_CREATED);

            this.manager.getMerchantProfilesContainer().setChanged(true);
        }
    }

    public void editProfile(EntityPlayerMP playerMP, MerchantProfile profile) {
        if (CommonReference.isPlayerOpped(playerMP)
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.asBoolean()) {
            String name = profile.getName();
            name = name.trim();
            if (name.length() > MerchantProfile.MAX_PROFILE_NAME_LENGTH)
                name = name.substring(0, MerchantProfile.MAX_PROFILE_NAME_LENGTH);
            if (name.isEmpty()) return;
            long oldId = profile.getId();
            this.manager.getMerchantProfilesContainer().removeProfile(oldId);
            profile.setId(this.manager.getMerchantProfilesContainer().createId(oldId));
            this.manager.getMerchantProfilesContainer().addProfile(profile);

            OxygenMain.network().sendTo(new CPProfileAction(EnumAction.UPDATED, profile), playerMP); 
            this.informPlayer(playerMP, EnumMerchantsStatusMessage.PROFILE_UPDATED);

            this.manager.getMerchantProfilesContainer().setChanged(true);
        }
    }   

    public void removeProfile(EntityPlayerMP playerMP, long profileId) {
        if (CommonReference.isPlayerOpped(playerMP)
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.asBoolean()) {
            MerchantProfile profile = this.manager.getMerchantProfilesContainer().getProfile(profileId);
            if (profile != null) {
                this.manager.getMerchantProfilesContainer().removeProfile(profileId);

                OxygenMain.network().sendTo(new CPProfileAction(EnumAction.REMOVED, profile), playerMP); 
                this.informPlayer(playerMP, EnumMerchantsStatusMessage.PROFILE_REMOVED);

                this.manager.getMerchantProfilesContainer().setChanged(true);
            }
        }
    }
}
