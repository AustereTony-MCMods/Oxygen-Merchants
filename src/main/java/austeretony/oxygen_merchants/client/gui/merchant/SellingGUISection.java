package austeretony.oxygen_merchants.client.gui.merchant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.gui.elements.CurrencyItemValueGUIElement;
import austeretony.oxygen_core.client.gui.elements.InventoryLoadGUIElement;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButtonPanel;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIText;
import austeretony.oxygen_core.client.gui.elements.OxygenGUITextField;
import austeretony.oxygen_core.client.gui.elements.SectionsGUIDDList;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.MerchantOffer;
import net.minecraft.item.ItemStack;

public class SellingGUISection extends AbstractGUISection {

    private final MerchantMenuGUIScreen screen;

    private OxygenGUITextField searchField;

    private OxygenGUIButtonPanel offersPanel;

    private CurrencyItemValueGUIElement balanceElement;

    private InventoryLoadGUIElement inventoryLoadElement;

    public SellingGUISection(MerchantMenuGUIScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new MerchantBackgroundGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenGUIText(4, 5, this.screen.merchantProfile.getName(), GUISettings.get().getTitleScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(this.offersPanel = new OxygenGUIButtonPanel(this.screen, 6, 27, this.getWidth() - 15, 16, 1, MathUtils.clamp(this.getSellingOffersAmount(), 9, 100), 9, GUISettings.get().getPanelTextScale(), true));   
        this.addElement(this.searchField = new OxygenGUITextField(6, 17, 65, 8, 24, "...", 3, false, - 1L));
        this.offersPanel.initSearchField(this.searchField);

        this.offersPanel.<OfferGUIButton>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (mouseButton == 0 && clicked.isAvailable()) 
                MerchantsManagerClient.instance().getMenuManager().performSellingSynced(this.screen.merchantProfile.getId(), clicked.index);
        });

        this.addElement(new SectionsGUIDDList(this.getWidth() - 4, 5, this, this.screen.getBuySection()));

        this.addElement(this.balanceElement = new CurrencyItemValueGUIElement(this.getWidth() - 10, this.getHeight() - 10));   
        if (!this.screen.merchantProfile.isUsingCurrency())
            this.balanceElement.setItemStack(this.screen.merchantProfile.getCurrencyStack().getItemStack());
        this.balanceElement.setValue(this.screen.getBuySection().getBalanceElement().getValue());
        this.balanceElement.setRed(this.balanceElement.getValue() == 0L);
        this.addElement(this.inventoryLoadElement = new InventoryLoadGUIElement(4, this.getHeight() - 9, EnumGUIAlignment.RIGHT));
        this.inventoryLoadElement.setLoad(this.screen.getBuySection().getInventoryLoadElement().getLoad());

        this.loadOffers();
    }

    private int getSellingOffersAmount() {
        return (int) this.screen.merchantProfile.getOffers()
                .stream()
                .filter((offer)->offer.isSellingEnabled())
                .count();
    }

    private void loadOffers() {
        List<MerchantOffer> offers = new ArrayList<>(this.screen.merchantProfile.getOffers());

        Collections.sort(offers, (o1, o2)->(int) ((o1.offerId - o2.offerId) / 5_000L));

        ItemStack currencyItemStack = null;
        if (!this.screen.merchantProfile.isUsingCurrency())
            currencyItemStack = this.screen.merchantProfile.getCurrencyStack().getItemStack();

        int stock;
        for (MerchantOffer offer : offers) {
            if (offer.isSellingEnabled()) {
                stock = this.screen.getEqualStackAmount(offer.getOfferedStack());
                this.offersPanel.addButton(new OfferGUIButton(offer, offer.getSellingCost(), stock, currencyItemStack)
                        .setAvailable((this.screen.merchantProfile.isUsingCurrency() || !this.inventoryLoadElement.isOverloaded()) && stock >= offer.getAmount()));        
            }
        }
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {}

    public void bought(MerchantOffer offer, long balance) {
        this.balanceElement.setValue(balance);
        this.balanceElement.setRed(balance == 0L);

        this.inventoryLoadElement.setLoad(this.screen.getBuySection().getInventoryLoadElement().getLoad());

        this.updateOffers();
    }

    private void updateOffers() {
        int stock;       
        OfferGUIButton offerButton;
        MerchantOffer offer;
        for (GUIButton button : this.offersPanel.buttonsBuffer) {
            offerButton = (OfferGUIButton) button;
            offer = this.screen.merchantProfile.getOffer(offerButton.index);
            stock = this.screen.getEqualStackAmount(offer.getOfferedStack());  
            offerButton.setAvailable((this.screen.merchantProfile.isUsingCurrency() || !this.inventoryLoadElement.isOverloaded()) && stock >= offer.getAmount());
            offerButton.setPlayerStock(stock);
        }
    }

    public void sold(MerchantOffer offer, long balance) {
        this.balanceElement.setValue(balance);
        this.balanceElement.setRed(balance == 0L);

        this.inventoryLoadElement.setLoad(this.screen.getBuySection().getInventoryLoadElement().getLoad());

        this.updateOffers();
    }
}
