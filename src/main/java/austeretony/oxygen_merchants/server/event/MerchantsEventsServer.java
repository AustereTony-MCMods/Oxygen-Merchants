package austeretony.oxygen_merchants.server.event;

import austeretony.oxygen_core.server.api.event.OxygenPlayerUnloadedEvent;
import austeretony.oxygen_core.server.api.event.OxygenWorldLoadedEvent;
import austeretony.oxygen_merchants.server.MerchantsManagerServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MerchantsEventsServer {

    @SubscribeEvent
    public void onWorldLoaded(OxygenWorldLoadedEvent event) {
        MerchantsManagerServer.instance().worldLoaded();
    }

    @SubscribeEvent
    public void onPlayerUnloaded(OxygenPlayerUnloadedEvent event) {         
        MerchantsManagerServer.instance().playerUnloaded(event.playerMP);
    }
}
