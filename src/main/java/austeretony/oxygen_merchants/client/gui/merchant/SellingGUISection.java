package austeretony.oxygen_merchants.client.gui.merchant;

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

public class SellingGUISection extends AbstractGUISection {

    private final MerchantMenuGUIScreen screen;

    private GUIButton buySectionButton, searchButton;

    private GUITextField searchField;

    private GUITextLabel inventoryStateTextLabel;

    private GUIButtonPanel sellingOffersPanel;

    private GUICurrencyBalance currencyBalance;

    private OfferGUIButton currentOfferButton;

    private boolean overloaded;

    private int balance, occupiedSlots;

    public SellingGUISection(MerchantMenuGUIScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new MerchantBackgroundGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new GUITextLabel(2, 4).setDisplayText(this.screen.merchantProfile.getName(), false, GUISettings.instance().getTitleScale()));

        String sectionName = ClientReference.localize("merchants.gui.merchant.selling");
        this.addElement(new GUITextLabel(this.getWidth() - 30 - this.textWidth(sectionName, GUISettings.instance().getTitleScale()), 4).setDisplayText(sectionName, false, GUISettings.instance().getTitleScale()));
        this.addElement(this.buySectionButton = new GUIButton(this.getWidth() - 28, 0, 12, 12).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(MerchantsGUITextures.BUY_ICONS, 12, 12).initSimpleTooltip(ClientReference.localize("merchants.gui.merchant.buy"), GUISettings.instance().getTooltipScale()));  
        this.addElement(new GUIButton(this.getWidth() - 14, 0, 12, 12).setTexture(MerchantsGUITextures.SELL_ICONS, 12, 12).initSimpleTooltip(ClientReference.localize("merchants.gui.merchant.selling"), GUISettings.instance().getTooltipScale()).toggle());    

        this.addElement(this.searchButton = new GUIButton(7, 15, 7, 7).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SEARCH_ICONS, 7, 7).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.search"), GUISettings.instance().getTooltipScale()));   

        this.sellingOffersPanel = new GUIButtonPanel(GUIEnumOrientation.VERTICAL, 0, 24, 180, 16).setButtonsOffset(1).setTextScale(GUISettings.instance().getTextScale());
        this.addElement(this.sellingOffersPanel);
        this.addElement(this.searchField = new GUITextField(0, 15, 113, 20).setScale(0.7F).enableDynamicBackground().setDisplayText("...", false, GUISettings.instance().getTextScale()).cancelDraggedElementLogic().disableFull());
        this.sellingOffersPanel.initSearchField(this.searchField);
        GUIScroller scroller = new GUIScroller(MathUtils.clamp(this.screen.merchantProfile.getOffersAmount(), 9, 100), 9);
        this.sellingOffersPanel.initScroller(scroller);
        GUISlider slider = new GUISlider(181, 24, 2, 152);
        slider.setDynamicBackgroundColor(GUISettings.instance().getEnabledSliderColor(), GUISettings.instance().getDisabledSliderColor(), GUISettings.instance().getHoveredSliderColor());
        scroller.initSlider(slider);        

        this.addElement(this.inventoryStateTextLabel = new GUITextLabel(2, 179).setTextScale(GUISettings.instance().getSubTextScale()));   
        this.addElement(this.currencyBalance = new GUICurrencyBalance(171, 181));   

        if (!this.screen.merchantProfile.isUsingCurrency())
            this.currencyBalance.setItemStack(this.screen.merchantProfile.getCurrencyStack().getItemStack());

        this.updateInventoryState();
        this.updateBalance();

        this.screen.getBuySection().loadOffers();
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
        else {
            for (ItemStack itemStack : this.mc.player.inventory.mainInventory)
                if (this.screen.merchantProfile.getCurrencyStack().isEquals(itemStack))
                    balance += itemStack.getCount();
        }
        this.setBalance(balance);
    }

    public void setBalance(int value) {
        this.currencyBalance.setBalance(value);
        this.currencyBalance.setEnabledTextColor(value == 0 ? 0xFFCC0000 : 0xFFD1D1D1);
        this.balance = value;
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
            if (element == this.buySectionButton)
                this.screen.getBuySection().open();
            else if (element == this.searchButton)
                this.searchField.enableFull();
            else if (element instanceof OfferGUIButton) {
                this.currentOfferButton = (OfferGUIButton) element;
                MerchantsManagerClient.instance().performSellingSynced(this.screen.merchantProfile.getId(), this.currentOfferButton.id);
            }
        }
    }

    public void sold() {
        this.mc.player.playSound(OxygenSoundEffects.SELL.soundEvent, 0.5F, 1.0F);

        if (this.currentOfferButton != null) {
            MerchantOffer offer = this.screen.merchantProfile.getOffer(this.currentOfferButton.id);

            this.simulateSelling(ClientReference.getClientPlayer(), this.screen.merchantProfile, offer);
            this.updateInventoryState();

            this.currentOfferButton.setPlayerStock(this.currentOfferButton.getPlayerStock() - offer.getAmount());
            this.setBalance(this.balance + offer.getSellingCost());

            this.currentOfferButton.setEnabled(this.currentOfferButton.getPlayerStock() >= offer.getAmount());

            this.screen.getBuySection().setInventoryState(this.occupiedSlots);
            this.screen.getBuySection().setBalance(this.balance);
            this.screen.getBuySection().updateOffer(offer.offerId, this.currentOfferButton.getPlayerStock());
        }
    }

    public void updateOffer(long offerId, int amount) {
        OfferGUIButton offerButton;
        for (GUIButton button : this.sellingOffersPanel.buttonsBuffer) {
            offerButton = (OfferGUIButton) button;
            if (offerButton.id == offerId) {
                offerButton.setEnabled(amount >= this.screen.merchantProfile.getOffer(offerButton.id).getAmount());
                offerButton.setPlayerStock(amount);
            }
        }
    }

    private void simulateSelling(EntityPlayer player, MerchantProfile profile, MerchantOffer offer) {
        if (!profile.isUsingCurrency())
            InventoryHelper.addItemStack(player, profile.getCurrencyStack().getItemStack(), offer.getSellingCost());
        InventoryHelper.removeEqualStack(player, offer.getOfferedStack(), offer.getAmount());
    }

    public GUIButtonPanel getSellingOffersPanel() {
        return this.sellingOffersPanel;
    }
}
