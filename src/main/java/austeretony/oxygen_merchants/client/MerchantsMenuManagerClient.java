package austeretony.oxygen_merchants.client;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_merchants.client.gui.management.ManagementScreen;
import austeretony.oxygen_merchants.client.gui.merchant.MerchantScreen;
import austeretony.oxygen_merchants.common.EnumMerchantOperation;
import austeretony.oxygen_merchants.common.MerchantOffer;
import austeretony.oxygen_merchants.common.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.server.SPMerchantOperation;
import austeretony.oxygen_merchants.common.network.server.SPRequestMerchantProfileSync;

public class MerchantsMenuManagerClient {

    private MerchantsManagerClient manager;

    protected MerchantsMenuManagerClient(MerchantsManagerClient manager) {
        this.manager = manager;
    }

    //management menu

    public void openManagementMenuDelegated() {
        ClientReference.getMinecraft().addScheduledTask(()->ClientReference.displayGuiScreen(new ManagementScreen()));
    }

    public void profilesSynchronized() {
        ClientReference.delegateToClientThread(()->{
            if (isManagementMenuOpened())
                ((ManagementScreen) ClientReference.getCurrentScreen()).profilesSynchronized();;
        }); 
    }

    public void profileCreated(MerchantProfile profile) {
        this.manager.getMerchantProfilesContainer().addProfile(profile);
        this.manager.getMerchantProfilesContainer().setChanged(true);

        ClientReference.delegateToClientThread(()->{
            if (isManagementMenuOpened())
                ((ManagementScreen) ClientReference.getCurrentScreen()).profileCreated(profile);;
        }); 
    }

    public void profileUpdated(MerchantProfile profile) {
        this.manager.getMerchantProfilesContainer().removeProfile(profile.getId() - 1L);
        this.manager.getMerchantProfilesContainer().addProfile(profile);
        this.manager.getMerchantProfilesContainer().setChanged(true);

        ClientReference.delegateToClientThread(()->{
            if (isManagementMenuOpened())
                ((ManagementScreen) ClientReference.getCurrentScreen()).profileUpdated(profile);;
        }); 
    }

    public void profileRemoved(MerchantProfile profile) {
        this.manager.getMerchantProfilesContainer().removeProfile(profile.getId());
        this.manager.getMerchantProfilesContainer().setChanged(true);

        ClientReference.delegateToClientThread(()->{
            if (isManagementMenuOpened())
                ((ManagementScreen) ClientReference.getCurrentScreen()).profileRemoved(profile);;
        }); 
    }

    public static boolean isManagementMenuOpened() {
        return ClientReference.hasActiveGUI() && ClientReference.getCurrentScreen() instanceof ManagementScreen;
    }

    //merchant menu
    
    public void tryOpenMerchantMenu(long profileId) {
        if (this.manager.getMerchantProfilesContainer().getProfile(profileId) != null)
            this.openMerchantMenuDelegated(profileId);
        else
            OxygenMain.network().sendToServer(new SPRequestMerchantProfileSync(profileId));
    }

    public void openMerchantMenuDelegated(long profileId) {
        ClientReference.delegateToClientThread(()->ClientReference.displayGuiScreen(new MerchantScreen(profileId)));
    }

    public void addProfileOpenMerchantMenu(MerchantProfile merchantProfile) {
        MerchantsMain.LOGGER.info("Synchronized merchant profile: <{}>.", merchantProfile.getName());
        MerchantsManagerClient.instance().getMerchantProfilesManager().profileCreated(merchantProfile);
        this.openMerchantMenuDelegated(merchantProfile.getId());
    }

    public void bought(MerchantOffer offer, long balance) {
        ClientReference.delegateToClientThread(()->{
            if (isMerchantMenuOpened())
                ((MerchantScreen) ClientReference.getCurrentScreen()).bought(offer, balance);;
        }); 
    }

    public void sold(MerchantOffer offer, long balance) {
        ClientReference.delegateToClientThread(()->{
            if (isMerchantMenuOpened())
                ((MerchantScreen) ClientReference.getCurrentScreen()).sold(offer, balance);;
        }); 
    }

    public static boolean isMerchantMenuOpened() {
        return ClientReference.hasActiveGUI() && ClientReference.getCurrentScreen() instanceof MerchantScreen;
    }

    public void performBuySynced(long profileId, long offerId) {
        OxygenMain.network().sendToServer(new SPMerchantOperation(EnumMerchantOperation.BUY, profileId, offerId));
    }

    public void performSellingSynced(long profileId, long offerId) {
        OxygenMain.network().sendToServer(new SPMerchantOperation(EnumMerchantOperation.SELLING, profileId, offerId));
    }
}
