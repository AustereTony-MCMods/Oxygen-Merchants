package austeretony.oxygen_merchants.common.config;

import java.util.List;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.config.AbstractConfig;
import austeretony.oxygen_core.common.config.ConfigValue;
import austeretony.oxygen_core.common.config.ConfigValueUtils;
import austeretony.oxygen_merchants.common.main.MerchantsMain;

public class MerchantsConfig extends AbstractConfig {

    public static final ConfigValue 
    MERCHANT_MENU_OPERATIONS_TIMEOUT_MILLIS = ConfigValueUtils.getValue("server", "merchant_menu_operations_timeout_millis", 120000),
    ALLOW_MANAGEMENT_INGAME = ConfigValueUtils.getValue("server", "allow_management_ingame", true),
    ADVANCED_LOGGING = ConfigValueUtils.getValue("server", "advanced_logging", false);

    @Override
    public String getDomain() {
        return MerchantsMain.MODID;
    }

    @Override
    public String getVersion() {
        return MerchantsMain.VERSION_CUSTOM;
    }

    @Override
    public String getExternalPath() {
        return CommonReference.getGameFolder() + "/config/oxygen/merchants.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(MERCHANT_MENU_OPERATIONS_TIMEOUT_MILLIS);
        values.add(ALLOW_MANAGEMENT_INGAME);
        values.add(ADVANCED_LOGGING);
    }
}
