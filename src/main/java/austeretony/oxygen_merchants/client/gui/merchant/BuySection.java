package austeretony.oxygen_merchants.client.gui.merchant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.api.WatcherHelperClient;
import austeretony.oxygen_core.client.gui.elements.OxygenCurrencyValue;
import austeretony.oxygen_core.client.gui.elements.OxygenInventoryLoad;
import austeretony.oxygen_core.client.gui.elements.OxygenScrollablePanel;
import austeretony.oxygen_core.client.gui.elements.OxygenSectionSwitcher;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.common.inventory.InventoryHelper;
import austeretony.oxygen_core.common.sound.OxygenSoundEffects;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.MerchantOffer;
import net.minecraft.item.ItemStack;

public class BuySection extends AbstractGUISection {

    private final MerchantScreen screen;

    private OxygenScrollablePanel offersPanel;

    private OxygenInventoryLoad inventoryLoad;

    private OxygenCurrencyValue balanceValue;

    public BuySection(MerchantScreen screen) {
        super(screen);
        this.screen = screen;
        this.setDisplayText(ClientReference.localize("oxygen_merchants.gui.merchant.buy"));
    }

    @Override
    public void init() {
        this.addElement(new BuyBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, this.screen.merchantProfile.getName(), EnumBaseGUISetting.TEXT_TITLE_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.offersPanel = new OxygenScrollablePanel(this.screen, 6, 16, this.getWidth() - 15, 16, 1, MathUtils.clamp(this.screen.buyOffersAmount, 9, 100), 9, EnumBaseGUISetting.TEXT_PANEL_SCALE.get().asFloat(), true));   

        this.offersPanel.<MerchantOfferPanelEntry>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (mouseButton == 0 && clicked.isAvailable())
                MerchantsManagerClient.instance().getMenuManager().performBuySynced(this.screen.merchantProfile.getId(), clicked.index);
        });

        this.addElement(new OxygenSectionSwitcher(this.getWidth() - 4, 5, this, this.screen.getSellingSection()));

        this.addElement(this.balanceValue = new OxygenCurrencyValue(this.getWidth() - 14, this.getHeight() - 10));  
        this.updateBalance();
        this.addElement(this.inventoryLoad = new OxygenInventoryLoad(6, this.getHeight() - 8));
        this.inventoryLoad.updateLoad();

        this.loadOffers();
    }

    private void updateBalance() {
        long balance = 0L;
        if (this.screen.merchantProfile.isUsingVirtalCurrency()) {
            balance = WatcherHelperClient.getLong(this.screen.merchantProfile.getCurrencyIndex());
            this.balanceValue.setValue(this.screen.merchantProfile.getCurrencyIndex(), balance);
        } else {
            balance = InventoryHelper.getEqualStackAmount(this.mc.player, this.screen.merchantProfile.getCurrencyStack());
            this.balanceValue.setValue(this.screen.merchantProfile.getCurrencyStack().getCachedItemStack(), (int) balance);
        }
        this.balanceValue.setRed(balance == 0L);
    }

    private void loadOffers() {
        List<MerchantOffer> offers = new ArrayList<>(this.screen.merchantProfile.getOffers());

        Collections.sort(offers, (o1, o2)->o1.offerId < o2.offerId ? - 1 : o1.offerId > o2.offerId ? 1 : 0);

        ItemStack currencyStack = null;
        if (!this.screen.merchantProfile.isUsingVirtalCurrency())
            currencyStack = this.screen.merchantProfile.getCurrencyStack().getCachedItemStack();

        for (MerchantOffer offer : offers)
            if (offer.isBuyEnabled())
                this.offersPanel.addEntry(new MerchantOfferPanelEntry(
                        offer, 
                        offer.getBuyCost(), 
                        this.screen.getEqualStackAmount(offer.getOfferedStack()), 
                        currencyStack,
                        this.screen.getCurrencyProperties())
                        .setAvailable(!this.inventoryLoad.isOverloaded() && offer.getBuyCost() <= this.balanceValue.getValue()));    

        this.offersPanel.getScroller().updateRowsAmount(MathUtils.clamp(offers.size(), 9, this.screen.buyOffersAmount));
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {}

    public void bought(MerchantOffer offer, long balance) {
        this.mc.player.playSound(OxygenSoundEffects.SELL.soundEvent, 0.5F, 1.0F);

        this.balanceValue.updateValue(balance);
        this.balanceValue.setRed(balance == 0L);

        if (!this.screen.merchantProfile.isUsingVirtalCurrency())
            InventoryHelper.removeEqualStack(this.mc.player, this.screen.merchantProfile.getCurrencyStack(), (int) offer.getBuyCost());
        InventoryHelper.addItemStack(this.mc.player, offer.getOfferedStack().getCachedItemStack(), offer.getAmount());
        this.inventoryLoad.updateLoad();
        this.screen.updateInventoryContent();

        this.updateOffers();
    }

    private void updateOffers() {
        MerchantOfferPanelEntry offerButton;
        MerchantOffer offer;
        for (GUIButton button : this.offersPanel.buttonsBuffer) {
            offerButton = (MerchantOfferPanelEntry) button;
            offer = this.screen.merchantProfile.getOffer(offerButton.index);
            offerButton.setAvailable(!this.inventoryLoad.isOverloaded() && offer.getBuyCost() <= this.balanceValue.getValue());
            if (offerButton.index == offer.offerId)
                offerButton.setPlayerStock(this.screen.getEqualStackAmount(offer.getOfferedStack()));
        }
    }

    public void sold(MerchantOffer offer, long balance) {
        this.mc.player.playSound(OxygenSoundEffects.SELL.soundEvent, 0.5F, 1.0F);

        this.balanceValue.updateValue(balance);
        this.balanceValue.setRed(balance == 0L);

        if (!this.screen.merchantProfile.isUsingVirtalCurrency())
            InventoryHelper.addItemStack(this.mc.player, this.screen.merchantProfile.getCurrencyStack().getCachedItemStack(), (int) offer.getSellingCost());
        InventoryHelper.removeEqualStack(this.mc.player, offer.getOfferedStack(), offer.getAmount());
        this.inventoryLoad.updateLoad();
        this.screen.updateInventoryContent();

        this.updateOffers();
    }

    public OxygenInventoryLoad getInventoryLoad() {
        return this.inventoryLoad;
    }
}
