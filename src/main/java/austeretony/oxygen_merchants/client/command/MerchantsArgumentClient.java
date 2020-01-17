package austeretony.oxygen_merchants.client.command;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class MerchantsArgumentClient implements ArgumentExecutor {

    @Override
    public String getName() {
        return "merchants";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 2) {
            if (args[1].equals("-reset-data")) {
                MerchantsManagerClient.instance().getMerchantProfilesContainer().reset();
                ClientReference.showChatMessage("oxygen_merchants.command.dataReset");
            }
        }
    }
}
