package austeretony.oxygen_merchants.server.event;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.server.api.event.OxygenPlayerLoadedEvent;
import austeretony.oxygen_core.server.api.event.OxygenPlayerUnloadedEvent;
import austeretony.oxygen_core.server.api.event.OxygenWorldLoadedEvent;
import austeretony.oxygen_merchants.server.MerchantsManagerServer;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MerchantsEventsServer {

    @SubscribeEvent
    public void onWorldLoaded(OxygenWorldLoadedEvent event) {
        MerchantsManagerServer.instance().worldLoaded();
    }

    @SubscribeEvent
    public void onPlayerLoaded(OxygenPlayerLoadedEvent event) {         
        MerchantsManagerServer.instance().onPlayerLoaded(event.playerMP);
    }

    @SubscribeEvent
    public void onPlayerUnloaded(OxygenPlayerUnloadedEvent event) {         
        MerchantsManagerServer.instance().onPlayerUnloaded(event.playerMP);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {  
        if (!event.getEntity().world.isRemote && event.getEntity() instanceof EntityLiving)
            MerchantsManagerServer.instance().getBoundEntitiesManager().entityLivingDied(CommonReference.getPersistentUUID(event.getEntity()));
    }
}
