package austeretony.oxygen_merchants.client.gui.management;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.oxygen.common.api.OxygenGUIHelper;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import net.minecraft.util.ResourceLocation;

public class ManagementMenuGUIScreen extends AbstractGUIScreen {

    public static final ResourceLocation 
    PROFILES_SECTION_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/profiles_background.png"),
    CURRENCY_MANAGEMENT_CALLBACK_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/currency_management_callback.png"),
    OFFER_CREATION_CALLBACK_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/offer_creation_callback.png"),
    ENTITIES_SECTION_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/entities_background.png");

    private ProfilesManagementGUISection profilesSection;

    private EntitiesManagementGUISection entitiesSection;

    private boolean profilesInitialized, entitiesInitialized;

    @Override
    protected GUIWorkspace initWorkspace() {
        return new GUIWorkspace(this, 289, 149);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.profilesSection = new ProfilesManagementGUISection(this));   
        this.getWorkspace().initSection(this.entitiesSection = new EntitiesManagementGUISection(this));        
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

    @Override
    public void updateScreen() {    
        super.updateScreen();
        if (!this.profilesInitialized//reduce map calls
                && OxygenGUIHelper.isNeedSync(MerchantsMain.PROFILES_MANAGEMENT_MENU_SCREEN_ID)
                && OxygenGUIHelper.isScreenInitialized(MerchantsMain.PROFILES_MANAGEMENT_MENU_SCREEN_ID)
                && OxygenGUIHelper.isDataRecieved(MerchantsMain.PROFILES_MANAGEMENT_MENU_SCREEN_ID)) {
            this.profilesInitialized = true;
            OxygenGUIHelper.resetNeedSync(MerchantsMain.PROFILES_MANAGEMENT_MENU_SCREEN_ID);
            this.profilesSection.sortProfiles(0);
        }

        if (!this.entitiesInitialized//reduce map calls
                && OxygenGUIHelper.isNeedSync(MerchantsMain.ENTITIES_MANAGEMENT_MENU_SCREEN_ID)
                && OxygenGUIHelper.isScreenInitialized(MerchantsMain.ENTITIES_MANAGEMENT_MENU_SCREEN_ID)
                && OxygenGUIHelper.isDataRecieved(MerchantsMain.ENTITIES_MANAGEMENT_MENU_SCREEN_ID)) {
            this.entitiesInitialized = true;
            OxygenGUIHelper.resetNeedSync(MerchantsMain.ENTITIES_MANAGEMENT_MENU_SCREEN_ID);
            this.entitiesSection.sortEntries(0);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        OxygenGUIHelper.resetNeedSync(MerchantsMain.PROFILES_MANAGEMENT_MENU_SCREEN_ID);
        OxygenGUIHelper.resetScreenInitialized(MerchantsMain.PROFILES_MANAGEMENT_MENU_SCREEN_ID);
        OxygenGUIHelper.resetDataRecieved(MerchantsMain.PROFILES_MANAGEMENT_MENU_SCREEN_ID);

        OxygenGUIHelper.resetNeedSync(MerchantsMain.ENTITIES_MANAGEMENT_MENU_SCREEN_ID);
        OxygenGUIHelper.resetScreenInitialized(MerchantsMain.ENTITIES_MANAGEMENT_MENU_SCREEN_ID);
        OxygenGUIHelper.resetDataRecieved(MerchantsMain.ENTITIES_MANAGEMENT_MENU_SCREEN_ID);
    }

    public ProfilesManagementGUISection getProfilesSection() {
        return this.profilesSection;
    }

    public EntitiesManagementGUISection getEntitiesSection() {
        return this.entitiesSection;
    }
}
