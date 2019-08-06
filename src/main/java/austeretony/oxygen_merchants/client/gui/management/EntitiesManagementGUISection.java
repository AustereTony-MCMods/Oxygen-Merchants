package austeretony.oxygen_merchants.client.gui.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import austeretony.alternateui.screen.browsing.GUIScroller;
import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.button.GUISlider;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.contextmenu.GUIContextMenu;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.panel.GUIButtonPanel;
import austeretony.alternateui.screen.text.GUITextField;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.alternateui.util.EnumGUIOrientation;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.IndexedGUIButton;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen.util.MathUtils;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.gui.MerchantsGUITextures;
import austeretony.oxygen_merchants.client.gui.management.entities.EntitiesSectionGUIFiller;
import austeretony.oxygen_merchants.client.gui.management.entities.EntityEntryGUIButton;
import austeretony.oxygen_merchants.client.gui.management.entities.callback.EntryCreationGUICallback;
import austeretony.oxygen_merchants.client.gui.management.entities.callback.EntryEditGUICallback;
import austeretony.oxygen_merchants.client.gui.management.entities.callback.RemoveEntryGUICallback;
import austeretony.oxygen_merchants.client.gui.management.entities.callback.VisitEntityGUICallback;
import austeretony.oxygen_merchants.client.gui.management.entities.context.EditBondContextAction;
import austeretony.oxygen_merchants.client.gui.management.entities.context.RemoveBondContextAction;
import austeretony.oxygen_merchants.client.gui.management.entities.context.VisitEntityContextAction;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntitiesManagementGUISection extends AbstractGUISection {

    private final ManagementMenuGUIScreen screen;

    private GUIButton profilesSectionButton, createButton, searchButton, refreshButton, sortUpNameButton, 
    sortDownNameButton, sortUpProfileButton, sortDownProfileButton;

    private GUIButtonPanel entitiesPanel;

    private GUITextLabel entitiesAmountTextLabel;

    private GUITextField searchField;

    private AbstractGUICallback entryCreationCallback, entryEditCallback, removeEntryCallback, visitEntityCallback;

    private IndexedGUIButton currentEntryButton;

    public final Entity pointedEntity;

    public EntitiesManagementGUISection(ManagementMenuGUIScreen screen) {
        super(screen);
        this.screen = screen;
        this.pointedEntity = ClientReference.getPointedEntity();
    }

    @Override
    public void init() {
        this.addElement(new EntitiesSectionGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        String title = ClientReference.localize("oxygen_merchants.gui.management.entities.title");
        this.addElement(new GUITextLabel(2, 4).setDisplayText(title, false, GUISettings.instance().getTitleScale()));

        this.addElement(this.profilesSectionButton = new GUIButton(this.getWidth() - 28, 0, 12, 12).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(MerchantsGUITextures.LIST_ICONS, 12, 12).initSimpleTooltip(ClientReference.localize("oxygen_merchants.tooltip.profiles"), GUISettings.instance().getTooltipScale()));  
        this.addElement(new GUIButton(this.getWidth() - 14, 0, 12, 12).setTexture(MerchantsGUITextures.USER_ICONS, 12, 12).initSimpleTooltip(ClientReference.localize("oxygen_merchants.tooltip.entities"), GUISettings.instance().getTooltipScale()).toggle());    

        this.addElement(this.searchButton = new GUIButton(2, 15, 7, 7).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SEARCH_ICONS, 7, 7).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.search"), GUISettings.instance().getTooltipScale()));   
        this.addElement(this.refreshButton = new GUIButton(87, 14, 10, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.REFRESH_ICONS, 9, 9).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.refresh"), GUISettings.instance().getTooltipScale()));
        this.addElement(this.entitiesAmountTextLabel = new GUITextLabel(0, 14).setTextScale(GUISettings.instance().getSubTextScale()));   

        this.addElement(this.sortDownNameButton = new GUIButton(2, 29, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_DOWN_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortUpNameButton = new GUIButton(2, 25, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_UP_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(new GUITextLabel(7, 25).setDisplayText(ClientReference.localize("oxygen.gui.name"), false, GUISettings.instance().getSubTextScale()));

        this.addElement(this.sortDownProfileButton = new GUIButton(180, 29, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_DOWN_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(this.sortUpProfileButton = new GUIButton(180, 25, 3, 3).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).setTexture(OxygenGUITextures.SORT_UP_ICONS, 3, 3).initSimpleTooltip(ClientReference.localize("oxygen.tooltip.sort"), GUISettings.instance().getTooltipScale())); 
        this.addElement(new GUITextLabel(185, 25).setDisplayText(ClientReference.localize("oxygen_merchants.gui.management.profile"), false, GUISettings.instance().getSubTextScale()));       

        this.entitiesPanel = new GUIButtonPanel(EnumGUIOrientation.VERTICAL, 0, 35, this.getWidth() - 3, 10).setButtonsOffset(1).setTextScale(GUISettings.instance().getTextScale());
        this.addElement(this.entitiesPanel);
        this.addElement(this.searchField = new GUITextField(0, 14, 85, 9, BoundEntityEntry.MAX_NAME_LENGTH)
                .enableDynamicBackground(GUISettings.instance().getEnabledTextFieldColor(), GUISettings.instance().getDisabledTextFieldColor(), GUISettings.instance().getHoveredTextFieldColor())
                .setDisplayText("...", false, GUISettings.instance().getSubTextScale()).setLineOffset(3).cancelDraggedElementLogic().disableFull());
        this.entitiesPanel.initSearchField(this.searchField);
        GUIScroller scroller = new GUIScroller(MathUtils.clamp(MerchantsManagerClient.instance().getBoundEntitiesManager().getEntriesAmount(), 10, 100), 10);
        this.entitiesPanel.initScroller(scroller);
        GUISlider slider = new GUISlider(this.getWidth() - 2, 35, 2, 98);
        slider.setDynamicBackgroundColor(GUISettings.instance().getEnabledSliderColor(), GUISettings.instance().getDisabledSliderColor(), GUISettings.instance().getHoveredSliderColor());
        scroller.initSlider(slider);  

        this.addElement(this.createButton = new GUIButton(22, 137, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent)
                .enableDynamicBackground(GUISettings.instance().getEnabledButtonColor(), GUISettings.instance().getDisabledButtonColor(), GUISettings.instance().getHoveredButtonColor())
                .setDisplayText(ClientReference.localize("oxygen_merchants.gui.management.create"), true, GUISettings.instance().getButtonTextScale())); 

        if (this.pointedEntity == null || !(this.pointedEntity instanceof EntityLiving)) 
            this.createButton.disable();

        GUIContextMenu menu = new GUIContextMenu(GUISettings.instance().getContextMenuWidth(), 10).setScale(GUISettings.instance().getContextMenuScale()).setTextScale(GUISettings.instance().getTextScale()).setTextAlignment(EnumGUIAlignment.LEFT, 2);
        menu.setOpenSound(OxygenSoundEffects.CONTEXT_OPEN.soundEvent);
        menu.setCloseSound(OxygenSoundEffects.CONTEXT_CLOSE.soundEvent);
        this.entitiesPanel.initContextMenu(menu);
        menu.enableDynamicBackground(GUISettings.instance().getEnabledContextActionColor(), GUISettings.instance().getDisabledContextActionColor(), GUISettings.instance().getHoveredContextActionColor());
        menu.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
        menu.addElement(new EditBondContextAction(this));
        menu.addElement(new VisitEntityContextAction(this));
        menu.addElement(new RemoveBondContextAction(this));

        this.entryCreationCallback = new EntryCreationGUICallback(this.screen, this, 140, 136).enableDefaultBackground();
        this.entryEditCallback = new EntryEditGUICallback(this.screen, this, 140, 136).enableDefaultBackground();
        this.visitEntityCallback = new VisitEntityGUICallback(this.screen, this, 140, 40).enableDefaultBackground();
        this.removeEntryCallback = new RemoveEntryGUICallback(this.screen, this, 140, 40).enableDefaultBackground();
    }

    public static String getProfileName(BoundEntityEntry entry) {
        return MerchantsManagerClient.instance().getMerchantProfilesManager().getProfile(entry.getProfileId()).getName();
    }

    public void sortEntries(int mode) {
        List<BoundEntityEntry> profiles = new ArrayList<BoundEntityEntry>(MerchantsManagerClient.instance().getBoundEntitiesManager().getEntries());

        if (mode == 0)
            Collections.sort(profiles, (p1, p2)->p1.getName().compareTo(p2.getName()));
        else if (mode == 1)
            Collections.sort(profiles, (p1, p2)->p2.getName().compareTo(p1.getName()));
        else if (mode == 2)
            Collections.sort(profiles, (p1, p2)->getProfileName(p1).compareTo(getProfileName(p2)));
        else if (mode == 3)
            Collections.sort(profiles, (p1, p2)->getProfileName(p2).compareTo(getProfileName(p1)));

        this.entitiesPanel.reset();
        String profileName;
        EntityEntryGUIButton button;

        for (BoundEntityEntry entry : profiles) {
            if (entry.getProfileId() != 0L)
                profileName = MerchantsManagerClient.instance().getMerchantProfilesManager().getProfile(entry.getProfileId()).getName();
            else
                profileName = ClientReference.localize("oxygen_merchants.gui.management.emptyProfile");
            button = new EntityEntryGUIButton(entry.getId(), entry.getName() + ", " + entry.getProfession(), profileName, entry.isDead(), entry.getProfileId() == 0L);
            button.enableDynamicBackground(GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getHoveredElementColor());
            button.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
            button.setDisplayText(entry.getName());

            this.entitiesPanel.addButton(button);
        }

        this.entitiesAmountTextLabel.setDisplayText(String.valueOf(MerchantsManagerClient.instance().getBoundEntitiesManager().getEntriesAmount()));     
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
        if (this.searchField.isEnabled() && !this.searchField.isHovered()) {
            this.searchButton.enableFull();
            this.searchField.disableFull();
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);                 
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.profilesSectionButton) 
                this.screen.getProfilesSection().open();
            else if (element == this.searchButton) {
                this.searchField.enableFull();
                
                this.searchButton.disableFull();
            } else if (element == this.sortDownNameButton) {
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
                this.entryCreationCallback.open();
        }
        if (element instanceof EntityEntryGUIButton) {
            EntityEntryGUIButton button = (EntityEntryGUIButton) element;
            if (this.currentEntryButton != button)                
                this.currentEntryButton = button;
        }
    }

    public IndexedGUIButton<Long> getCurrentEntryButton() {
        return this.currentEntryButton;
    }

    public void openEntryEditCallback() {
        this.entryEditCallback.open();
    }

    public void openVisitEntityCallback() {
        this.visitEntityCallback.open();
    }

    public void openRemoveEntryCallback() {
        this.removeEntryCallback.open();
    }
}
