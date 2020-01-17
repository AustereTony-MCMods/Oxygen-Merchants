package austeretony.oxygen_merchants.client.gui.management;

import java.util.ArrayList;
import java.util.List;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_merchants.common.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import net.minecraft.item.ItemStack;

public class ManagementScreen extends AbstractGUIScreen {

    private MerchantProfilesSection profilesSection;

    public final List<ItemStackWrapper> inventoryContent = new ArrayList<>();

    public ManagementScreen() {
        OxygenHelperClient.syncData(MerchantsMain.MERCHANT_PROFILES_DATA_ID);

        this.updateInventoryContent();
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        return new GUIWorkspace(this, 289, 149);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.profilesSection = (MerchantProfilesSection) new MerchantProfilesSection(this).enable());   
    }

    @Override
    protected AbstractGUISection getDefaultSection() {
        return this.profilesSection;
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element) {}

    @Override
    protected boolean doesGUIPauseGame() {
        return false;
    }

    public void profilesSynchronized() {
        this.profilesSection.profilesSynchronized();
    }

    public void profileCreated(MerchantProfile profile) {
        this.profilesSection.profileCreated(profile);
    }

    public void profileUpdated(MerchantProfile profile) {
        this.profilesSection.profileUpdated(profile);
    }

    public void profileRemoved(MerchantProfile profile) {
        this.profilesSection.profileRemoved(profile);
    }

    public void updateInventoryContent() {
        this.inventoryContent.clear();
        ItemStackWrapper wrapper;
        for (ItemStack itemStack : ClientReference.getClientPlayer().inventory.mainInventory) {
            if (!itemStack.isEmpty()) {
                wrapper = ItemStackWrapper.getFromStack(itemStack);
                if (!this.inventoryContent.contains(wrapper))
                    this.inventoryContent.add(wrapper);
            }
        }
    }
}
