package austeretony.oxygen_merchants.server.command;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_merchants.common.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import austeretony.oxygen_merchants.common.network.client.CPOpenManagementMenu;
import austeretony.oxygen_merchants.common.network.client.CPTryOpenMerchantMenu;
import austeretony.oxygen_merchants.server.MerchantsManagerServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class MerchantsArgumentOperator implements ArgumentExecutor {

    @Override
    public String getName() {
        return "merchants";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 2) {
            if (args[1].equals("-management")) {
                if (sender instanceof MinecraftServer)
                    throw new WrongUsageException("Unavailable command for server console.");
                OxygenMain.network().sendTo(new CPOpenManagementMenu(), (EntityPlayerMP) sender);
            } else if (args[1].equals("-merchant-menu")) {
                if (args.length == 4) {
                    EntityPlayerMP targetPlayerMP = CommandBase.getPlayer(server, sender, args[2]);
                    long persistentId = CommandBase.parseLong(args[3], 0L, Long.MAX_VALUE);

                    MerchantProfile profile = MerchantsManagerServer.instance().getMerchantProfilesContainer().getProfileByPersistentId(persistentId);
                    if (profile != null) {
                        OxygenHelperServer.resetTimeOut(CommonReference.getPersistentUUID(targetPlayerMP), MerchantsMain.MERCHANT_MENU_TIMEOUT_ID);
                        OxygenMain.network().sendTo(new CPTryOpenMerchantMenu(profile.getId()), targetPlayerMP);
                    }
                }
            }
        }
    }
}
