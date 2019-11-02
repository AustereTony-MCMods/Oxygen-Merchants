package austeretony.oxygen_merchants.client.gui.merchant;

import java.util.LinkedHashMap;
import java.util.Map;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.MerchantOffer;
import austeretony.oxygen_merchants.common.MerchantProfile;
import net.minecraft.item.ItemStack;

public class MerchantMenuGUIScreen extends AbstractGUIScreen {

    public final MerchantProfile merchantProfile;

    protected BuyGUISection buySection;

    protected SellingGUISection sellingSection;

    public final Map<ItemStackWrapper, Integer> inventoryContent = new LinkedHashMap<>();

    public MerchantMenuGUIScreen(long profileId) {
        this.merchantProfile = MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfile(profileId);
        this.updateInventoryContent();
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        return new GUIWorkspace(this, 173, 195).setAlignment(EnumGUIAlignment.RIGHT, - 10, 0);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.buySection = (BuyGUISection) new BuyGUISection(this).setDisplayText(ClientReference.localize("oxygen_merchants.gui.merchant.buy")).enable());    
        this.getWorkspace().initSection(this.sellingSection = (SellingGUISection) new SellingGUISection(this).setDisplayText(ClientReference.localize("oxygen_merchants.gui.merchant.selling")).enable());        
    }

    @Override
    protected AbstractGUISection getDefaultSection() {
        return this.buySection;
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

    public BuyGUISection getBuySection() {
        return this.buySection;
    }

    public SellingGUISection getSellingSection() {
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
}
