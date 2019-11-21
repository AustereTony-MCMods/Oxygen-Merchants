package austeretony.oxygen_merchants.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_merchants.common.EnumAction;
import austeretony.oxygen_merchants.common.MerchantProfile;
import austeretony.oxygen_merchants.common.config.MerchantsConfig;
import austeretony.oxygen_merchants.common.main.EnumMerchantsStatusMessage;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.client.CPProfileAction;
import net.minecraft.entity.player.EntityPlayerMP;

public class MerchantProfilesManagerServer {  

    private final MerchantsManagerServer manager;

    protected MerchantProfilesManagerServer(MerchantsManagerServer manager) {
        this.manager = manager;
    }

    public void informPlayer(EntityPlayerMP playerMP, EnumMerchantsStatusMessage status) {
        OxygenHelperServer.sendStatusMessage(playerMP, MerchantsMain.MERCHANTS_MOD_INDEX, status.ordinal());
    }

    public void createProfile(EntityPlayerMP playerMP, String name) {
        if (CommonReference.isPlayerOpped(playerMP) 
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.getBooleanValue()) {
            MerchantProfile profile = new MerchantProfile();
            profile.setId(this.manager.getMerchantProfilesContainer().createId(System.currentTimeMillis()));
            profile.setName(name);
            profile.setUseCurrency(true);
            this.manager.getMerchantProfilesContainer().addProfile(profile);

            OxygenMain.network().sendTo(new CPProfileAction(EnumAction.CREATED, profile), playerMP); 
            this.informPlayer(playerMP, EnumMerchantsStatusMessage.PROFILE_CREATED);

            this.manager.getMerchantProfilesContainer().setChanged(true);
        }
    }

    public void editProfile(EntityPlayerMP playerMP, MerchantProfile profile) {
        if (CommonReference.isPlayerOpped(playerMP)
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.getBooleanValue()) {
            long oldId = profile.getId();
            this.manager.getMerchantProfilesContainer().removeProfile(oldId);
            profile.setId(this.manager.getMerchantProfilesContainer().createId(oldId));
            this.manager.getMerchantProfilesContainer().addProfile(profile);
            this.manager.getBoundEntitiesManager().merchantProfileEdited(oldId, profile.getId());

            OxygenMain.network().sendTo(new CPProfileAction(EnumAction.UPDATED, profile), playerMP); 
            this.informPlayer(playerMP, EnumMerchantsStatusMessage.PROFILE_UPDATED);

            this.manager.getMerchantProfilesContainer().setChanged(true);
        }
    }   

    public void removeProfile(EntityPlayerMP playerMP, long profileId) {
        if (CommonReference.isPlayerOpped(playerMP)
                && MerchantsConfig.ALLOW_INGAME_MANAGEMENT.getBooleanValue()) {
            if (this.manager.getMerchantProfilesContainer().profileExist(profileId)) {
                MerchantProfile profile = this.manager.getMerchantProfilesContainer().getProfile(profileId);
                this.manager.getMerchantProfilesContainer().removeProfile(profileId);
                this.manager.getBoundEntitiesManager().merchantProfileRemoved(profileId);

                OxygenMain.network().sendTo(new CPProfileAction(EnumAction.REMOVED, profile), playerMP); 
                this.informPlayer(playerMP, EnumMerchantsStatusMessage.ENTITY_REMOVED);

                this.manager.getMerchantProfilesContainer().setChanged(true);
            }
        }
    }
}
