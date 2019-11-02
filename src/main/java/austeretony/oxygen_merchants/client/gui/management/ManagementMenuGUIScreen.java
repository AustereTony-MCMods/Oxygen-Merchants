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
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import austeretony.oxygen_merchants.common.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import net.minecraft.item.ItemStack;

public class ManagementMenuGUIScreen extends AbstractGUIScreen {

    private ProfilesManagementGUISection profilesSection;

    private EntitiesManagementGUISection entitiesSection;

    public final List<ItemStackWrapper> inventoryContent = new ArrayList<>();

    public ManagementMenuGUIScreen() {
        OxygenHelperClient.syncData(MerchantsMain.MERCHANT_PROFILES_DATA_ID);
        OxygenHelperClient.syncData(MerchantsMain.ENTITIES_DATA_ID);

        this.updateInventoryContent();
    }

    @Override
    protected GUIWorkspace initWorkspace() {
        return new GUIWorkspace(this, 289, 149);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.profilesSection = (ProfilesManagementGUISection) new ProfilesManagementGUISection(this).setDisplayText(ClientReference.localize("oxygen_merchants.gui.management.profiles.title")).enable());   
        this.getWorkspace().initSection(this.entitiesSection = (EntitiesManagementGUISection) new EntitiesManagementGUISection(this).setDisplayText(ClientReference.localize("oxygen_merchants.gui.management.entities.title")).enable());    
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

    public void entitiesSynchronized() {
        this.entitiesSection.entitiesSynchronized();
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

    public void entityCreated(BoundEntityEntry entry) {
        this.entitiesSection.entityCreated(entry);
    }

    public void entityUpdated(BoundEntityEntry entry) {
        this.entitiesSection.entityUpdated(entry);
    }

    public void entityRemoved(BoundEntityEntry entry) {
        this.entitiesSection.entityRemoved(entry);
    }

    public ProfilesManagementGUISection getProfilesSection() {
        return this.profilesSection;
    }

    public EntitiesManagementGUISection getEntitiesSection() {
        return this.entitiesSection;
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
