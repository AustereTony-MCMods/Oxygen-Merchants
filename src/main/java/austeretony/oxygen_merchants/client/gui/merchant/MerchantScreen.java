package austeretony.oxygen_merchants.client.gui.merchant;

import java.util.LinkedHashMap;
import java.util.Map;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.currency.CurrencyProperties;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.setting.gui.EnumMerchantsGUISetting;
import austeretony.oxygen_merchants.common.MerchantOffer;
import austeretony.oxygen_merchants.common.MerchantProfile;
import net.minecraft.item.ItemStack;

public class MerchantScreen extends AbstractGUIScreen {

    public final MerchantProfile merchantProfile;

    private CurrencyProperties currencyProperties;

    public final Map<ItemStackWrapper, Integer> inventoryContent = new LinkedHashMap<>();

    public final int buyOffersAmount, sellingOffersAmount;

    protected BuySection buySection;

    protected SellingSection sellingSection;

    public MerchantScreen(long profileId) {
        this.merchantProfile = MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfile(profileId);
        this.currencyProperties = OxygenHelperClient.getCurrencyProperties(this.merchantProfile.getCurrencyIndex());
        if (this.currencyProperties == null)
            this.currencyProperties = OxygenHelperClient.getCurrencyProperties(OxygenMain.COMMON_CURRENCY_INDEX);
        this.buyOffersAmount = this.merchantProfile.getBuyOffersAmount();
        this.sellingOffersAmount = this.merchantProfile.getSellingOffersAmount();

        this.updateInventoryContent();
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
        return new GUIWorkspace(this, 185, 183).setAlignment(alignment, 0, 0);
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

    public void updateInventoryContent() {
        this.inventoryContent.clear();
        ItemStackWrapper wrapper;
        int amount;
        for (ItemStack itemStack : ClientReference.getClientPlayer().inventory.mainInventory) {
            if (!itemStack.isEmpty()) {
                wrapper = ItemStackWrapper.getFromStack(itemStack);
                if (!this.inventoryContent.containsKey(wrapper))
                    this.inventoryContent.put(wrapper, itemStack.getCount());
                else {
                    amount = this.inventoryContent.get(wrapper);
                    amount += itemStack.getCount();
                    this.inventoryContent.put(wrapper, amount);
                }
            }
        }
    }

    public int getEqualStackAmount(ItemStackWrapper stackWrapper) {
        int amount = 0;
        for (ItemStackWrapper wrapper : this.inventoryContent.keySet())
            if (wrapper.isEquals(stackWrapper))
                amount += this.inventoryContent.get(wrapper);
        return amount;
    }

    public CurrencyProperties getCurrencyProperties() {
        return this.currencyProperties;
    }
}
