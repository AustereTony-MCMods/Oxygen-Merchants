package austeretony.oxygen_merchants.common.event;

import austeretony.oxygen.common.api.event.OxygenPlayerLoadedEvent;
import austeretony.oxygen.common.api.event.OxygenPlayerUnloadedEvent;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MerchantsEventsServer {

    @SubscribeEvent
    public void onPlayerLoaded(OxygenPlayerLoadedEvent event) {         
        MerchantsManagerServer.instance().onPlayerLoaded(event.player);
    }

    @SubscribeEvent
    public void onPlayerUnloaded(OxygenPlayerUnloadedEvent event) {         
        MerchantsManagerServer.instance().onPlayerUnloaded(event.player);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {  
        if (!event.getEntity().world.isRemote && event.getEntity() instanceof EntityLiving)
            MerchantsManagerServer.instance().getBoundEntitiesManager().entityLivingDied(CommonReference.getPersistentUUID(event.getEntity()));
    }
}
