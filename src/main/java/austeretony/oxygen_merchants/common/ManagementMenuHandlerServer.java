package austeretony.oxygen_merchants.common;

import java.util.Set;
import java.util.UUID;

import austeretony.oxygen.common.api.network.OxygenNetwork;
import austeretony.oxygen.common.sync.gui.api.IComplexGUIHandlerServer;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import net.minecraft.network.PacketBuffer;

public class ManagementMenuHandlerServer implements IComplexGUIHandlerServer {

    @Override
    public OxygenNetwork getNetwork() {
        return MerchantsMain.network();
    }

    @Override
    public Set<Long> getValidIdentifiersFirst(UUID playerUUID) {
        return MerchantsManagerServer.instance().getMerchantProfilesManager().getProfilesIds();
    }

    @Override
    public Set<Long> getValidIdentifiersSecond(UUID playerUUID) {
        return MerchantsManagerServer.instance().getBoundEntitiesManager().getEntriesIds();
    }

    @Override
    public void writeEntries(UUID playerUUID, PacketBuffer buffer, long[] firstIds, long[] secondIds) {
        if (firstIds != null)
            for (long id : firstIds)
                MerchantsManagerServer.instance().getMerchantProfilesManager().getProfile(id).write(buffer);
        if (secondIds != null)
            for (long id : secondIds)
                MerchantsManagerServer.instance().getBoundEntitiesManager().getBoundEntityEntry(id).write(buffer);
    }
}
