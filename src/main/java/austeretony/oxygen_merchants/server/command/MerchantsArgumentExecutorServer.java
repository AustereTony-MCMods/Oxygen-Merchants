package austeretony.oxygen_merchants.server.command;

import java.util.Set;

import austeretony.oxygen_core.common.api.command.AbstractArgumentExecutor;
import austeretony.oxygen_core.common.api.command.ArgumentParameterImpl;
import austeretony.oxygen_core.common.command.ArgumentParameter;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_merchants.common.network.client.CPOpenManagementMenu;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class MerchantsArgumentExecutorServer extends AbstractArgumentExecutor {

    public static final String ACTION_MANAGEMENT = "management";

    public MerchantsArgumentExecutorServer(String argument, boolean hasParams) {
        super(argument, hasParams);
    }

    @Override
    public void getParams(Set<ArgumentParameter> params) {
        params.add(new ArgumentParameterImpl(ACTION_MANAGEMENT));
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, Set<ArgumentParameter> params) throws CommandException {
        for (ArgumentParameter param : params)
            if (param.getBaseName().equals(ACTION_MANAGEMENT))
                OxygenMain.network().sendTo(new CPOpenManagementMenu(), (EntityPlayerMP) sender);
    }
}
