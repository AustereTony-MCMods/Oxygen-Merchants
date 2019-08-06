package austeretony.oxygen_merchants.client.gui.management;

import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.oxygen.client.gui.SynchronizedGUIScreen;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import net.minecraft.util.ResourceLocation;

public class ManagementMenuGUIScreen extends SynchronizedGUIScreen {

    public static final ResourceLocation 
    PROFILES_MENU_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/profiles_menu.png"),
    PROFILE_CREATION_CALLBACK_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/profile_creation_callback.png"),
    REMOVE_PROFILE_CALLBACK_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/remove_profile_callback.png"),
    PROFILE_NAME_EDIT_CALLBACK_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/profile_name_edit_callback.png"),
    CURRENCY_MANAGEMENT_CALLBACK_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/currency_management_callback.png"),
    OFFER_CREATION_CALLBACK_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/offer_creation_callback.png"),
    SAVE_CHANGES_CALLBACK_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/save_changes_callback.png"),

    ENTITIES_MENU_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/entities_menu.png"),
    ENTRY_CREATION_CALLBACK_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/entry_creation_callback.png"),
    VISIT_ENTITY_CALLBACK_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/visit_entity_callback.png"),
    REMOVE_ENTRY_CALLBACK_BACKGROUND = new ResourceLocation(MerchantsMain.MODID, "textures/gui/management/remove_entry_callback.png");

    private ProfilesManagementGUISection profilesSection;

    private EntitiesManagementGUISection entitiesSection;

    public ManagementMenuGUIScreen() {
        super(MerchantsMain.MANAGEMENT_MENU_SCREEN_ID);
    }

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
    public void loadData() {
        this.profilesSection.sortProfiles(0);
        this.entitiesSection.sortEntries(0);
    }

    public ProfilesManagementGUISection getProfilesSection() {
        return this.profilesSection;
    }

    public EntitiesManagementGUISection getEntitiesSection() {
        return this.entitiesSection;
    }
}
