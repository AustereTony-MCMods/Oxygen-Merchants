package austeretony.oxygen_merchants.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.command.CommandOxygenClient;
import austeretony.oxygen_core.client.interaction.InteractionHelperClient;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.RequestsFilterHelper;
import austeretony.oxygen_core.server.command.CommandOxygenServer;
import austeretony.oxygen_merchants.client.EntitiesSyncHandlerClient;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.MerchantsStatusMessagesHandler;
import austeretony.oxygen_merchants.client.ProfilesSyncHandlerClient;
import austeretony.oxygen_merchants.client.command.MerchantsArgumentExecutorClient;
import austeretony.oxygen_merchants.client.event.MerchantsEventsClient;
import austeretony.oxygen_merchants.client.gui.overlay.MerchantInteractionOverlay;
import austeretony.oxygen_merchants.client.interaction.MerchantInteraction;
import austeretony.oxygen_merchants.common.config.MerchantsConfig;
import austeretony.oxygen_merchants.common.network.client.CPEntityAction;
import austeretony.oxygen_merchants.common.network.client.CPMerchantAction;
import austeretony.oxygen_merchants.common.network.client.CPOpenManagementMenu;
import austeretony.oxygen_merchants.common.network.client.CPOpenMerchantMenu;
import austeretony.oxygen_merchants.common.network.client.CPProfileAction;
import austeretony.oxygen_merchants.common.network.client.CPSyncDataOpenMenu;
import austeretony.oxygen_merchants.common.network.server.SPCreateBond;
import austeretony.oxygen_merchants.common.network.server.SPCreateProfile;
import austeretony.oxygen_merchants.common.network.server.SPEditBond;
import austeretony.oxygen_merchants.common.network.server.SPMerchantOperation;
import austeretony.oxygen_merchants.common.network.server.SPOpenMerchantMenu;
import austeretony.oxygen_merchants.common.network.server.SPRemoveBond;
import austeretony.oxygen_merchants.common.network.server.SPRemoveProfile;
import austeretony.oxygen_merchants.common.network.server.SPUpdateMerchantProfile;
import austeretony.oxygen_merchants.common.network.server.SPVisitEntity;
import austeretony.oxygen_merchants.server.EntitiesSyncHandlerServer;
import austeretony.oxygen_merchants.server.MerchantsManagerServer;
import austeretony.oxygen_merchants.server.ProfilesSyncHandlerServer;
import austeretony.oxygen_merchants.server.command.MerchantsArgumentExecutorServer;
import austeretony.oxygen_merchants.server.event.MerchantsEventsServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = MerchantsMain.MODID, 
        name = MerchantsMain.NAME, 
        version = MerchantsMain.VERSION,
        dependencies = "required-after:oxygen_core@[0.9.5,);",
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = MerchantsMain.VERSIONS_FORGE_URL)
public class MerchantsMain {

    public static final String 
    MODID = "oxygen_merchants",
    NAME = "Oxygen: Merchants",
    VERSION = "0.9.2",
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Merchants/info/mod_versions_forge.json";

    public static final int 
    MERCHANTS_MOD_INDEX = 4,

    MANAGEMENT_MENU_SCREEN_ID = 40,
    MERCHANT_MENU_SCREEN_ID = 41,

    MERCHANT_PROFILES_DATA_ID = 40,
    ENTITIES_DATA_ID = 41,

    PROFILE_MANAGEMENT_REQUEST_ID = 45,
    ENTITY_MANAGEMENT_REQUEST_ID = 46,
    MERCHANT_OPERATION_REQUEST_ID = 47;

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperCommon.registerConfig(new MerchantsConfig());
        if (event.getSide() == Side.CLIENT)
            CommandOxygenClient.registerArgumentExecutor(new MerchantsArgumentExecutorClient("merchants", true));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        MerchantsManagerServer.create();
        CommonReference.registerEvent(new MerchantsEventsServer());
        CommandOxygenServer.registerArgumentExecutor(new MerchantsArgumentExecutorServer("merchants", true));
        OxygenHelperServer.registerDataSyncHandler(new ProfilesSyncHandlerServer());
        OxygenHelperServer.registerDataSyncHandler(new EntitiesSyncHandlerServer());
        RequestsFilterHelper.registerNetworkRequest(PROFILE_MANAGEMENT_REQUEST_ID, 2);
        RequestsFilterHelper.registerNetworkRequest(ENTITY_MANAGEMENT_REQUEST_ID, 2);
        RequestsFilterHelper.registerNetworkRequest(MERCHANT_OPERATION_REQUEST_ID, 1);
        if (event.getSide() == Side.CLIENT) {
            MerchantsManagerClient.create();
            CommonReference.registerEvent(new MerchantsEventsClient());
            InteractionHelperClient.registerInteraction(new MerchantInteraction());
            OxygenGUIHelper.registerScreenId(MANAGEMENT_MENU_SCREEN_ID);
            OxygenGUIHelper.registerScreenId(MERCHANT_MENU_SCREEN_ID);
            OxygenGUIHelper.registerOverlay(new MerchantInteractionOverlay());
            OxygenHelperClient.registerStatusMessagesHandler(new MerchantsStatusMessagesHandler());
            OxygenHelperClient.registerDataSyncHandler(new ProfilesSyncHandlerClient());
            OxygenHelperClient.registerDataSyncHandler(new EntitiesSyncHandlerClient());
        }
    }

    private void initNetwork() {
        OxygenMain.network().registerPacket(CPOpenManagementMenu.class);
        OxygenMain.network().registerPacket(CPSyncDataOpenMenu.class);
        OxygenMain.network().registerPacket(CPOpenMerchantMenu.class);
        OxygenMain.network().registerPacket(CPProfileAction.class);
        OxygenMain.network().registerPacket(CPEntityAction.class);
        OxygenMain.network().registerPacket(CPMerchantAction.class);

        OxygenMain.network().registerPacket(SPCreateProfile.class);
        OxygenMain.network().registerPacket(SPRemoveProfile.class);
        OxygenMain.network().registerPacket(SPUpdateMerchantProfile.class);
        OxygenMain.network().registerPacket(SPMerchantOperation.class);
        OxygenMain.network().registerPacket(SPCreateBond.class);
        OxygenMain.network().registerPacket(SPEditBond.class);
        OxygenMain.network().registerPacket(SPVisitEntity.class);
        OxygenMain.network().registerPacket(SPRemoveBond.class);
        OxygenMain.network().registerPacket(SPOpenMerchantMenu.class);
    }
}
