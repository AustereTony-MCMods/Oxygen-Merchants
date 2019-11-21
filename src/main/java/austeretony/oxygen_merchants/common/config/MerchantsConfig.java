package austeretony.oxygen_merchants.common.config;

import java.util.List;

import austeretony.oxygen_core.common.EnumValueType;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.config.AbstractConfigHolder;
import austeretony.oxygen_core.common.api.config.ConfigValueImpl;
import austeretony.oxygen_core.common.config.ConfigValue;
import austeretony.oxygen_merchants.common.main.MerchantsMain;

public class MerchantsConfig extends AbstractConfigHolder {

    public static final ConfigValue 
    DATA_SAVE_DELAY_MINUTES = new ConfigValueImpl(EnumValueType.INT, "setup", "data_save_delay_minutes"),

    ALLOW_INGAME_MANAGEMENT = new ConfigValueImpl(EnumValueType.BOOLEAN, "main", "allow_ingame_management");

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
    public String getInternalPath() {
        return "assets/oxygen_merchants/config/merchants.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(DATA_SAVE_DELAY_MINUTES);

        values.add(ALLOW_INGAME_MANAGEMENT);
    }

    @Override
    public boolean sync() {
        return false;
    }
}
