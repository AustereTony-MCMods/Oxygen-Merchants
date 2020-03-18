package austeretony.oxygen_merchants.server.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_merchants.common.config.MerchantsConfig;
import austeretony.oxygen_merchants.common.merchant.MerchantOffer;
import austeretony.oxygen_merchants.common.merchant.MerchantProfile;
import austeretony.oxygen_merchants.server.MerchantsManagerServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class MerchantsArgumentOperator implements ArgumentExecutor {

    @Override
    public String getName() {
        return "merchants";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP playerMP = null, targetPlayerMP;
        if (sender instanceof EntityPlayerMP)
            playerMP = CommandBase.getCommandSenderAsPlayer(sender);

        if (args.length >= 2) {
            if (args[1].equals("-reload-profiles")) {
                MerchantsManagerServer.instance().getMerchantProfilesManager().reloadOffers(playerMP);
                OxygenMain.LOGGER.info("[Merchants] (Operator/Console) {} reloaded merchant profiles.",
                        sender.getName());
            } else if (args[1].equals("-profile")) {
                if (args[2].equals("-create")) {
                    if (args.length >= 6) {
                        long id = CommandBase.parseLong(args[3], 0L, Long.MAX_VALUE);
                        String displayName = StringUtils.join(Arrays.copyOfRange(args, 5, args.length), ' ');

                        if (MerchantsManagerServer.instance().getMerchantProfilesManager().createProfile(
                                playerMP, 
                                id, 
                                args[4], 
                                displayName))
                            OxygenMain.LOGGER.info("[Merchants] (Operator/Console) {} created merchant profile <{} ({})>.",
                                    sender.getName(),
                                    displayName,
                                    id);
                    }
                } else if (args[2].equals("-set-name")) {
                    if (args.length >= 5) {
                        long persistentId = CommandBase.parseLong(args[3], 0L, Long.MAX_VALUE);
                        String displayName = StringUtils.join(Arrays.copyOfRange(args, 4, args.length), ' ');

                        if (MerchantsManagerServer.instance().getMerchantProfilesManager().editProfileName(
                                playerMP, 
                                persistentId, 
                                displayName))
                            OxygenMain.LOGGER.info("[Merchants] (Operator/Console) {} set merchnt profile <{}> display name to <{}>.",
                                    sender.getName(),
                                    persistentId,
                                    displayName);
                    }
                } else if (args[2].equals("-set-currency")) {
                    if (args.length >= 4) {
                        long persistentId = CommandBase.parseLong(args[3], 0L, Long.MAX_VALUE);
                        if (args.length == 4) {
                            if (!(sender instanceof EntityPlayerMP))
                                throw new WrongUsageException("Command available only for player!");
                            if (playerMP.getHeldItemMainhand() == ItemStack.EMPTY) 
                                throw new WrongUsageException("Main hand is empty!");

                            ItemStackWrapper stackWrapper = ItemStackWrapper.of(playerMP.getHeldItemMainhand());

                            if (MerchantsManagerServer.instance().getMerchantProfilesManager().editProfileCurrency(
                                    playerMP, 
                                    persistentId, 
                                    stackWrapper,
                                    - 1))
                                OxygenMain.LOGGER.info("[Merchants] (Operator/Console) {} set merchnt profile <{}> currency to item {}.",
                                        sender.getName(),
                                        persistentId,
                                        stackWrapper);
                        } else {
                            int currencyIndex = CommandBase.parseInt(args[4], 0, 127);

                            if (MerchantsManagerServer.instance().getMerchantProfilesManager().editProfileCurrency(
                                    playerMP, 
                                    persistentId, 
                                    null,
                                    currencyIndex))
                                OxygenMain.LOGGER.info("[Merchants] (Operator/Console) {} set merchnt profile <{}> currency to virtual currency <{}>.",
                                        sender.getName(),
                                        persistentId,
                                        currencyIndex);
                        }   
                    }
                } else if (args[2].equals("-add-offer")) {
                    if (args.length == 8) {
                        if (!(sender instanceof EntityPlayerMP))
                            throw new WrongUsageException("Command available only for player!");
                        if (playerMP.getHeldItemMainhand() == ItemStack.EMPTY) 
                            throw new WrongUsageException("Main hand is empty!");

                        long 
                        persistentId = CommandBase.parseLong(args[3], 0L, Long.MAX_VALUE),
                        newOfferId = CommandBase.parseLong(args[4], 0L, Long.MAX_VALUE);
                        int amount = CommandBase.parseInt(args[5], 1, Short.MAX_VALUE);
                        long 
                        buyCost = CommandBase.parseLong(args[6], 0L, Long.MAX_VALUE),
                        sellingCost = CommandBase.parseLong(args[7], 0L, Long.MAX_VALUE);

                        MerchantOffer offer = new MerchantOffer(
                                newOfferId,
                                ItemStackWrapper.of(playerMP.getHeldItemMainhand()),
                                amount,
                                buyCost,
                                sellingCost);

                        if (MerchantsManagerServer.instance().getMerchantProfilesManager().addMerchantOffer(playerMP, persistentId, offer))
                            OxygenMain.LOGGER.info("[Merchants] (Operator/Console) {} added new offer {} to merchant profile <{}>.",
                                    sender.getName(),
                                    offer,
                                    persistentId);  
                    }
                } else if (args[2].equals("-remove-offer")) {
                    if (args.length == 5) {
                        long 
                        persistentId = CommandBase.parseLong(args[3], 0L, Long.MAX_VALUE),
                        offerId = CommandBase.parseLong(args[4], 0L, Long.MAX_VALUE);

                        if (MerchantsManagerServer.instance().getMerchantProfilesManager().removeMerchantOffer(playerMP, persistentId, offerId))
                            OxygenMain.LOGGER.info("[Merchants] (Operator/Console) {} removed merchant profile offer <{}>.",
                                    sender.getName(),
                                    persistentId);
                    }
                } else if (args[2].equals("-remove")) {
                    if (args.length == 4) {
                        long persistentId = CommandBase.parseLong(args[3], 0L, Long.MAX_VALUE);

                        if (MerchantsManagerServer.instance().getMerchantProfilesManager().removeProfile(playerMP, persistentId))
                            OxygenMain.LOGGER.info("[Merchants] (Operator/Console) {} removed merchant profile <{}>.",
                                    sender.getName(),
                                    persistentId);
                    }
                } else if (args[2].equals("-save")) {
                    if (args.length == 4) {
                        long persistentId = CommandBase.parseLong(args[3], 0L, Long.MAX_VALUE);

                        if (MerchantsManagerServer.instance().getMerchantProfilesManager().saveProfile(playerMP, persistentId))
                            OxygenMain.LOGGER.info("[Merchants] (Operator/Console) {} saved merchant profile <{}>.",
                                    sender.getName(),
                                    persistentId);
                    }
                }
            } else if (args[1].equals("-open-menu")) {
                if (args.length == 5) {
                    targetPlayerMP = CommandBase.getPlayer(server, sender, args[2]);
                    long persistentId = CommandBase.parseLong(args[3], 0L, Long.MAX_VALUE);
                    boolean debug = CommandBase.parseBoolean(args[4]);

                    MerchantProfile profile = MerchantsManagerServer.instance().getMerchantProfilesManager().openMerchantMenu(targetPlayerMP, persistentId, debug);

                    if (MerchantsConfig.ADVANCED_LOGGING.asBoolean()) {
                        if (profile != null)
                            OxygenMain.LOGGER.info("[Merchants] (Operator/Console) {} opened merchant menu <{} ({})> for player {}/{}.",
                                    sender.getName(),
                                    profile.getDisplayName(),
                                    persistentId,
                                    CommonReference.getName(targetPlayerMP),
                                    CommonReference.getPersistentUUID(targetPlayerMP));
                        else
                            OxygenMain.LOGGER.info("[Merchants] (Operator/Console) {} failed to open merchant menu <{}> for player {}/{}.",
                                    sender.getName(),
                                    persistentId,
                                    CommonReference.getName(targetPlayerMP),
                                    CommonReference.getPersistentUUID(targetPlayerMP));
                    }
                }
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 2)
            return CommandBase.getListOfStringsMatchingLastWord(args, "-reload-profiles", "-profile", "-open-menu");
        else if (args.length >= 3) {
            if (args[1].equals("-profile"))
                return CommandBase.getListOfStringsMatchingLastWord(args, "-create", "-set-name", "-set-currency", "-add-offer", "-remove-offer", "-remove", "-save");
        }
        return Collections.<String>emptyList();
    }
}
