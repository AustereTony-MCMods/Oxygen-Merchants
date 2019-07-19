package austeretony.oxygen_merchants.client.gui.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import austeretony.alternateui.screen.browsing.GUIScroller;
import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.button.GUISlider;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.contextmenu.GUIContextMenu;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.panel.GUIButtonPanel;
import austeretony.alternateui.screen.panel.GUIButtonPanel.GUIEnumOrientation;
import austeretony.alternateui.screen.text.GUITextField;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.IndexedGUIButton;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.api.OxygenGUIHelper;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen.util.MathUtils;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.gui.MerchantsGUITextures;
import austeretony.oxygen_merchants.client.gui.management.entities.EntitiesSectionGUIFiller;
import austeretony.oxygen_merchants.client.gui.management.entities.EntityEntryGUIButton;
import austeretony.oxygen_merchants.client.gui.management.entities.callback.BondCreationGUICallback;
import austeretony.oxygen_merchants.client.gui.management.entities.callback.BondEditGUICallback;
import austeretony.oxygen_merchants.client.gui.management.entities.callback.BondRemoveGUICallback;
import austeretony.oxygen_merchants.client.gui.management.entities.callback.DownloadingGUICallback;
import austeretony.oxygen_merchants.client.gui.management.entities.callback.VisitEntityGUICallback;
import austeretony.oxygen_merchants.client.gui.management.entities.context.EditBondContextAction;
import austeretony.oxygen_merchants.client.gui.management.entities.context.RemoveBondContextAction;
import austeretony.oxygen_merchants.client.gui.management.entities.context.VisitEntityContextAction;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import austeretony.oxygen_merchants.common.main.MerchantProfile;
import austeretony.oxygen_merchants.common.main.MerchantsMain;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLiving;

public class EntitiesManagementGUISection extends AbstractGUISection {

    private final ManagementMenuGUIScreen screen;

    private GUIButton downloadButton, profilesSectionButton, createButton, searchButton, refreshButton, sortUpNameButton, 
    sortDownNameButton, sortUpProfileButton, sortDownProfileButton;

    private GUIButtonPanel entitiesPanel;

    private GUITextLabel entitiesAmountTextLabel;

    private GUITextField searchField;

    private AbstractGUICallback downloadingCallback, bondCreationCallback, bondEditCallback, bondRemoveCallback, visitEntityCallback;

    private IndexedGUIButton currentBondButton;

    public EntitiesManagementGUISection(ManagementMenuGUIScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new EntitiesSectionGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        String title = I18n.format("merchants.gui.management.entities.title");
        this.addElement(new GUITextLabel(2, 4).setDisplayText(title, false, GUISettings.instance().getTitleScale()));
        this.addElement(this.downloadButton = new GUIButton(this.textWidth(title, GUISettings.instance().getTitleScale()) + 4, 4, 8, 8).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.DOWNLOAD_ICONS, 8, 8).initSimpleTooltip(I18n.format("oxygen.tooltip.download"), GUISettings.instance().getTooltipScale()));

        this.addElement(this.profilesSectionButton = new GUIButton(this.getWidth() - 28, 0, 12, 12).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(MerchantsGUITextures.LIST_ICONS, 12, 12).initSimpleTooltip(I18n.format("merchants.tooltip.profiles"), GUISettings.instance().getTooltipScale()));  
        this.addElement(new GUIButton(this.getWidth() - 14, 0, 12, 12).setTexture(MerchantsGUITextures.USER_ICONS, 12, 12).initSimpleTooltip(I18n.format("merchants.tooltip.entities"), GUISettings.instance().getTooltipScale()).toggle());    

        this.addElement(this.searchButton = new GUIButton(2, 15, 7, 7).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SEARCH_ICONS, 7, 7).initSimpleTooltip(I18n.format("oxygen.tooltip.search"), GUISettings.instance().getTooltipScale()));   
        this.addElement(this.refreshButton = new GUIButton(87, 14, 10, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.REFRESH_ICONS, 9, 9).initSimpleTooltip(I18n.format("oxygen.tooltip.refresh"), GUISettings.instance().getTooltipScale()));
        this.addElement(this.entitiesAmountTextLabel = new GUITextLabel(0, 14).setTextScale(GUISettings.instance().getSubTextScale()));   

        this.addElement(this.sortDownNameButton = new GUIButton(2, 29, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_DOWN_ICONS, 3, 3).initSimpleTooltip(I18n.format("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortUpNameButton = new GUIButton(2, 25, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_UP_ICONS, 3, 3).initSimpleTooltip(I18n.format("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(new GUITextLabel(7, 25).setDisplayText(I18n.format("oxygen.gui.name"), false, GUISettings.instance().getTextScale()));

        this.addElement(this.sortDownProfileButton = new GUIButton(180, 29, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_DOWN_ICONS, 3, 3).initSimpleTooltip(I18n.format("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortUpProfileButton = new GUIButton(180, 25, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_UP_ICONS, 3, 3).initSimpleTooltip(I18n.format("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(new GUITextLabel(185, 25).setDisplayText(I18n.format("merchants.gui.management.profile"), false, GUISettings.instance().getTextScale()));       

        this.entitiesPanel = new GUIButtonPanel(GUIEnumOrientation.VERTICAL, 0, 35, this.getWidth() - 3, 10).setButtonsOffset(1).setTextScale(GUISettings.instance().getTextScale());
        this.addElement(this.entitiesPanel);
        this.addElement(this.searchField = new GUITextField(0, 15, 113, BoundEntityEntry.MAX_NAME_LENGTH).setScale(0.7F).enableDynamicBackground().setDisplayText("...", false, GUISettings.instance().getTextScale()).cancelDraggedElementLogic().disableFull());
        this.entitiesPanel.initSearchField(this.searchField);
        GUIScroller scroller = new GUIScroller(MathUtils.clamp(MerchantsManagerClient.instance().getBoundEntitiesManager().getBondsAmount(), 10, 100), 10);
        this.entitiesPanel.initScroller(scroller);
        GUISlider slider = new GUISlider(this.getWidth() - 2, 35, 2, 98);
        slider.setDynamicBackgroundColor(GUISettings.instance().getEnabledSliderColor(), GUISettings.instance().getDisabledSliderColor(), GUISettings.instance().getHoveredSliderColor());
        scroller.initSlider(slider);  

        this.addElement(this.createButton = new GUIButton(22, 137, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(I18n.format("merchants.gui.management.create"), true, GUISettings.instance().getButtonTextScale())); 

        if (MerchantsManagerClient.instance().getPointedEntity() == null || !(MerchantsManagerClient.instance().getPointedEntity() instanceof EntityLiving)) 
            this.createButton.disable();

        //Protection
        if (!OxygenGUIHelper.isNeedSync(MerchantsMain.ENTITIES_MANAGEMENT_MENU_SCREEN_ID) || OxygenGUIHelper.isDataRecieved(MerchantsMain.ENTITIES_MANAGEMENT_MENU_SCREEN_ID))
            this.sortEntries(0);

        GUIContextMenu menu = new GUIContextMenu(GUISettings.instance().getContextMenuWidth(), 10).setScale(GUISettings.instance().getContextMenuScale()).setTextScale(GUISettings.instance().getTextScale()).setTextAlignment(EnumGUIAlignment.LEFT, 2);
        menu.setOpenSound(OxygenSoundEffects.CONTEXT_OPEN.soundEvent);
        menu.setCloseSound(OxygenSoundEffects.CONTEXT_CLOSE.soundEvent);
        this.entitiesPanel.initContextMenu(menu);
        menu.enableDynamicBackground(GUISettings.instance().getEnabledContextActionColor(), GUISettings.instance().getDisabledContextActionColor(), GUISettings.instance().getHoveredContextActionColor());
        menu.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
        menu.addElement(new EditBondContextAction(this));
        menu.addElement(new VisitEntityContextAction(this));
        menu.addElement(new RemoveBondContextAction(this));

        this.downloadingCallback = new DownloadingGUICallback(this.screen, this, 140, 40).enableDefaultBackground();

        this.bondCreationCallback = new BondCreationGUICallback(this.screen, this, 140, 134).enableDefaultBackground();
        this.bondEditCallback = new BondEditGUICallback(this.screen, this, 140, 134).enableDefaultBackground();
        this.visitEntityCallback = new VisitEntityGUICallback(this.screen, this, 140, 40).enableDefaultBackground();
        this.bondRemoveCallback = new BondRemoveGUICallback(this.screen, this, 140, 40).enableDefaultBackground();

        OxygenGUIHelper.screenInitialized(MerchantsMain.ENTITIES_MANAGEMENT_MENU_SCREEN_ID);
    }

    public void sortEntries(int mode) {
        List<BoundEntityEntry> profiles = new ArrayList<BoundEntityEntry>(MerchantsManagerClient.instance().getBoundEntitiesManager().getBonds());
        if (mode == 0 || mode == 1) {//by entry name
            Collections.sort(profiles, new Comparator<BoundEntityEntry>() {

                @Override
                public int compare(BoundEntityEntry entry1, BoundEntityEntry entry2) {
                    if (mode == 0)
                        return entry1.getName().compareTo(entry2.getName());
                    else
                        return entry2.getName().compareTo(entry1.getName());
                }
            });
        } else {//2 - 3, by merchant profile name
            Collections.sort(profiles, new Comparator<BoundEntityEntry>() {

                @Override
                public int compare(BoundEntityEntry entry1, BoundEntityEntry entry2) {
                    MerchantProfile 
                    profile1 = MerchantsManagerClient.instance().getMerchantProfilesManager().getProfile(entry1.getProfileId()),
                    profile2 = MerchantsManagerClient.instance().getMerchantProfilesManager().getProfile(entry2.getProfileId());
                    if (mode == 2)
                        return profile1.getName().compareTo(profile2.getName());
                    else
                        return profile2.getName().compareTo(profile1.getName());
                }
            });
        }

        this.entitiesPanel.reset();
        String profileName;
        EntityEntryGUIButton button;

        for (BoundEntityEntry entry : profiles) {
            if (entry.getProfileId() != 0L)
                profileName = MerchantsManagerClient.instance().getMerchantProfilesManager().getProfile(entry.getProfileId()).getName();
            else
                profileName = ClientReference.localize("merchants.gui.management.emptyProfile");
            button = new EntityEntryGUIButton(entry.getId(), entry.getName() + ", " + entry.getProfession(), profileName, entry.isDead(), entry.getProfileId() == 0L);
            button.enableDynamicBackground(GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getHoveredElementColor());
            button.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
            button.setDisplayText(entry.getName());

            this.entitiesPanel.addButton(button);
        }

        this.entitiesAmountTextLabel.setDisplayText(String.valueOf(MerchantsManagerClient.instance().getBoundEntitiesManager().getBondsAmount()));     
        this.entitiesAmountTextLabel.setX(this.getWidth() - 4 - this.textWidth(this.entitiesAmountTextLabel.getDisplayText(), GUISettings.instance().getSubTextScale()));

        this.entitiesPanel.getScroller().resetPosition();
        this.entitiesPanel.getScroller().getSlider().reset();

        this.sortUpNameButton.toggle();
        this.sortDownNameButton.setToggled(false);

        this.sortUpProfileButton.setToggled(false);
        this.sortDownProfileButton.setToggled(false);
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
            if (element == this.downloadButton)
                this.downloadingCallback.open();
            else if (element == this.profilesSectionButton) 
                this.screen.getProfilesSection().open();
            else if (element == this.searchButton)
                this.searchField.enableFull();
            else if (element == this.sortDownNameButton) {
                if (!this.sortDownNameButton.isToggled()) {
                    this.sortEntries(1);
                    this.sortUpNameButton.setToggled(false);
                    this.sortDownNameButton.toggle(); 

                    this.sortDownProfileButton.setToggled(false);
                    this.sortUpProfileButton.setToggled(false);
                }
            } else if (element == this.sortUpNameButton) {
                if (!this.sortUpNameButton.isToggled()) {
                    this.sortEntries(0);
                    this.sortDownNameButton.setToggled(false);
                    this.sortUpNameButton.toggle();

                    this.sortDownProfileButton.setToggled(false);
                    this.sortUpProfileButton.setToggled(false);
                }
            } else if (element == this.sortDownProfileButton) {
                if (!this.sortDownProfileButton.isToggled()) {
                    this.sortEntries(3);
                    this.sortUpProfileButton.setToggled(false);
                    this.sortDownProfileButton.toggle(); 

                    this.sortDownNameButton.setToggled(false);
                    this.sortUpNameButton.setToggled(false);
                }
            } else if (element == this.sortUpProfileButton) {
                if (!this.sortUpProfileButton.isToggled()) {
                    this.sortEntries(2);
                    this.sortDownProfileButton.setToggled(false);
                    this.sortUpProfileButton.toggle();

                    this.sortDownNameButton.setToggled(false);
                    this.sortUpNameButton.setToggled(false);
                }
            } else if (element == this.refreshButton) {
                this.searchField.reset();
                this.sortEntries(0);
            } else if (element == this.createButton)
                this.bondCreationCallback.open();
        }
        if (element instanceof EntityEntryGUIButton) {
            EntityEntryGUIButton button = (EntityEntryGUIButton) element;
            if (this.currentBondButton != button)                
                this.currentBondButton = button;
        }
    }

    public IndexedGUIButton getCurrentBondButton() {
        return this.currentBondButton;
    }

    public void openBondEditCallback() {
        this.bondEditCallback.open();
    }

    public void openVisitEntityCallback() {
        this.visitEntityCallback.open();
    }

    public void openBondRemoveCallback() {
        this.bondRemoveCallback.open();
    }
}
