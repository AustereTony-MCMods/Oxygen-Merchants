package austeretony.oxygen_merchants.server;

import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.server.api.OxygenHelperServer;
import net.minecraft.entity.player.EntityPlayerMP;

public class MerchantsManagerServer {

    private static MerchantsManagerServer instance;

    private final MerchantProfilesContainerServer profilesContainer = new MerchantProfilesContainerServer();

    private final MerchantProfilesManagerServer profilesManager;

    private final PlayersManagerServer playersManager;

    private MerchantsManagerServer() {
        this.profilesManager = new MerchantProfilesManagerServer(this);
        this.playersManager = new PlayersManagerServer(this);
    }

    private void registerPersistentData() {
        OxygenHelperServer.registerPersistentData(this.profilesContainer);
    }

    private void scheduleRepeatableProcesses() {
        OxygenHelperServer.getSchedulerExecutorService().scheduleAtFixedRate(()->this.playersManager.process(), 500L, 500L, TimeUnit.MILLISECONDS);
    }

    public static void create() {
        if (instance == null) {
            instance = new MerchantsManagerServer();
            instance.registerPersistentData();
            instance.scheduleRepeatableProcesses();
        }
    }

    public static MerchantsManagerServer instance() {
        return instance;
    }

    public MerchantProfilesContainerServer getMerchantProfilesContainer() {
        return this.profilesContainer;
    }

    public MerchantProfilesManagerServer getMerchantProfilesManager() {
        return this.profilesManager;
    }

    public PlayersManagerServer getPlayersManager() {
        return this.playersManager;
    }

    public void onPlayerUnloaded(EntityPlayerMP playerMP) {
        this.playersManager.onPlayerUnloaded(playerMP);
    }

    public void worldLoaded() {
        OxygenHelperServer.loadPersistentDataAsync(this.profilesContainer);
    }
}
