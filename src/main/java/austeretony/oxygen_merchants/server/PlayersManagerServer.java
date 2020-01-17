package austeretony.oxygen_merchants.server;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_merchants.common.EnumMerchantOperation;
import net.minecraft.entity.player.EntityPlayerMP;

public class PlayersManagerServer {

    private final Map<UUID, OperationsProcessor> processors = new ConcurrentHashMap<>();

    private final MerchantsManagerServer manager;

    protected PlayersManagerServer(MerchantsManagerServer manager) {
        this.manager = manager;
    }

    public OperationsProcessor getOperationsProcessor(UUID playerUUID) {
        OperationsProcessor processor = this.processors.get(playerUUID);
        if (processor == null) {
            processor = new OperationsProcessor(playerUUID);
            this.processors.put(playerUUID, processor);
        }
        return processor;
    }

    public void removeOperationsProcessor(UUID playerUUID) {
        this.processors.remove(playerUUID);
    }

    void process() {
        OxygenHelperServer.addRoutineTask(()->{
            for (OperationsProcessor processor : this.processors.values())
                processor.process();
        });
    }

    public void onPlayerUnloaded(EntityPlayerMP playerMP) {
        this.removeOperationsProcessor(CommonReference.getPersistentUUID(playerMP));
    }

    public void performOperation(EntityPlayerMP playerMP, EnumMerchantOperation operation, long profileId, long offerId) {
        this.getOperationsProcessor(CommonReference.getPersistentUUID(playerMP)).addOperation(operation, profileId, offerId);
    }
}
