package austeretony.oxygen_merchants.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;

public class MerchantsManagerClient {

    private static MerchantsManagerClient instance;

    private final MerchantProfilesContainerClient profilesContainer = new MerchantProfilesContainerClient();

    private final MerchantsMenuManagerClient menuManager;

    private MerchantsManagerClient() {        
        this.menuManager = new MerchantsMenuManagerClient(this);
    }

    private void registerPersistentData() {
        OxygenHelperClient.registerPersistentData(this.profilesContainer);
    }

    public static void create() {
        if (instance == null) {
            instance = new MerchantsManagerClient();
            instance.registerPersistentData();
        }
    }

    public static MerchantsManagerClient instance() {
        return instance;
    }

    public MerchantProfilesContainerClient getMerchantProfilesContainer() {
        return this.profilesContainer;
    }

    public MerchantsMenuManagerClient getMenuManager() {
        return this.menuManager;
    }

    public void worldLoaded() {
        OxygenHelperClient.loadPersistentDataAsync(this.profilesContainer);
    }
}
