package austeretony.oxygen_merchants.server;

import austeretony.oxygen_core.server.api.OxygenHelperServer;
import net.minecraft.entity.player.EntityPlayerMP;

public class MerchantsManagerServer {

    private static MerchantsManagerServer instance;

    private final MerchantProfilesContainerServer profilesContainer = new MerchantProfilesContainerServer();

    private final BoundEntitiesContainerServer entitiesContainer = new BoundEntitiesContainerServer();

    private final MerchantProfilesManagerServer profilesManager;

    private final BoundEntitiesManagerServer entitiesManager;

    private final PlayersManagerServer playersManager;

    private MerchantsManagerServer() {
        this.profilesManager = new MerchantProfilesManagerServer(this);
        this.entitiesManager = new BoundEntitiesManagerServer(this);
        this.playersManager = new PlayersManagerServer(this);
    }

    private void registerPersistentData() {
        OxygenHelperServer.registerPersistentData(this.profilesContainer);
        OxygenHelperServer.registerPersistentData(this.entitiesContainer);
    }

    public static void create() {
        if (instance == null) {
            instance = new MerchantsManagerServer();
            instance.registerPersistentData();
        }
    }

    public static MerchantsManagerServer instance() {
        return instance;
    }

    public MerchantProfilesContainerServer getMerchantProfilesContainer() {
        return this.profilesContainer;
    }

    public BoundEntitiesContainerServer getBoundEntitiesContainer() {
        return this.entitiesContainer;
    }

    public MerchantProfilesManagerServer getMerchantProfilesManager() {
        return this.profilesManager;
    }

    public BoundEntitiesManagerServer getBoundEntitiesManager() {
        return this.entitiesManager;
    }

    public PlayersManagerServer getPlayersManager() {
        return this.playersManager;
    }

    public void onPlayerLoaded(EntityPlayerMP playerMP) {
        this.playersManager.onPlayerLoaded(playerMP);
    }

    public void onPlayerUnloaded(EntityPlayerMP playerMP) {
        this.playersManager.onPlayerUnloaded(playerMP);
    }

    public void worldLoaded() {
        OxygenHelperServer.loadPersistentDataAsync(this.profilesContainer);
        OxygenHelperServer.loadPersistentDataAsync(this.entitiesContainer);
    }
}
