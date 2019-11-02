package austeretony.oxygen_merchants.server;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.currency.CurrencyHelperServer;
import austeretony.oxygen_core.common.inventory.InventoryHelper;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.OxygenPlayerData;
import austeretony.oxygen_core.server.api.WatcherHelperServer;
import austeretony.oxygen_merchants.common.EnumMerchantOperation;
import austeretony.oxygen_merchants.common.MerchantOffer;
import austeretony.oxygen_merchants.common.MerchantProfile;
import austeretony.oxygen_merchants.common.network.client.CPMerchantAction;
import net.minecraft.entity.player.EntityPlayerMP;

public class OperationsProcessor {

    private final UUID playerUUID;

    private final Queue<QueuedMerchantOperation> operations = new ConcurrentLinkedQueue<>();

    public OperationsProcessor(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void addOperation(EnumMerchantOperation operation, long profileId, long offerId) {
        this.operations.offer(new QueuedMerchantOperation(operation, profileId, offerId));
    }

    public void process() {
        QueuedMerchantOperation operation;
        MerchantProfile profile = null;
        MerchantOffer offer;
        EntityPlayerMP playerMP;
        boolean save = false;
        while (!this.operations.isEmpty()) {
            operation = this.operations.poll();
            if (profile == null) {
                if (MerchantsManagerServer.instance().getMerchantProfilesContainer().profileExist(operation.profileId))
                    profile = MerchantsManagerServer.instance().getMerchantProfilesContainer().getProfile(operation.profileId);
                else
                    break;
            } 
            if (profile != null) {
                if (profile.offerExist(operation.offerId)) {
                    playerMP = CommonReference.playerByUUID(this.playerUUID);
                    offer = profile.getOffer(operation.offerId);
                    switch (operation.operation) {
                    case BUY:
                        if (!offer.isSellingOnly())
                            save = this.buy(playerMP, profile, offer);
                        break;
                    case SELLING:
                        if (offer.isSellingEnabled())
                            save = this.sell(playerMP, profile, offer);
                        break;
                    }
                }
            }
        }   
        if (save) {
            CurrencyHelperServer.save(this.playerUUID);
            WatcherHelperServer.setValue(this.playerUUID, OxygenPlayerData.CURRENCY_COINS_WATCHER_ID, CurrencyHelperServer.getCurrency(this.playerUUID));
        }
    }

    private boolean buy(EntityPlayerMP playerMP, MerchantProfile profile, MerchantOffer offer) {
        boolean save = false;
        if (!InventoryHelper.haveEnoughSpace(playerMP, offer.getAmount(), offer.getOfferedStack().getCachedItemStack().getMaxStackSize()))
            return false;

        long balance;
        if (profile.isUsingCurrency()) {
            if (!CurrencyHelperServer.enoughCurrency(this.playerUUID, offer.getBuyCost()))
                return false;

            CurrencyHelperServer.removeCurrency(this.playerUUID, offer.getBuyCost());
            save = true;

            balance = CurrencyHelperServer.getCurrency(this.playerUUID);
        } else {
            if ((balance = InventoryHelper.getEqualStackAmount(playerMP, profile.getCurrencyStack())) < offer.getBuyCost())
                return false;

            CommonReference.delegateToServerThread(()->InventoryHelper.removeEqualStack(playerMP, profile.getCurrencyStack(), (int) offer.getBuyCost()));

            balance -= offer.getBuyCost();
        }

        CommonReference.delegateToServerThread(()->InventoryHelper.addItemStack(playerMP, offer.getOfferedStack().getItemStack(), offer.getAmount()));
        this.notifyPlayer(playerMP, EnumMerchantOperation.BUY, offer, balance);

        return save;
    }

    private boolean sell(EntityPlayerMP playerMP, MerchantProfile profile, MerchantOffer offer) {
        boolean save = false;

        if (!profile.isUsingCurrency() && !InventoryHelper.haveEnoughSpace(playerMP, (int) offer.getSellingCost(), offer.getOfferedStack().getCachedItemStack().getMaxStackSize()))
            return false;

        if (InventoryHelper.getEqualStackAmount(playerMP, offer.getOfferedStack()) < offer.getAmount())
            return false;

        long balance;
        if (profile.isUsingCurrency()) {
            CurrencyHelperServer.addCurrency(this.playerUUID, offer.getSellingCost());
            save = true;

            balance = CurrencyHelperServer.getCurrency(this.playerUUID);
        } else {
            balance = InventoryHelper.getEqualStackAmount(playerMP, profile.getCurrencyStack());

            CommonReference.delegateToServerThread(()->InventoryHelper.addItemStack(playerMP, profile.getCurrencyStack().getItemStack(), (int) offer.getSellingCost()));

            balance += offer.getSellingCost();
        }

        CommonReference.delegateToServerThread(()->InventoryHelper.removeEqualStack(playerMP, offer.getOfferedStack(), offer.getAmount()));
        this.notifyPlayer(playerMP, EnumMerchantOperation.SELLING, offer, balance);

        return save;
    }

    public void notifyPlayer(EntityPlayerMP playerMP, EnumMerchantOperation operation, MerchantOffer offer, long balance) {
        OxygenMain.network().sendTo(new CPMerchantAction(operation, offer, balance), playerMP); 
    }
}
