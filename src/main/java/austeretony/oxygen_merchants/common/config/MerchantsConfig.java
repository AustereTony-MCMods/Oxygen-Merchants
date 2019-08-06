package austeretony.oxygen_merchants.common.config;

import java.util.List;

import austeretony.oxygen.common.api.config.AbstractConfigHolder;
import austeretony.oxygen.common.api.config.ConfigValue;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen_merchants.common.main.MerchantsMain;

public class MerchantsConfig extends AbstractConfigHolder {

    public static final ConfigValue ALLOW_INGAME_MANAGEMENT = new ConfigValue(ConfigValue.EnumValueType.BOOLEAN, "main", "allow_ingame_management");

    @Override
    public String getModId() {
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
        return "assets/oxygen_merchants/merchants.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(ALLOW_INGAME_MANAGEMENT);
    }

    @Override
    public boolean sync() {
        return false;
    }
}
