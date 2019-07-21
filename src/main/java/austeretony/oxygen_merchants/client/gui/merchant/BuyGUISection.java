package austeretony.oxygen_merchants.client.gui.merchant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import austeretony.alternateui.screen.browsing.GUIScroller;
import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.button.GUISlider;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.panel.GUIButtonPanel;
import austeretony.alternateui.screen.panel.GUIButtonPanel.GUIEnumOrientation;
import austeretony.alternateui.screen.text.GUITextField;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.oxygen.client.api.WatcherHelperClient;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.IndexedGUIButton;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.itemstack.InventoryHelper;
import austeretony.oxygen.common.main.OxygenPlayerData;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen.util.MathUtils;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.gui.MerchantsGUITextures;
import austeretony.oxygen_merchants.common.main.MerchantOffer;
import austeretony.oxygen_merchants.common.main.MerchantProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class BuyGUISection extends AbstractGUISection {

    private final MerchantMenuGUIScreen screen;

    private GUIButton sellingSectionButton, searchButton;

    private GUITextField searchField;

    private GUITextLabel inventoryStateTextLabel;

    private GUIButtonPanel buyOffersPanel;

    private GUICurrencyBalance currencyBalance;

    private OfferGUIButton currentOfferButton;

    private boolean overloaded;

    private int balance, occupiedSlots;

    public BuyGUISection(MerchantMenuGUIScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new MerchantBackgroundGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new GUITextLabel(2, 4).setDisplayText(this.screen.merchantProfile.getName(), false, GUISettings.instance().getTitleScale()));

        String sectionName = ClientReference.localize("merchants.gui.merchant.buy");
        this.addElement(new GUITextLabel(this.getWidth() - 32 - this.textWidth(sectionName, GUISettings.instance().getTitleScale()), 4).setDisplayText(sectionName, false, GUISettings.instance().getTitleScale()));
        this.addElement(new GUIButton(this.getWidth() - 28, 0, 12, 12).setTexture(MerchantsGUITextures.BUY_ICONS, 12, 12).initSimpleTooltip(ClientReference.localize("merchants.gui.merchant.buy"), GUISettings.instance().getTooltipScale()).toggle());    
        this.addElement(this.sellingSectionButton = new GUIButton(this.getWidth() - 14, 0, 12, 12).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(MerchantsGUITextures.SELL_ICONS, 12, 12).initSimpleTooltip(ClientReference.localize("merchants.gui.merchant.selling"), GUISettings.instance().getTooltipScale()));     

        this.addElement(this.searchButton = new GUIButton(7, 15, 7, 7).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SEARCH_ICONS, 7, 7).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.search"), GUISettings.instance().getTooltipScale()));   

        this.buyOffersPanel = new GUIButtonPanel(GUIEnumOrientation.VERTICAL, 0, 24, this.getWidth() - 3, 16).setButtonsOffset(1).setTextScale(GUISettings.instance().getTextScale());
        this.addElement(this.buyOffersPanel);
        this.addElement(this.searchField = new GUITextField(0, 15, 113, 20).setScale(0.7F).enableDynamicBackground().setDisplayText("...", false, GUISettings.instance().getTextScale()).cancelDraggedElementLogic().disableFull());
        this.buyOffersPanel.initSearchField(this.searchField);
        GUIScroller scroller = new GUIScroller(MathUtils.clamp(this.screen.merchantProfile.getOffersAmount(), 9, 100), 9);
        this.buyOffersPanel.initScroller(scroller);
        GUISlider slider = new GUISlider(this.getWidth() - 2, 24, 2, 152);
        slider.setDynamicBackgroundColor(GUISettings.instance().getEnabledSliderColor(), GUISettings.instance().getDisabledSliderColor(), GUISettings.instance().getHoveredSliderColor());
        scroller.initSlider(slider); 

        this.addElement(this.inventoryStateTextLabel = new GUITextLabel(2, 179).setTextScale(GUISettings.instance().getSubTextScale()));   
        this.addElement(this.currencyBalance = new GUICurrencyBalance(this.getWidth() - 12, 181));   

        if (!this.screen.merchantProfile.isUsingCurrency())
            this.currencyBalance.setItemStack(this.screen.merchantProfile.getCurrencyStack().getItemStack());

        this.updateInventoryState();
        this.updateBalance();
    }

    public void updateInventoryState() {
        this.setInventoryState(InventoryHelper.getOccupiedSlotsAmount(this.mc.player));
    }

    public void setInventoryState(int value) {
        this.inventoryStateTextLabel.setDisplayText(String.valueOf(value) + "/" + String.valueOf(this.mc.player.inventory.mainInventory.size()));
        this.occupiedSlots = value;
        this.overloaded = value == this.mc.player.inventory.mainInventory.size();
        this.inventoryStateTextLabel.setEnabledTextColor(this.overloaded ? 0xFFCC0000 : 0xFFD1D1D1);
    }

    public void updateBalance() {
        int balance = 0;
        if (this.screen.merchantProfile.isUsingCurrency())
            balance = WatcherHelperClient.getInt(OxygenPlayerData.CURRENCY_GOLD_ID);
        else
            balance = InventoryHelper.getEqualStackAmount(this.mc.player, this.screen.merchantProfile.getCurrencyStack());
        this.setBalance(balance);
    }

    public void setBalance(int value) {
        this.currencyBalance.setBalance(value);
        this.currencyBalance.setEnabledTextColor(value == 0 ? 0xFFCC0000 : 0xFFD1D1D1);
        this.balance = value;
    }

    public void loadOffers() {
        List<MerchantOffer> offers = new ArrayList<MerchantOffer>(this.screen.merchantProfile.getOffers());
        Collections.sort(offers, new Comparator<MerchantOffer>() {

            @Override
            public int compare(MerchantOffer offer1, MerchantOffer offer2) {
                return (int) ((offer1.offerId - offer2.offerId) / 10_000L);
            }
        });

        OfferGUIButton button;
        ItemStack 
        currencyItemStack = null,
        offeredStack;
        if (!this.screen.merchantProfile.isUsingCurrency())
            currencyItemStack = this.screen.merchantProfile.getCurrencyStack().getItemStack();

        int 
        stock,
        sellingOfferCounter = 0;
        for (MerchantOffer offer : offers) {
            stock = InventoryHelper.getEqualStackAmount(this.mc.player, offer.getOfferedStack());
            offeredStack = offer.getOfferedStack().getItemStack();
            button = new OfferGUIButton(offer.offerId, stock, offeredStack, offer.getAmount(), offer.getBuyCost(), currencyItemStack);
            button.enableDynamicBackground(GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getHoveredElementColor());
            button.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
            button.setEnabled(!this.overloaded && this.balance >= offer.getBuyCost());
            button.requireDoubleClick();

            this.buyOffersPanel.addButton(button);

            if (offer.isSellingEnabled()) {
                button = new OfferGUIButton(offer.offerId, stock, offeredStack, offer.getAmount(), offer.getSellingCost(), currencyItemStack);
                button.enableDynamicBackground(GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getHoveredElementColor());
                button.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
                button.setEnabled(!this.overloaded && stock >= offer.getAmount());
                button.requireDoubleClick();

                this.screen.getSellingSection().getSellingOffersPanel().addButton(button);
                
                sellingOfferCounter++;
            }
        }

        this.screen.getSellingSection().getSellingOffersPanel().getScroller().updateRowsAmount(MathUtils.clamp(sellingOfferCounter, 9, 100));
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.searchField.isEnabled() && !this.searchField.isHovered())
            this.searchField.disableFull();
        return super.mouseClicked(mouseX, mouseY, mouseButton);                 
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.sellingSectionButton)
                this.screen.getSellingSection().open();
            else if (element == this.searchButton)
                this.searchField.enableFull();
            else if (element instanceof OfferGUIButton) {
                this.currentOfferButton = (OfferGUIButton) element;
                MerchantsManagerClient.instance().performBuySynced(this.screen.merchantProfile.getId(), this.currentOfferButton.id);
            }
        }
    }

    public void bought() {
        this.mc.player.playSound(OxygenSoundEffects.SELL.soundEvent, 0.5F, 1.0F);

        if (this.currentOfferButton != null) {
            MerchantOffer offer = this.screen.merchantProfile.getOffer(this.currentOfferButton.id);

            this.simulateBuy(ClientReference.getClientPlayer(), this.screen.merchantProfile, offer);
            this.updateInventoryState();

            this.currentOfferButton.setPlayerStock(this.currentOfferButton.getPlayerStock() + offer.getAmount());
            this.setBalance(this.balance - offer.getBuyCost());

            this.screen.getSellingSection().setInventoryState(this.occupiedSlots);
            this.screen.getSellingSection().setBalance(this.balance);
            this.screen.getSellingSection().updateOffer(offer.offerId, this.currentOfferButton.getPlayerStock());
        }

        for (GUIButton button : this.buyOffersPanel.buttonsBuffer)
            button.setEnabled(!this.overloaded && this.balance >= this.screen.merchantProfile.getOffer(((IndexedGUIButton) button).id).getBuyCost());
    }

    public void updateOffer(long offerId, int amount) {
        OfferGUIButton offerButton;
        for (GUIButton button : this.buyOffersPanel.buttonsBuffer) {
            offerButton = (OfferGUIButton) button;
            button.setEnabled(!this.overloaded && this.balance >= this.screen.merchantProfile.getOffer(offerButton.id).getBuyCost());
            if (offerButton.id == offerId)
                offerButton.setPlayerStock(amount);
        }
    }

    private void simulateBuy(EntityPlayer player, MerchantProfile profile, MerchantOffer offer) {
        if (!profile.isUsingCurrency())
            InventoryHelper.removeEqualStack(player, profile.getCurrencyStack(), offer.getBuyCost());
        InventoryHelper.addItemStack(player, offer.getOfferedStack().getItemStack(), offer.getAmount());
    }
}
