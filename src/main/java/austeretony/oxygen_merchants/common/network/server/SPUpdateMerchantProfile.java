package austeretony.oxygen_merchants.common.network.server;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.RequestsFilterHelper;
import austeretony.oxygen_merchants.common.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.server.MerchantsManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPUpdateMerchantProfile extends Packet {

    private MerchantProfile profile;

    public SPUpdateMerchantProfile() {}

    public SPUpdateMerchantProfile(MerchantProfile profile) {
        this.profile = profile;
    }   

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        this.profile.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (RequestsFilterHelper.getLock(CommonReference.getPersistentUUID(playerMP), MerchantsMain.PROFILE_MANAGEMENT_REQUEST_ID)) {
            final MerchantProfile profile = new MerchantProfile();
            profile.read(buffer);
            OxygenHelperServer.addRoutineTask(()->MerchantsManagerServer.instance().getMerchantProfilesManager().editProfile(playerMP, profile));   
        }
    }
}       
