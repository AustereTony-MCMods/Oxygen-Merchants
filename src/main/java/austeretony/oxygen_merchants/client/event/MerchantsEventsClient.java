package austeretony.oxygen_merchants.client.event;

import austeretony.oxygen_core.client.api.event.OxygenClientInitEvent;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MerchantsEventsClient {

    @SubscribeEvent
    public void onClientInit(OxygenClientInitEvent event) {
        MerchantsManagerClient.instance().worldLoaded();
    } 
}
