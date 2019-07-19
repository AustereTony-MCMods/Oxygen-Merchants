package austeretony.oxygen_merchants.common.command;

import java.util.Set;

import austeretony.oxygen.common.api.command.AbstractArgumentExecutor;
import austeretony.oxygen.common.command.IArgumentParameter;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class ManagementArgumentExecutor extends AbstractArgumentExecutor {

    public ManagementArgumentExecutor(String argument, boolean hasParams) {
        super(argument, hasParams);
    }

    @Override
    public void getParams(Set<IArgumentParameter> params) {}

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, Set<IArgumentParameter> params) throws CommandException {
        MerchantsManagerServer.instance().openManagementMenu((EntityPlayerMP) sender);
    }
}
