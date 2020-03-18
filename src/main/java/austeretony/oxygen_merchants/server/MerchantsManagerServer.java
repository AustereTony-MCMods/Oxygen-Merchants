package austeretony.oxygen_merchants.server;

import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_merchants.common.main.EnumMerchantsStatusMessage;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
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

    private void scheduleRepeatableProcesses() {
        OxygenHelperServer.getSchedulerExecutorService().scheduleAtFixedRate(this.playersManager::process, 500L, 500L, TimeUnit.MILLISECONDS);
    }

    public static void create() {
        if (instance == null) {
            instance = new MerchantsManagerServer();
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

    public void playerUnloaded(EntityPlayerMP playerMP) {
        this.playersManager.playerUnloaded(playerMP);
    }

    public void worldLoaded() {
        this.profilesContainer.loadAsync();
    }

    public void sendStatusMessage(EntityPlayerMP playerMP, EnumMerchantsStatusMessage status) {
        OxygenHelperServer.sendStatusMessage(playerMP, MerchantsMain.MERCHANTS_MOD_INDEX, status.ordinal());
    }
}
