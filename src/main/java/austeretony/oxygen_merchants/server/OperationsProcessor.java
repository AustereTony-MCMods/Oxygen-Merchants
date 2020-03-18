package austeretony.oxygen_merchants.server;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Nullable;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.CurrencyHelperServer;
import austeretony.oxygen_core.server.api.InventoryProviderServer;
import austeretony.oxygen_merchants.common.EnumMerchantOperation;
import austeretony.oxygen_merchants.common.merchant.MerchantOffer;
import austeretony.oxygen_merchants.common.merchant.MerchantProfile;
import austeretony.oxygen_merchants.common.network.client.CPMerchantAction;
import net.minecraft.entity.player.EntityPlayerMP;

public class OperationsProcessor {

    private final UUID playerUUID;

    private final Queue<QueuedMerchantOperation> operations = new ConcurrentLinkedQueue<>();

    @Nullable
    private MerchantProfile profile;

    public OperationsProcessor(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void setMerchantProfile(MerchantProfile profile) {
        this.profile = profile;
    }

    public void addOperation(EnumMerchantOperation operation, long offerId) {
        this.operations.offer(new QueuedMerchantOperation(operation, offerId));
    }

    public void process() {
        QueuedMerchantOperation queued;
        MerchantOffer offer;
        EntityPlayerMP playerMP;

        while (!this.operations.isEmpty()) {
            queued = this.operations.poll();
            if (queued != null) {
                if (this.profile != null) {
                    offer = this.profile.getOffer(queued.offerId);
                    if (offer != null) {
                        playerMP = CommonReference.playerByUUID(this.playerUUID);
                        if (playerMP != null) {
                            switch (queued.operation) {
                            case BUY:
                                if (offer.isBuyEnabled())
                                    this.buy(playerMP, this.profile, offer);
                                break;
                            case SELLING:
                                if (offer.isSellingEnabled())
                                    this.sell(playerMP, this.profile, offer);
                                break;
                            }
                        }
                    }
                }
            }
        }   
    }

    private void buy(EntityPlayerMP playerMP, MerchantProfile profile, MerchantOffer offer) {
        if (!InventoryProviderServer.getPlayerInventory().haveEnoughSpace(playerMP, offer.getStackWrapper(), offer.getAmount()))
            return;

        long balance;
        if (profile.isUsingVirtalCurrency()) {
            if (!CurrencyHelperServer.enoughCurrency(this.playerUUID, offer.getBuyCost(), profile.getCurrencyIndex()))
                return;

            CurrencyHelperServer.removeCurrency(this.playerUUID, offer.getBuyCost(), profile.getCurrencyIndex());

            balance = CurrencyHelperServer.getCurrency(this.playerUUID, profile.getCurrencyIndex());
        } else {
            if ((balance = InventoryProviderServer.getPlayerInventory().getEqualItemAmount(playerMP, profile.getCurrencyStackWrapper())) < offer.getBuyCost())
                return;

            InventoryProviderServer.getPlayerInventory().removeItem(playerMP, profile.getCurrencyStackWrapper(), (int) offer.getBuyCost());

            balance -= offer.getBuyCost();
        }

        InventoryProviderServer.getPlayerInventory().addItem(playerMP, offer.getStackWrapper(), offer.getAmount());
        this.notifyPlayer(playerMP, EnumMerchantOperation.BUY, offer, balance);
    }

    private void sell(EntityPlayerMP playerMP, MerchantProfile profile, MerchantOffer offer) {
        if (!profile.isUsingVirtalCurrency() && !InventoryProviderServer.getPlayerInventory().haveEnoughSpace(playerMP, offer.getStackWrapper(), (int) offer.getSellingCost()))
            return;

        if (InventoryProviderServer.getPlayerInventory().getEqualItemAmount(playerMP, offer.getStackWrapper()) < offer.getAmount())
            return;

        long balance;
        if (profile.isUsingVirtalCurrency()) {
            CurrencyHelperServer.addCurrency(this.playerUUID, offer.getSellingCost(), profile.getCurrencyIndex());

            balance = CurrencyHelperServer.getCurrency(this.playerUUID, profile.getCurrencyIndex());
        } else {
            balance = InventoryProviderServer.getPlayerInventory().getEqualItemAmount(playerMP, profile.getCurrencyStackWrapper());

            InventoryProviderServer.getPlayerInventory().addItem(playerMP, profile.getCurrencyStackWrapper(), (int) offer.getSellingCost());

            balance += offer.getSellingCost();
        }

        InventoryProviderServer.getPlayerInventory().removeItem(playerMP, offer.getStackWrapper(), offer.getAmount());
        this.notifyPlayer(playerMP, EnumMerchantOperation.SELLING, offer, balance);
    }

    public void notifyPlayer(EntityPlayerMP playerMP, EnumMerchantOperation operation, MerchantOffer offer, long balance) {
        OxygenMain.network().sendTo(new CPMerchantAction(operation, offer, balance), playerMP); 
    }
}
