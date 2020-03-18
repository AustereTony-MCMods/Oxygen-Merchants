package austeretony.oxygen_merchants.client.gui.merchant;

import java.util.Map;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.InventoryProviderClient;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.currency.CurrencyProperties;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.setting.gui.EnumMerchantsGUISetting;
import austeretony.oxygen_merchants.common.merchant.MerchantOffer;
import austeretony.oxygen_merchants.common.merchant.MerchantProfile;

public class MerchantScreen extends AbstractGUIScreen {

    private final MerchantProfile merchantProfile;

    private CurrencyProperties currencyProperties;

    private final Map<ItemStackWrapper, Integer> inventoryContent;

    public final int buyOffersAmount, sellingOffersAmount;

    public final boolean debug;

    private BuySection buySection;

    private SellingSection sellingSection;

    public MerchantScreen(long profileId, boolean debug) {
        this.merchantProfile = MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfile(profileId);
        this.currencyProperties = OxygenHelperClient.getCurrencyProperties(this.merchantProfile.getCurrencyIndex());
        if (this.currencyProperties == null)
            this.currencyProperties = OxygenHelperClient.getCurrencyProperties(OxygenMain.COMMON_CURRENCY_INDEX);
        this.inventoryContent = InventoryProviderClient.getPlayerInventory().getInventoryContent(ClientReference.getClientPlayer());

        this.buyOffersAmount = this.merchantProfile.getBuyOffersAmount();
        this.sellingOffersAmount = this.merchantProfile.getSellingOffersAmount();

        this.debug = debug;
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        EnumGUIAlignment alignment = EnumGUIAlignment.CENTER;
        switch (EnumMerchantsGUISetting.MERCHANT_MENU_ALIGNMENT.get().asInt()) {
        case - 1: 
            alignment = EnumGUIAlignment.LEFT;
            break;
        case 0:
            alignment = EnumGUIAlignment.CENTER;
            break;
        case 1:
            alignment = EnumGUIAlignment.RIGHT;
            break;    
        default:
            alignment = EnumGUIAlignment.CENTER;
            break;
        }
        return new GUIWorkspace(this, 185, 191).setAlignment(alignment, 0, 0);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.buySection = (BuySection) new BuySection(this).setEnabled(this.buyOffersAmount > 0));    
        this.getWorkspace().initSection(this.sellingSection = (SellingSection) new SellingSection(this).setEnabled(this.sellingOffersAmount > 0));        
    }

    @Override
    protected AbstractGUISection getDefaultSection() {
        if (this.buyOffersAmount > 0)
            return this.buySection;
        return this.sellingSection;
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element) {}

    @Override
    protected boolean doesGUIPauseGame() {
        return false;
    }

    public void bought(MerchantOffer offer, long balance) {
        this.buySection.bought(offer, balance);
        this.sellingSection.bought(offer, balance);
    }

    public void sold(MerchantOffer offer, long balance) {
        this.buySection.sold(offer, balance);
        this.sellingSection.sold(offer, balance);
    }

    public BuySection getBuySection() {
        return this.buySection;
    }

    public SellingSection getSellingSection() {
        return this.sellingSection;
    }

    public MerchantProfile getMerchantProfile() {
        return this.merchantProfile;
    }

    public Map<ItemStackWrapper, Integer> getInventoryContent() {
        return this.inventoryContent;
    }

    public int getEqualStackAmount(ItemStackWrapper stackWrapper) {
        Integer amount = this.inventoryContent.get(stackWrapper);
        return amount == null ? 0 : amount.intValue();
    }

    public void addItemStack(ItemStackWrapper stackWrapper, int amount) {
        Integer stored = this.inventoryContent.get(stackWrapper);
        this.inventoryContent.put(stackWrapper, stored != null ? stored + amount : amount);
    }

    public void removeItemStack(ItemStackWrapper stackWrapper, int amount) {
        Integer stored = this.inventoryContent.get(stackWrapper);
        if (stored != null) {
            if (stored > amount)
                this.inventoryContent.put(stackWrapper, stored - amount);
            else
                this.inventoryContent.remove(stackWrapper);
        }
    }

    public CurrencyProperties getCurrencyProperties() {
        return this.currencyProperties;
    }
}
