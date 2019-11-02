package austeretony.oxygen_merchants.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;

public class MerchantsManagerClient {

    private static MerchantsManagerClient instance;

    private final MerchantProfilesContainerClient profilesContainer = new MerchantProfilesContainerClient();

    private final BoundEntitiesContainerClient entitiesContainer = new BoundEntitiesContainerClient();

    private final MerchantProfilesManagerClient profilesManager;

    private final BoundEntitiesManagerClient entitiesManager;    

    private final MerchantsMenuManagerClient menuManager;

    private MerchantsManagerClient() {        
        this.profilesManager = new MerchantProfilesManagerClient(this);
        this.entitiesManager = new BoundEntitiesManagerClient(this);
        this.menuManager = new MerchantsMenuManagerClient(this);
    }

    private void registerPersistentData() {
        OxygenHelperClient.registerPersistentData(this.profilesContainer);
        OxygenHelperClient.registerPersistentData(this.entitiesContainer);
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

    public BoundEntitiesContainerClient getBoundEntitiesContainer() {
        return this.entitiesContainer;
    }

    public MerchantProfilesManagerClient getMerchantProfilesManager() {
        return this.profilesManager;
    }

    public BoundEntitiesManagerClient getBoundEntitiesManager() {
        return this.entitiesManager;
    }

    public MerchantsMenuManagerClient getMenuManager() {
        return this.menuManager;
    }

    public void worldLoaded() {
        OxygenHelperClient.loadPersistentDataAsync(this.profilesContainer);
        OxygenHelperClient.loadPersistentDataAsync(this.entitiesContainer);
    }
}
