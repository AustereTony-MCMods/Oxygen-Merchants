package austeretony.oxygen_merchants.client;

import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen_merchants.client.gui.merchant.MerchantMenuGUIScreen;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.main.OperationsProcessor;
import austeretony.oxygen_merchants.common.network.server.SPMerchantOperation;
import austeretony.oxygen_merchants.common.network.server.SPOpenMerchantMenu;

public class MerchantsManagerClient {

    private static MerchantsManagerClient instance;

    private final MerchantProfilesManagerClient profilesManager;

    private final BoundEntitiesManagerClient entitiesManager;      

    private long lastRequestedProfileId;

    private MerchantsManagerClient() {        
        this.profilesManager = new MerchantProfilesManagerClient();
        this.entitiesManager = new BoundEntitiesManagerClient();
    }

    public static void create() {
        if (instance == null) 
            instance = new MerchantsManagerClient();
    }

    public static MerchantsManagerClient instance() {
        return instance;
    }

    public MerchantProfilesManagerClient getMerchantProfilesManager() {
        return this.profilesManager;
    }

    public BoundEntitiesManagerClient getBoundEntitiesManager() {
        return this.entitiesManager;
    }

    public void openMerchantMenuSynced(int entityId, long profileId) {
        this.lastRequestedProfileId = profileId;
        MerchantsMain.network().sendToServer(new SPOpenMerchantMenu(entityId, profileId));
    }

    public void openMerchantMenuDelegated(long profileId) {
        ClientReference.getMinecraft().addScheduledTask(new Runnable() {

            @Override
            public void run() {
                openMerchantMenu(profileId);
            }
        });
    }

    public void openLastRequestedMerchantMenuDelegated() {
        this.openMerchantMenuDelegated(this.lastRequestedProfileId);
    }

    public void openMerchantMenu(long profileId) {
        ClientReference.displayGuiScreen(new MerchantMenuGUIScreen(profileId));
    }

    public void openMerchantMenuManagement(long profileId) {
        ClientReference.displayGuiScreen(new MerchantMenuGUIScreen(profileId));
    }

    public void performBuySynced(long profileId, long offerId) {
        MerchantsMain.network().sendToServer(new SPMerchantOperation(OperationsProcessor.EnumOperation.BUY, profileId, offerId));
    }

    public void performSellingSynced(long profileId, long offerId) {
        MerchantsMain.network().sendToServer(new SPMerchantOperation(OperationsProcessor.EnumOperation.SELLING, profileId, offerId));
    }

    public void updateMerchantMenu(OperationsProcessor.EnumOperation operation) {
        if (ClientReference.hasActiveGUI() && ClientReference.getCurrentScreen() instanceof MerchantMenuGUIScreen) {
            switch (operation) {
            case BUY:
                ((MerchantMenuGUIScreen) ClientReference.getCurrentScreen()).getBuySection().bought();
                break;
            case SELLING:
                ((MerchantMenuGUIScreen) ClientReference.getCurrentScreen()).getSellingSection().sold();
                break;
            }
        }
    }

    public void reset() {
        this.profilesManager.reset();
        this.entitiesManager.reset();
    }
}
