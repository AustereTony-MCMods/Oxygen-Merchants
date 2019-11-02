package austeretony.oxygen_merchants.client.event;

import austeretony.oxygen_core.client.api.event.OxygenChatMessageEvent;
import austeretony.oxygen_core.client.api.event.OxygenClientInitEvent;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.main.EnumMerchantsChatMessage;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MerchantsEventsClient {

    @SubscribeEvent
    public void onChatMessage(OxygenChatMessageEvent event) {   
        if (event.modIndex == MerchantsMain.MERCHANTS_MOD_INDEX)
            EnumMerchantsChatMessage.values()[event.messageIndex].show(event.args);
    }

    @SubscribeEvent
    public void onClientInit(OxygenClientInitEvent event) {
        MerchantsManagerClient.instance().worldLoaded();
    } 
}
