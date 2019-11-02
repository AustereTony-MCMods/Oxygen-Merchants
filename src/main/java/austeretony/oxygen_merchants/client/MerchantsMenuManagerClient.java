package austeretony.oxygen_merchants.client;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_merchants.client.gui.management.ManagementMenuGUIScreen;
import austeretony.oxygen_merchants.client.gui.merchant.MerchantMenuGUIScreen;
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import austeretony.oxygen_merchants.common.EnumMerchantOperation;
import austeretony.oxygen_merchants.common.MerchantOffer;
import austeretony.oxygen_merchants.common.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.server.SPMerchantOperation;
import austeretony.oxygen_merchants.common.network.server.SPOpenMerchantMenu;

public class MerchantsMenuManagerClient {

    private MerchantsManagerClient manager;

    protected MerchantsMenuManagerClient(MerchantsManagerClient manager) {
        this.manager = manager;
    }

    //management menu

    public void openManagementMenuDelegated() {
        ClientReference.getMinecraft().addScheduledTask(()->ClientReference.displayGuiScreen(new ManagementMenuGUIScreen()));
    }

    public void profilesSynchronized() {
        ClientReference.delegateToClientThread(()->{
            if (isManagementMenuOpened())
                ((ManagementMenuGUIScreen) ClientReference.getCurrentScreen()).profilesSynchronized();;
        }); 
    }

    public void entitiesSynchronized() {
        ClientReference.delegateToClientThread(()->{
            if (isManagementMenuOpened())
                ((ManagementMenuGUIScreen) ClientReference.getCurrentScreen()).entitiesSynchronized();;
        }); 
    }

    public void profileCreated(MerchantProfile profile) {
        this.manager.getMerchantProfilesContainer().addProfile(profile);
        this.manager.getMerchantProfilesContainer().setChanged(true);

        ClientReference.delegateToClientThread(()->{
            if (isManagementMenuOpened())
                ((ManagementMenuGUIScreen) ClientReference.getCurrentScreen()).profileCreated(profile);;
        }); 
    }

    public void profileUpdated(MerchantProfile profile) {
        this.manager.getMerchantProfilesContainer().removeProfile(profile.getId() - 1L);
        this.manager.getMerchantProfilesContainer().addProfile(profile);
        this.manager.getMerchantProfilesContainer().setChanged(true);

        ClientReference.delegateToClientThread(()->{
            if (isManagementMenuOpened())
                ((ManagementMenuGUIScreen) ClientReference.getCurrentScreen()).profileUpdated(profile);;
        }); 
    }

    public void profileRemoved(MerchantProfile profile) {
        this.manager.getMerchantProfilesContainer().removeProfile(profile.getId());
        this.manager.getMerchantProfilesContainer().setChanged(true);

        ClientReference.delegateToClientThread(()->{
            if (isManagementMenuOpened())
                ((ManagementMenuGUIScreen) ClientReference.getCurrentScreen()).profileRemoved(profile);;
        }); 
    }

    public void entityCreated(BoundEntityEntry entry) {
        this.manager.getBoundEntitiesContainer().addEntry(entry);
        this.manager.getBoundEntitiesContainer().setChanged(true);

        ClientReference.delegateToClientThread(()->{
            if (isManagementMenuOpened())
                ((ManagementMenuGUIScreen) ClientReference.getCurrentScreen()).entityCreated(entry);;
        }); 
    }

    public void entityUpdated(BoundEntityEntry entry) {
        this.manager.getBoundEntitiesContainer().removeEntry(entry.getId() - 1L);
        this.manager.getBoundEntitiesContainer().addEntry(entry);
        this.manager.getBoundEntitiesContainer().setChanged(true);

        ClientReference.delegateToClientThread(()->{
            if (isManagementMenuOpened())
                ((ManagementMenuGUIScreen) ClientReference.getCurrentScreen()).entityUpdated(entry);;
        }); 
    }

    public void entityRemoved(BoundEntityEntry entry) {
        this.manager.getBoundEntitiesContainer().removeEntry(entry.getId());
        this.manager.getBoundEntitiesContainer().setChanged(true);

        ClientReference.delegateToClientThread(()->{
            if (isManagementMenuOpened())
                ((ManagementMenuGUIScreen) ClientReference.getCurrentScreen()).entityRemoved(entry);;
        }); 
    }

    public static boolean isManagementMenuOpened() {
        return ClientReference.hasActiveGUI() && ClientReference.getCurrentScreen() instanceof ManagementMenuGUIScreen;
    }

    //merchant menu

    public void openMerchantMenuSynced(int entityId, long profileId) {
        OxygenMain.network().sendToServer(new SPOpenMerchantMenu(entityId, profileId));
    }

    public void openMerchantMenuDelegated(long profileId) {
        ClientReference.getMinecraft().addScheduledTask(()->ClientReference.displayGuiScreen(new MerchantMenuGUIScreen(profileId)));
    }

    public void updateDataOpenMerchantMenu(BoundEntityEntry entry, MerchantProfile merchantProfile) {
        MerchantsMain.LOGGER.info("Synchronized merchant profile: <{}>.", merchantProfile.getName());
        MerchantsManagerClient.instance().getBoundEntitiesManager().entryCreated(entry);
        MerchantsManagerClient.instance().getMerchantProfilesManager().profileCreated(merchantProfile);
        this.openMerchantMenuDelegated(merchantProfile.getId());
    }

    public void bought(MerchantOffer offer, long balance) {
        ClientReference.delegateToClientThread(()->{
            if (isMerchantMenuOpened())
                ((MerchantMenuGUIScreen) ClientReference.getCurrentScreen()).bought(offer, balance);;
        }); 
    }

    public void sold(MerchantOffer offer, long balance) {
        ClientReference.delegateToClientThread(()->{
            if (isMerchantMenuOpened())
                ((MerchantMenuGUIScreen) ClientReference.getCurrentScreen()).sold(offer, balance);;
        }); 
    }

    public static boolean isMerchantMenuOpened() {
        return ClientReference.hasActiveGUI() && ClientReference.getCurrentScreen() instanceof MerchantMenuGUIScreen;
    }

    public void performBuySynced(long profileId, long offerId) {
        OxygenMain.network().sendToServer(new SPMerchantOperation(EnumMerchantOperation.BUY, profileId, offerId));
    }

    public void performSellingSynced(long profileId, long offerId) {
        OxygenMain.network().sendToServer(new SPMerchantOperation(EnumMerchantOperation.SELLING, profileId, offerId));
    }
}
