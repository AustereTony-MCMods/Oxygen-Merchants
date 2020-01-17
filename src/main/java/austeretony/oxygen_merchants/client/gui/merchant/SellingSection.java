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
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.MerchantOffer;
import net.minecraft.item.ItemStack;

public class SellingSection extends AbstractGUISection {

    private final MerchantScreen screen;

    private OxygenScrollablePanel offersPanel;

    private OxygenInventoryLoad inventoryLoad;

    private OxygenCurrencyValue balanceValue;

    public SellingSection(MerchantScreen screen) {
        super(screen);
        this.screen = screen;
        this.setDisplayText(ClientReference.localize("oxygen_merchants.gui.merchant.selling"));
    }

    @Override
    public void init() {
        this.addElement(new BuyBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, this.screen.merchantProfile.getName(), EnumBaseGUISetting.TEXT_TITLE_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.offersPanel = new OxygenScrollablePanel(this.screen, 6, 16, this.getWidth() - 15, 16, 1, MathUtils.clamp(this.screen.sellingOffersAmount, 9, 100), 9, EnumBaseGUISetting.TEXT_PANEL_SCALE.get().asFloat(), true));   

        this.offersPanel.<MerchantOfferPanelEntry>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (mouseButton == 0 && clicked.isAvailable()) 
                MerchantsManagerClient.instance().getMenuManager().performSellingSynced(this.screen.merchantProfile.getId(), clicked.index);
        });

        this.addElement(new OxygenSectionSwitcher(this.getWidth() - 4, 5, this, this.screen.getBuySection()));

        this.addElement(this.balanceValue = new OxygenCurrencyValue(this.getWidth() - 14, this.getHeight() - 10));   
        this.updateBalance();
        this.addElement(this.inventoryLoad = new OxygenInventoryLoad(6, this.getHeight() - 8));
        this.inventoryLoad.setLoad(this.screen.getBuySection().getInventoryLoad().getLoad());

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

        int stock;
        for (MerchantOffer offer : offers) {
            if (offer.isSellingEnabled()) {
                stock = this.screen.getEqualStackAmount(offer.getOfferedStack());
                this.offersPanel.addEntry(new MerchantOfferPanelEntry(
                        offer, 
                        offer.getSellingCost(), 
                        stock, 
                        currencyStack,
                        this.screen.getCurrencyProperties())
                        .setAvailable((this.screen.merchantProfile.isUsingVirtalCurrency() || !this.inventoryLoad.isOverloaded()) && stock >= offer.getAmount()));        
            }
        }

        this.offersPanel.getScroller().updateRowsAmount(MathUtils.clamp(offers.size(), 9, this.screen.sellingOffersAmount));
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {}

    public void bought(MerchantOffer offer, long balance) {
        this.balanceValue.updateValue(balance);
        this.balanceValue.setRed(balance == 0L);

        this.inventoryLoad.setLoad(this.screen.getBuySection().getInventoryLoad().getLoad());

        this.updateOffers();
    }

    private void updateOffers() {
        int stock;       
        MerchantOfferPanelEntry offerButton;
        MerchantOffer offer;
        for (GUIButton button : this.offersPanel.buttonsBuffer) {
            offerButton = (MerchantOfferPanelEntry) button;
            offer = this.screen.merchantProfile.getOffer(offerButton.index);
            stock = this.screen.getEqualStackAmount(offer.getOfferedStack());  
            offerButton.setAvailable((this.screen.merchantProfile.isUsingVirtalCurrency() || !this.inventoryLoad.isOverloaded()) && stock >= offer.getAmount());
            offerButton.setPlayerStock(stock);
        }
    }

    public void sold(MerchantOffer offer, long balance) {
        this.balanceValue.updateValue(balance);
        this.balanceValue.setRed(balance == 0L);

        this.inventoryLoad.setLoad(this.screen.getBuySection().getInventoryLoad().getLoad());

        this.updateOffers();
    }
}
