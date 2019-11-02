package austeretony.oxygen_merchants.client;

import austeretony.oxygen_core.common.status.ChatMessagesHandler;
import austeretony.oxygen_merchants.common.main.EnumMerchantsStatusMessage;
import austeretony.oxygen_merchants.common.main.MerchantsMain;

public class MerchantsStatusMessagesHandler implements ChatMessagesHandler {

    @Override
    public int getModIndex() {
        return MerchantsMain.MERCHANTS_MOD_INDEX;
    }

    @Override
    public String getMessage(int messageIndex) {
        return EnumMerchantsStatusMessage.values()[messageIndex].localizedName();
    }
}
