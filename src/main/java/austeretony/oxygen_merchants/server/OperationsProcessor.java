package austeretony.oxygen_merchants.server;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.inventory.InventoryHelper;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.CurrencyHelperServer;
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
        QueuedMerchantOperation queued;
        MerchantProfile profile;
        MerchantOffer offer;
        EntityPlayerMP playerMP;

        while (!this.operations.isEmpty()) {
            queued = this.operations.poll();
            if (queued != null) {
                profile = MerchantsManagerServer.instance().getMerchantProfilesContainer().getProfile(queued.profileId); 
                if (profile != null) {
                    offer = profile.getOffer(queued.offerId);
                    if (offer != null) {
                        playerMP = CommonReference.playerByUUID(this.playerUUID);
                        if (playerMP != null) {
                            switch (queued.operation) {
                            case BUY:
                                if (offer.isBuyEnabled())
                                    this.buy(playerMP, profile, offer);
                                break;
                            case SELLING:
                                if (offer.isSellingEnabled())
                                    this.sell(playerMP, profile, offer);
                                break;
                            }
                        }
                    }
                }
            }
        }   
    }

    private void buy(EntityPlayerMP playerMP, MerchantProfile profile, MerchantOffer offer) {
        if (!InventoryHelper.haveEnoughSpace(playerMP, offer.getAmount(), offer.getOfferedStack().getCachedItemStack().getMaxStackSize()))
            return;

        long balance;
        if (profile.isUsingVirtalCurrency()) {
            if (!CurrencyHelperServer.enoughCurrency(this.playerUUID, offer.getBuyCost(), profile.getCurrencyIndex()))
                return;

            CurrencyHelperServer.removeCurrency(this.playerUUID, offer.getBuyCost(), profile.getCurrencyIndex());

            balance = CurrencyHelperServer.getCurrency(this.playerUUID, profile.getCurrencyIndex());
        } else {
            if ((balance = InventoryHelper.getEqualStackAmount(playerMP, profile.getCurrencyStack())) < offer.getBuyCost())
                return;

            CommonReference.delegateToServerThread(()->InventoryHelper.removeEqualStack(playerMP, profile.getCurrencyStack(), (int) offer.getBuyCost()));

            balance -= offer.getBuyCost();
        }

        CommonReference.delegateToServerThread(()->InventoryHelper.addItemStack(playerMP, offer.getOfferedStack().getCachedItemStack().copy(), offer.getAmount()));
        this.notifyPlayer(playerMP, EnumMerchantOperation.BUY, offer, balance);
    }

    private void sell(EntityPlayerMP playerMP, MerchantProfile profile, MerchantOffer offer) {
        if (!profile.isUsingVirtalCurrency() && !InventoryHelper.haveEnoughSpace(playerMP, (int) offer.getSellingCost(), offer.getOfferedStack().getCachedItemStack().getMaxStackSize()))
            return;

        if (InventoryHelper.getEqualStackAmount(playerMP, offer.getOfferedStack()) < offer.getAmount())
            return;

        long balance;
        if (profile.isUsingVirtalCurrency()) {
            CurrencyHelperServer.addCurrency(this.playerUUID, offer.getSellingCost(), profile.getCurrencyIndex());

            balance = CurrencyHelperServer.getCurrency(this.playerUUID, profile.getCurrencyIndex());
        } else {
            balance = InventoryHelper.getEqualStackAmount(playerMP, profile.getCurrencyStack());

            CommonReference.delegateToServerThread(()->InventoryHelper.addItemStack(playerMP, profile.getCurrencyStack().getCachedItemStack().copy(), (int) offer.getSellingCost()));

            balance += offer.getSellingCost();
        }

        CommonReference.delegateToServerThread(()->InventoryHelper.removeEqualStack(playerMP, offer.getOfferedStack(), offer.getAmount()));
        this.notifyPlayer(playerMP, EnumMerchantOperation.SELLING, offer, balance);
    }

    public void notifyPlayer(EntityPlayerMP playerMP, EnumMerchantOperation operation, MerchantOffer offer, long balance) {
        OxygenMain.network().sendTo(new CPMerchantAction(operation, offer, balance), playerMP); 
    }
}
