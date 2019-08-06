package austeretony.oxygen_merchants.common.command;

import java.util.Set;

import austeretony.oxygen.common.api.command.AbstractArgumentExecutor;
import austeretony.oxygen.common.api.command.ArgumentParameter;
import austeretony.oxygen.common.command.IArgumentParameter;
import austeretony.oxygen.common.sync.gui.api.ComplexGUIHandlerServer;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
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
    public void getParams(Set<IArgumentParameter> params) {
        params.add(new ArgumentParameter(ACTION_MANAGEMENT));
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, Set<IArgumentParameter> params) throws CommandException {
        for (IArgumentParameter param : params) {
            if (param.getBaseName().equals(ACTION_MANAGEMENT))
                ComplexGUIHandlerServer.openScreen((EntityPlayerMP) sender, MerchantsMain.MANAGEMENT_MENU_SCREEN_ID);
        }
    }
}
