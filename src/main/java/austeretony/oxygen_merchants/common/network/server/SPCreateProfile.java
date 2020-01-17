package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.server.MerchantsManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPCreateProfile extends Packet {

    private String name;

    public SPCreateProfile() {}

    public SPCreateProfile(String name) {
        this.name = name;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        ByteBufUtils.writeString(this.name, buffer);
    }

    @Override   
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (OxygenHelperServer.isNetworkRequestAvailable(CommonReference.getPersistentUUID(playerMP), MerchantsMain.PROFILE_MANAGEMENT_REQUEST_ID)) {
            final String name = ByteBufUtils.readString(buffer);
            OxygenHelperServer.addRoutineTask(()->MerchantsManagerServer.instance().getMerchantProfilesManager().createProfile(playerMP, name));
        }
    }
}
