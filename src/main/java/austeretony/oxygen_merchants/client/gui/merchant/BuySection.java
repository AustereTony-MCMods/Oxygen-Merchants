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
import austeretony.oxygen_core.client.gui.elements.OxygenSorter;
import austeretony.oxygen_core.client.gui.elements.OxygenSorter.EnumSorting;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.common.sound.OxygenSoundEffects;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.merchant.MerchantOffer;
import net.minecraft.item.ItemStack;

public class BuySection extends AbstractGUISection {

    private final MerchantScreen screen;

    private OxygenSorter nameSorter, priceSorter;

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
        this.addElement(new OxygenTextLabel(4, 12, this.screen.getMerchantProfile().getDisplayName(), EnumBaseGUISetting.TEXT_TITLE_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        if (this.screen.debug) {
            this.addElement(new OxygenTextLabel(40, this.getHeight() - 7, String.format("Persistent Id: %d", this.screen.getMerchantProfile().getPersistentId()), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));
            this.addElement(new OxygenTextLabel(40, this.getHeight() - 1, String.format("File: %s", this.screen.getMerchantProfile().getFileName()), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));
        }

        this.addElement(this.priceSorter = new OxygenSorter(6, 18, EnumSorting.INACTIVE, ClientReference.localize("oxygen_core.gui.price")));   
        this.priceSorter.setSortingListener((sorting)->{
            this.nameSorter.reset();

            if (sorting == EnumSorting.DOWN)
                this.loadOffers(1);
            else
                this.loadOffers(2);
        });

        this.addElement(this.nameSorter = new OxygenSorter(12, 18, EnumSorting.INACTIVE, ClientReference.localize("oxygen_core.gui.name")));  
        this.nameSorter.setSortingListener((sorting)->{
            this.priceSorter.reset();

            if (sorting == EnumSorting.DOWN)
                this.loadOffers(3);
            else
                this.loadOffers(4);
        });

        this.addElement(this.offersPanel = new OxygenScrollablePanel(this.screen, 6, 24, this.getWidth() - 15, 16, 1, MathUtils.clamp(this.screen.buyOffersAmount, 9, 100), 9, EnumBaseGUISetting.TEXT_PANEL_SCALE.get().asFloat(), true));   

        this.offersPanel.<MerchantOfferPanelEntry>setElementClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (mouseButton == 0 && clicked.isAvailable())
                MerchantsManagerClient.instance().getMenuManager().performBuySynced(clicked.getWrapped());
        });

        this.addElement(new OxygenSectionSwitcher(this.getWidth() - 4, 5, this, this.screen.getSellingSection()));

        this.addElement(this.balanceValue = new OxygenCurrencyValue(this.getWidth() - 14, this.getHeight() - 10));  
        this.updateBalance();
        this.addElement(this.inventoryLoad = new OxygenInventoryLoad(6, this.getHeight() - 8));
        this.inventoryLoad.updateLoad();

        this.loadOffers(0);
    }

    private void updateBalance() {
        long balance = 0L;
        if (this.screen.getMerchantProfile().isUsingVirtalCurrency()) {
            balance = WatcherHelperClient.getLong(this.screen.getMerchantProfile().getCurrencyIndex());
            this.balanceValue.setValue(this.screen.getMerchantProfile().getCurrencyIndex(), balance);
        } else {
            balance = this.screen.getEqualStackAmount(this.screen.getMerchantProfile().getCurrencyStackWrapper());
            this.balanceValue.setValue(this.screen.getMerchantProfile().getCurrencyStackWrapper().getCachedItemStack(), (int) balance);
        }
        this.balanceValue.setRed(balance == 0L);
    }

    private void loadOffers(int mode) {
        List<MerchantOffer> offers = new ArrayList<>(this.screen.getMerchantProfile().getOffers());

        if (mode == 0)
            Collections.sort(offers, (o1, o2)->o1.getId() < o2.getId() ? - 1 : o1.getId() > o2.getId() ? 1 : 0);
        else if (mode == 1)
            Collections.sort(offers, (o1, o2)->o1.getBuyCost() < o2.getBuyCost() ? - 1 : o1.getBuyCost() > o2.getBuyCost() ? 1 : 0);
        else if (mode == 2)
            Collections.sort(offers, (o1, o2)->o2.getBuyCost() < o1.getBuyCost() ? - 1 : o2.getBuyCost() > o1.getBuyCost() ? 1 : 0);
        else if (mode == 3)
            Collections.sort(offers, (o1, o2)->getItemDisplayName(o1).compareTo(getItemDisplayName(o2)));
        else if (mode == 4)
            Collections.sort(offers, (o1, o2)->getItemDisplayName(o2).compareTo(getItemDisplayName(o1)));

        ItemStack currencyStack = null;
        if (!this.screen.getMerchantProfile().isUsingVirtalCurrency())
            currencyStack = this.screen.getMerchantProfile().getCurrencyStackWrapper().getCachedItemStack();

        this.offersPanel.reset();
        for (MerchantOffer offer : offers)
            if (offer.isBuyEnabled())
                this.offersPanel.addEntry(new MerchantOfferPanelEntry(
                        offer, 
                        offer.getBuyCost(), 
                        this.screen.getEqualStackAmount(offer.getStackWrapper()), 
                        currencyStack,
                        this.screen.getCurrencyProperties(),
                        this.screen.debug)
                        .setAvailable(!this.inventoryLoad.isOverloaded() && offer.getBuyCost() <= this.balanceValue.getValue()));    

        this.offersPanel.getScroller().updateRowsAmount(MathUtils.clamp(offers.size(), 9, this.screen.buyOffersAmount));
    }

    public static String getItemDisplayName(MerchantOffer offer) {
        return offer.getStackWrapper().getCachedItemStack().getDisplayName();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {}

    public void bought(MerchantOffer offer, long balance) {
        this.mc.player.playSound(OxygenSoundEffects.INVENTORY_OPERATION.getSoundEvent(), 0.5F, 1.0F);
        this.mc.player.playSound(OxygenSoundEffects.RINGING_COINS.getSoundEvent(), 0.5F, 1.0F);

        this.balanceValue.updateValue(balance);
        this.balanceValue.setRed(balance == 0L);

        if (!this.screen.getMerchantProfile().isUsingVirtalCurrency())
            this.screen.removeItemStack(this.screen.getMerchantProfile().getCurrencyStackWrapper(), (int) offer.getBuyCost());
        this.screen.addItemStack(offer.getStackWrapper(), offer.getAmount());
        this.inventoryLoad.updateLoad();

        this.updateOffers();
    }

    private void updateOffers() {
        MerchantOfferPanelEntry offerButton;
        MerchantOffer offer;
        for (GUIButton button : this.offersPanel.buttonsBuffer) {
            offerButton = (MerchantOfferPanelEntry) button;
            offer = this.screen.getMerchantProfile().getOffer(offerButton.getWrapped());
            offerButton.setAvailable(!this.inventoryLoad.isOverloaded() && offer.getBuyCost() <= this.balanceValue.getValue());
            if (offerButton.getWrapped() == offer.getId())
                offerButton.setPlayerStock(this.screen.getEqualStackAmount(offer.getStackWrapper()));
        }
    }

    public void sold(MerchantOffer offer, long balance) {
        this.balanceValue.updateValue(balance);
        this.balanceValue.setRed(balance == 0L);

        if (!this.screen.getMerchantProfile().isUsingVirtalCurrency())
            this.screen.addItemStack(this.screen.getMerchantProfile().getCurrencyStackWrapper(), (int) offer.getSellingCost());
        this.screen.removeItemStack(offer.getStackWrapper(), offer.getAmount());
        this.inventoryLoad.updateLoad();

        this.updateOffers();
    }

    public OxygenInventoryLoad getInventoryLoad() {
        return this.inventoryLoad;
    }
}
