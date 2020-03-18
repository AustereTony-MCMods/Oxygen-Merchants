package austeretony.oxygen_merchants.client;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_merchants.client.gui.merchant.MerchantScreen;
import austeretony.oxygen_merchants.common.EnumMerchantOperation;
import austeretony.oxygen_merchants.common.merchant.MerchantOffer;
import austeretony.oxygen_merchants.common.merchant.MerchantProfile;
import austeretony.oxygen_merchants.common.network.server.SPMerchantOperation;
import austeretony.oxygen_merchants.common.network.server.SPRequestMerchantProfileSync;

public class MerchantsMenuManagerClient {

    private MerchantsManagerClient manager;

    //cache

    private boolean debug;

    protected MerchantsMenuManagerClient(MerchantsManagerClient manager) {
        this.manager = manager;
    }

    public void tryOpenMerchantMenu(long profileId, boolean debug) {
        if (this.manager.getMerchantProfilesContainer().getProfile(profileId) != null)
            this.openMerchantMenuDelegated(profileId, debug);
        else {
            this.debug = debug;
            OxygenMain.network().sendToServer(new SPRequestMerchantProfileSync(profileId));
        }
    }

    public void openMerchantMenuDelegated(long profileId, boolean debug) {
        ClientReference.delegateToClientThread(()->ClientReference.displayGuiScreen(new MerchantScreen(profileId, debug)));
    }

    public void addProfileOpenMerchantMenu(MerchantProfile profile) {
        OxygenMain.LOGGER.info("[Merchants] Synchronized merchant profile: <{}>.", profile.getDisplayName());
        this.manager.getMerchantProfilesContainer().addProfile(profile);
        this.manager.getMerchantProfilesContainer().setChanged(true);
        this.openMerchantMenuDelegated(profile.getId(), this.debug);
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

    public void performBuySynced(long offerId) {
        OxygenMain.network().sendToServer(new SPMerchantOperation(EnumMerchantOperation.BUY, offerId));
    }

    public void performSellingSynced(long offerId) {
        OxygenMain.network().sendToServer(new SPMerchantOperation(EnumMerchantOperation.SELLING, offerId));
    }
}
