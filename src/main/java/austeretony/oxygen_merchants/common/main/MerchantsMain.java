package austeretony.oxygen_merchants.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.oxygen.client.api.OxygenGUIHelper;
import austeretony.oxygen.client.command.CommandOxygenClient;
import austeretony.oxygen.client.interaction.InteractionHelperClient;
import austeretony.oxygen.client.sync.gui.api.ComplexGUIHandlerClient;
import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.api.network.OxygenNetwork;
import austeretony.oxygen.common.command.CommandOxygenServer;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.sync.gui.api.ComplexGUIHandlerServer;
import austeretony.oxygen.common.update.UpdateAdaptersManager;
import austeretony.oxygen_merchants.client.ManagementMenuHandlerClient;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.command.MerchantsArgumentExecutorClient;
import austeretony.oxygen_merchants.client.event.MerchantsEventsClient;
import austeretony.oxygen_merchants.client.gui.overlay.MerchantInteractionOverlay;
import austeretony.oxygen_merchants.client.interaction.MerchantInteraction;
import austeretony.oxygen_merchants.common.ManagementMenuHandlerServer;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import austeretony.oxygen_merchants.common.RunMerchantOperations;
import austeretony.oxygen_merchants.common.command.MerchantsArgumentExecutorServer;
import austeretony.oxygen_merchants.common.config.MerchantsConfig;
import austeretony.oxygen_merchants.common.event.MerchantsEventsServer;
import austeretony.oxygen_merchants.common.network.client.CPMerchantsCommand;
import austeretony.oxygen_merchants.common.network.client.CPSyncDataOpenMenu;
import austeretony.oxygen_merchants.common.network.client.CPSyncEntityEntries;
import austeretony.oxygen_merchants.common.network.client.CPSyncMerchantProfiles;
import austeretony.oxygen_merchants.common.network.client.CPSyncValidEntitiesIds;
import austeretony.oxygen_merchants.common.network.client.CPUpdateMerchantMenu;
import austeretony.oxygen_merchants.common.network.server.SPCreateBond;
import austeretony.oxygen_merchants.common.network.server.SPCreateProfile;
import austeretony.oxygen_merchants.common.network.server.SPEditBond;
import austeretony.oxygen_merchants.common.network.server.SPMerchantOperation;
import austeretony.oxygen_merchants.common.network.server.SPOpenMerchantMenu;
import austeretony.oxygen_merchants.common.network.server.SPRemoveBond;
import austeretony.oxygen_merchants.common.network.server.SPRemoveProfile;
import austeretony.oxygen_merchants.common.network.server.SPSendAbsentEntitiesIds;
import austeretony.oxygen_merchants.common.network.server.SPSendMerchantProfile;
import austeretony.oxygen_merchants.common.network.server.SPVisitEntity;
import austeretony.oxygen_merchants.common.update.MerchantsUpdateAdapter;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = MerchantsMain.MODID, 
        name = MerchantsMain.NAME, 
        version = MerchantsMain.VERSION,
        dependencies = "required-after:oxygen@[0.8.2,);",//TODO Always check required Oxygen version before build
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = MerchantsMain.VERSIONS_FORGE_URL)
public class MerchantsMain {

    public static final String 
    MODID = "oxygen_merchants",
    NAME = "Oxygen: Merchants",
    VERSION = "0.8.1",
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Merchants/info/mod_versions_forge.json";

    public static final int 
    MERCHANTS_MOD_INDEX = 4,//Oxygen - 0, Teleportation - 1, Groups - 2, Exchange - 3, Players List - 5, Friends List - 6, Interaction - 7, Mail - 8, Chat - 9

    MANAGEMENT_MENU_SCREEN_ID = 40,
    MERCHANT_MENU_SCREEN_ID = 41;

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    private static OxygenNetwork network;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperServer.registerConfig(new MerchantsConfig());
        if (event.getSide() == Side.CLIENT)
            CommandOxygenClient.registerArgumentExecutor(new MerchantsArgumentExecutorClient("merchants", true));
        UpdateAdaptersManager.register(new MerchantsUpdateAdapter());//TODO for 0.8.1b - patches old data to make it compatible with 0.8.1b
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        MerchantsManagerServer.create();
        CommonReference.registerEvent(new MerchantsEventsServer());
        OxygenHelperServer.addPersistentServiceProcess(new RunMerchantOperations());
        ComplexGUIHandlerServer.registerScreen(MANAGEMENT_MENU_SCREEN_ID, new ManagementMenuHandlerServer());
        CommandOxygenServer.registerArgumentExecutor(new MerchantsArgumentExecutorServer("merchants", true));
        if (event.getSide() == Side.CLIENT) {
            MerchantsManagerClient.create();
            CommonReference.registerEvent(new MerchantsEventsClient());
            InteractionHelperClient.registerInteraction(new MerchantInteraction());
            OxygenGUIHelper.registerScreenId(MANAGEMENT_MENU_SCREEN_ID);
            OxygenGUIHelper.registerScreenId(MERCHANT_MENU_SCREEN_ID);
            ComplexGUIHandlerClient.registerScreen(MANAGEMENT_MENU_SCREEN_ID, new ManagementMenuHandlerClient());
            OxygenGUIHelper.registerOverlay(new MerchantInteractionOverlay());
        }
    }

    private void initNetwork() {
        network = OxygenHelperServer.createNetworkHandler(MODID);

        network.registerPacket(CPMerchantsCommand.class);
        network.registerPacket(CPSyncMerchantProfiles.class);
        network.registerPacket(CPSyncEntityEntries.class);
        network.registerPacket(CPSyncValidEntitiesIds.class);
        network.registerPacket(CPUpdateMerchantMenu.class);
        network.registerPacket(CPSyncDataOpenMenu.class);

        network.registerPacket(SPSendAbsentEntitiesIds.class);
        network.registerPacket(SPCreateProfile.class);
        network.registerPacket(SPRemoveProfile.class);
        network.registerPacket(SPSendMerchantProfile.class);
        network.registerPacket(SPMerchantOperation.class);
        network.registerPacket(SPCreateBond.class);
        network.registerPacket(SPEditBond.class);
        network.registerPacket(SPVisitEntity.class);
        network.registerPacket(SPRemoveBond.class);
        network.registerPacket(SPOpenMerchantMenu.class);
    }

    public static OxygenNetwork network() {     
        return network;
    }
}
