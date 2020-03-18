package austeretony.oxygen_merchants.common.main;

import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.command.CommandOxygenClient;
import austeretony.oxygen_core.client.gui.settings.SettingsScreen;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.command.CommandOxygenOperator;
import austeretony.oxygen_core.server.network.NetworkRequestsRegistryServer;
import austeretony.oxygen_core.server.timeout.TimeOutRegistryServer;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.MerchantsStatusMessagesHandler;
import austeretony.oxygen_merchants.client.command.MerchantsArgumentClient;
import austeretony.oxygen_merchants.client.event.MerchantsEventsClient;
import austeretony.oxygen_merchants.client.gui.settings.MerchantsSettingsContainer;
import austeretony.oxygen_merchants.client.setting.gui.EnumMerchantsGUISetting;
import austeretony.oxygen_merchants.common.config.MerchantsConfig;
import austeretony.oxygen_merchants.common.network.client.CPMerchantAction;
import austeretony.oxygen_merchants.common.network.client.CPSyncProfileOpenMenu;
import austeretony.oxygen_merchants.common.network.client.CPTryOpenMerchantMenu;
import austeretony.oxygen_merchants.common.network.server.SPMerchantOperation;
import austeretony.oxygen_merchants.common.network.server.SPRequestMerchantProfileSync;
import austeretony.oxygen_merchants.server.MerchantsManagerServer;
import austeretony.oxygen_merchants.server.command.MerchantsArgumentOperator;
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
        dependencies = "required-after:oxygen_core@[0.11.0,);",
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = MerchantsMain.VERSIONS_FORGE_URL)
public class MerchantsMain {

    public static final String 
    MODID = "oxygen_merchants",
    NAME = "Oxygen: Merchants",
    VERSION = "0.11.0",
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Merchants/info/mod_versions_forge.json";

    public static final int 
    MERCHANTS_MOD_INDEX = 4,

    MERCHANT_MENU_SCREEN_ID = 41,

    MERCHANT_PROFILES_DATA_ID = 40,

    MERCHANT_OPERATION_REQUEST_ID = 46,

    MERCHANT_MENU_TIMEOUT_ID = 40;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperCommon.registerConfig(new MerchantsConfig());
        if (event.getSide() == Side.CLIENT)
            CommandOxygenClient.registerArgument(new MerchantsArgumentClient());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        MerchantsManagerServer.create();
        CommonReference.registerEvent(new MerchantsEventsServer());
        NetworkRequestsRegistryServer.registerRequest(MERCHANT_OPERATION_REQUEST_ID, 500);
        TimeOutRegistryServer.registerTimeOut(MERCHANT_MENU_TIMEOUT_ID, MerchantsConfig.MERCHANT_MENU_OPERATIONS_TIMEOUT_MILLIS.asInt());
        CommandOxygenOperator.registerArgument(new MerchantsArgumentOperator());
        if (event.getSide() == Side.CLIENT) {
            MerchantsManagerClient.create();
            CommonReference.registerEvent(new MerchantsEventsClient());
            OxygenGUIHelper.registerScreenId(MERCHANT_MENU_SCREEN_ID);
            OxygenHelperClient.registerStatusMessagesHandler(new MerchantsStatusMessagesHandler());
            EnumMerchantsGUISetting.register();
            SettingsScreen.registerSettingsContainer(new MerchantsSettingsContainer());
        }
    }

    private void initNetwork() {
        OxygenMain.network().registerPacket(CPSyncProfileOpenMenu.class);
        OxygenMain.network().registerPacket(CPTryOpenMerchantMenu.class);
        OxygenMain.network().registerPacket(CPMerchantAction.class);

        OxygenMain.network().registerPacket(SPMerchantOperation.class);
        OxygenMain.network().registerPacket(SPRequestMerchantProfileSync.class);
    }
}
