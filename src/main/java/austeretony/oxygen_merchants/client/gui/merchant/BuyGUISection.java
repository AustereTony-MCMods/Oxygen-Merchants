package austeretony.oxygen_merchants.client.gui.merchant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.WatcherHelperClient;
import austeretony.oxygen_core.client.gui.elements.InventoryLoadGUIElement;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButtonPanel;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIText;
import austeretony.oxygen_core.client.gui.elements.OxygenGUITextField;
import austeretony.oxygen_core.client.gui.elements.SectionsGUIDDList;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_core.common.inventory.InventoryHelper;
import austeretony.oxygen_core.common.sound.OxygenSoundEffects;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_core.server.OxygenPlayerData;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.MerchantOffer;
import net.minecraft.item.ItemStack;

public class BuyGUISection extends AbstractGUISection {

    private final MerchantMenuGUIScreen screen;

    private OxygenGUITextField searchField;

    private OxygenGUIButtonPanel offersPanel;

    private GUICurrencyBalance balanceElement;

    private InventoryLoadGUIElement inventoryLoadElement;

    public BuyGUISection(MerchantMenuGUIScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new MerchantBackgroundGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenGUIText(4, 5, this.screen.merchantProfile.getName(), GUISettings.get().getTitleScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(this.offersPanel = new OxygenGUIButtonPanel(this.screen, 6, 27, this.getWidth() - 15, 16, 1, MathUtils.clamp(this.getBuyOffersAmount(), 9, 100), 9, GUISettings.get().getPanelTextScale(), true));   
        this.addElement(this.searchField = new OxygenGUITextField(6, 17, 65, 8, 24, "...", 3, false, - 1L));
        this.offersPanel.initSearchField(this.searchField);

        this.offersPanel.<OfferGUIButton>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (mouseButton == 0 && clicked.isAvailable())
                MerchantsManagerClient.instance().getMenuManager().performBuySynced(this.screen.merchantProfile.getId(), clicked.index);
        });

        this.addElement(new SectionsGUIDDList(this.getWidth() - 4, 5, this, this.screen.getSellingSection()));

        this.addElement(this.balanceElement = new GUICurrencyBalance(this.getWidth() - 10, this.getHeight() - 10));  
        if (!this.screen.merchantProfile.isUsingCurrency())
            this.balanceElement.setItemStack(this.screen.merchantProfile.getCurrencyStack().getCachedItemStack());
        this.updateBalance();
        this.addElement(this.inventoryLoadElement = new InventoryLoadGUIElement(4, this.getHeight() - 9, EnumGUIAlignment.RIGHT));
        this.inventoryLoadElement.updateLoad();

        this.loadOffers();
    }

    private int getBuyOffersAmount() {
        return (int) this.screen.merchantProfile.getOffers()
                .stream()
                .filter((offer)->!offer.isSellingOnly())
                .count();
    }

    private void updateBalance() {
        long balance = 0L;
        if (this.screen.merchantProfile.isUsingCurrency())
            balance = WatcherHelperClient.getLong(OxygenPlayerData.CURRENCY_COINS_WATCHER_ID);
        else
            balance = InventoryHelper.getEqualStackAmount(this.mc.player, this.screen.merchantProfile.getCurrencyStack());
        this.balanceElement.setValue(balance);
        this.balanceElement.setRed(balance == 0L);
    }

    private void loadOffers() {
        List<MerchantOffer> offers = new ArrayList<>(this.screen.merchantProfile.getOffers());

        Collections.sort(offers, (o1, o2)->(int) ((o1.offerId - o2.offerId) / 5_000L));

        ItemStack currencyItemStack = null;
        if (!this.screen.merchantProfile.isUsingCurrency())
            currencyItemStack = this.screen.merchantProfile.getCurrencyStack().getItemStack();

        for (MerchantOffer offer : offers)
            if (!offer.isSellingOnly())
                this.offersPanel.addButton(new OfferGUIButton(offer, offer.getBuyCost(), this.screen.getEqualStackAmount(offer.getOfferedStack()), currencyItemStack)
                        .setAvailable(!this.inventoryLoadElement.isOverloaded() && offer.getBuyCost() <= this.balanceElement.getValue()));              
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {}

    public void bought(MerchantOffer offer, long balance) {
        this.mc.player.playSound(OxygenSoundEffects.SELL.soundEvent, 0.5F, 1.0F);

        this.balanceElement.setValue(balance);
        this.balanceElement.setRed(balance == 0L);

        if (!this.screen.merchantProfile.isUsingCurrency())
            InventoryHelper.removeEqualStack(this.mc.player, this.screen.merchantProfile.getCurrencyStack(), (int) offer.getBuyCost());
        InventoryHelper.addItemStack(this.mc.player, offer.getOfferedStack().getCachedItemStack(), offer.getAmount());
        this.inventoryLoadElement.updateLoad();
        this.screen.updateInventoryContent();

        this.updateOffers();
    }

    private void updateOffers() {
        OfferGUIButton offerButton;
        MerchantOffer offer;
        for (GUIButton button : this.offersPanel.buttonsBuffer) {
            offerButton = (OfferGUIButton) button;
            offer = this.screen.merchantProfile.getOffer(offerButton.index);
            offerButton.setAvailable(!this.inventoryLoadElement.isOverloaded() && offer.getBuyCost() <= this.balanceElement.getValue());
            if (offerButton.index == offer.offerId)
                offerButton.setPlayerStock(this.screen.getEqualStackAmount(offer.getOfferedStack()));
        }
    }

    public void sold(MerchantOffer offer, long balance) {
        this.mc.player.playSound(OxygenSoundEffects.SELL.soundEvent, 0.5F, 1.0F);

        this.balanceElement.setValue(balance);
        this.balanceElement.setRed(balance == 0L);

        if (!this.screen.merchantProfile.isUsingCurrency())
            InventoryHelper.addItemStack(this.mc.player, this.screen.merchantProfile.getCurrencyStack().getCachedItemStack(), (int) offer.getSellingCost());
        InventoryHelper.removeEqualStack(this.mc.player, offer.getOfferedStack(), offer.getAmount());
        this.inventoryLoadElement.updateLoad();
        this.screen.updateInventoryContent();

        this.updateOffers();
    }

    public GUICurrencyBalance getBalanceElement() {
        return this.balanceElement;
    }

    public InventoryLoadGUIElement getInventoryLoadElement() {
        return this.inventoryLoadElement;
    }
}
