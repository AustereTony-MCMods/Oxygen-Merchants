package austeretony.oxygen_merchants.common.main;

import austeretony.oxygen.client.core.api.ClientReference;
import net.minecraft.util.text.TextComponentTranslation;

public enum EnumMerchantsChatMessages {

    INGAME_MANAGEMENT_DISABLED,
    MERCHANTS_DATA_RELOADED,
    BOUGHT_ITEM,
    SOLD_ITEM;

    public void show(String... args) {
        switch (this) {
        case INGAME_MANAGEMENT_DISABLED:
            ClientReference.showMessage(new TextComponentTranslation("merchants.message.ingameManagementDisabled"));
            break;
        case MERCHANTS_DATA_RELOADED:
            ClientReference.showMessage(new TextComponentTranslation("merchants.message.merchantsDataReloaded"));
            break;
        case BOUGHT_ITEM:
            ClientReference.showMessage(new TextComponentTranslation("merchants.message.bought", args[0], args[1]));
            break;
        case SOLD_ITEM:
            ClientReference.showMessage(new TextComponentTranslation("merchants.message.sold", args[0], args[1]));
            break;
        }
    }
}
