package austeretony.oxygen_merchants.common.main;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import austeretony.oxygen.common.api.WatcherHelperServer;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.currency.CurrencyHelperServer;
import austeretony.oxygen.common.itemstack.InventoryHelper;
import austeretony.oxygen.common.main.OxygenPlayerData;
import austeretony.oxygen_merchants.common.MerchantsManagerServer;
import austeretony.oxygen_merchants.common.network.client.CPUpdateMerchantMenu;
import net.minecraft.entity.player.EntityPlayerMP;

public class OperationsProcessor {

    private final UUID playerUUID;

    private final Queue<MerchantOperation> operations = new ConcurrentLinkedQueue<MerchantOperation>();

    public OperationsProcessor(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void addOperation(EnumOperation operation, long profileId, long offerId) {
        this.operations.offer(new MerchantOperation(operation, profileId, offerId));
    }

    public void process() {
        MerchantOperation operation;
        MerchantProfile profile = null;
        MerchantOffer offer;
        EntityPlayerMP playerMP;
        boolean save = false;
        while (!this.operations.isEmpty()) {
            operation = this.operations.poll();
            if (profile == null) {
                if (MerchantsManagerServer.instance().getMerchantProfilesManager().profileExist(operation.profileId))
                    profile = MerchantsManagerServer.instance().getMerchantProfilesManager().getProfile(operation.profileId);
                else
                    break;
            } 
            if (profile != null) {
                if (profile.offerExist(operation.offerId)) {
                    playerMP = CommonReference.playerByUUID(this.playerUUID);
                    offer = profile.getOffer(operation.offerId);
                    switch (operation.operation) {
                    case BUY:
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
            WatcherHelperServer.setValue(this.playerUUID, OxygenPlayerData.CURRENCY_COINS_WATCHER_ID, (int) CurrencyHelperServer.getCurrency(this.playerUUID));
        }
    }

    private boolean buy(EntityPlayerMP playerMP, MerchantProfile profile, MerchantOffer offer) {
        boolean save = false;
        if (!InventoryHelper.haveEnoughSpace(playerMP, offer.getAmount()))
            return false;

        if (profile.isUsingCurrency()) {
            if (!CurrencyHelperServer.enoughCurrency(this.playerUUID, offer.getBuyCost()))//abort if not enough currency
                return false;

            CurrencyHelperServer.removeCurrency(this.playerUUID, offer.getBuyCost());
            save = true;
        } else {
            if (InventoryHelper.getEqualStackAmount(playerMP, profile.getCurrencyStack()) < offer.getBuyCost())//abort if not enough currency
                return false;

            InventoryHelper.removeEqualStack(playerMP, profile.getCurrencyStack(), offer.getBuyCost());
        }

        InventoryHelper.addItemStack(playerMP, offer.getOfferedStack().getItemStack(), offer.getAmount());
        this.notifyPlayer(playerMP, EnumOperation.BUY);

        return save;
    }

    private boolean sell(EntityPlayerMP playerMP, MerchantProfile profile, MerchantOffer offer) {
        boolean save = false;

        if (!profile.isUsingCurrency() && !InventoryHelper.haveEnoughSpace(playerMP, offer.getSellingCost()))
            return false;

        if (InventoryHelper.getEqualStackAmount(playerMP, offer.getOfferedStack()) < offer.getAmount())//abort if not enough currency
            return false;

        if (profile.isUsingCurrency()) {
            CurrencyHelperServer.addCurrency(this.playerUUID, offer.getSellingCost());
            save = true;
        } else
            InventoryHelper.addItemStack(playerMP, profile.getCurrencyStack().getItemStack(), offer.getSellingCost());

        InventoryHelper.removeEqualStack(playerMP, offer.getOfferedStack(), offer.getAmount());
        this.notifyPlayer(playerMP, EnumOperation.SELLING);

        return save;
    }

    public void notifyPlayer(EntityPlayerMP playerMP, EnumOperation operation) {
        MerchantsMain.network().sendTo(new CPUpdateMerchantMenu(operation), playerMP); 
    }

    public static class MerchantOperation {

        public final EnumOperation operation;

        public final long profileId, offerId;

        public MerchantOperation(EnumOperation operation, long profileId, long offerId) {
            this.operation = operation;
            this.profileId = profileId;
            this.offerId = offerId;
        }
    }

    public enum EnumOperation {

        BUY,
        SELLING;
    }
}
