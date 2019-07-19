package austeretony.oxygen_merchants.common.command;

import java.util.Set;

import austeretony.oxygen.common.api.command.AbstractOxygenCommand;
import austeretony.oxygen.common.command.IArgumentExecutor;
import austeretony.oxygen.common.core.api.CommonReference;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandMerchants extends AbstractOxygenCommand {

    public CommandMerchants(String commandName) {
        super(commandName);
    }

    @Override
    public void getArgumentExecutors(Set<IArgumentExecutor> executors) {
        executors.add(new ManagementArgumentExecutor("management", false));
    }

    @Override
    public boolean valid(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && CommonReference.isOpped((EntityPlayerMP) sender);
    }
}
