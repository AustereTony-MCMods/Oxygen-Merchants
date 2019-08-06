package austeretony.oxygen_merchants.client.gui.management.entities.callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import austeretony.alternateui.screen.browsing.GUIScroller;
import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.button.GUISlider;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.panel.GUIButtonPanel;
import austeretony.alternateui.screen.text.GUITextField;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.alternateui.util.EnumGUIOrientation;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.IndexedGUIButton;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen.util.MathUtils;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.gui.management.EntitiesManagementGUISection;
import austeretony.oxygen_merchants.client.gui.management.ManagementMenuGUIScreen;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import austeretony.oxygen_merchants.common.main.MerchantProfile;

public class EntryEditGUICallback extends AbstractGUICallback {

    private final ManagementMenuGUIScreen screen;

    private final EntitiesManagementGUISection section;

    private GUITextField nameField, professionField;

    private GUIButton confirmButton, cancelButton;

    private GUIButtonPanel profilesPanel;

    private IndexedGUIButton<Long> currentProfile;

    private String oldName, oldProfession;

    public EntryEditGUICallback(ManagementMenuGUIScreen screen, EntitiesManagementGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section;
    }

    @Override
    public void init() {
        this.addElement(new EntryCreationCallbackGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new GUITextLabel(2, 2).setDisplayText(ClientReference.localize("oxygen_merchants.gui.management.entityBondEditCallback"), true, GUISettings.instance().getTitleScale())); 
        this.addElement(new GUITextLabel(2, 16).setDisplayText(ClientReference.localize("oxygen.gui.name"), false, GUISettings.instance().getSubTextScale())); 
        this.addElement(new GUITextLabel(2, 36).setDisplayText(ClientReference.localize("oxygen_merchants.gui.management.profession"), false, GUISettings.instance().getSubTextScale()));    

        this.addElement(this.nameField = new GUITextField(2, 25, 136, 9, BoundEntityEntry.MAX_NAME_LENGTH).setTextScale(GUISettings.instance().getSubTextScale()).setLineOffset(3)
                .enableDynamicBackground(GUISettings.instance().getEnabledTextFieldColor(), GUISettings.instance().getDisabledTextFieldColor(), GUISettings.instance().getHoveredTextFieldColor())
                .cancelDraggedElementLogic());
        this.addElement(this.professionField = new GUITextField(2, 45, 136, 9, BoundEntityEntry.MAX_PROFESSION_LENGTH).setTextScale(GUISettings.instance().getSubTextScale()).setLineOffset(3)
                .enableDynamicBackground(GUISettings.instance().getEnabledTextFieldColor(), GUISettings.instance().getDisabledTextFieldColor(), GUISettings.instance().getHoveredTextFieldColor())
                .cancelDraggedElementLogic());

        this.addElement(new GUITextLabel(2, 56).setDisplayText(ClientReference.localize("oxygen_merchants.gui.management.profile"), false, GUISettings.instance().getSubTextScale()));    

        this.profilesPanel = new GUIButtonPanel(EnumGUIOrientation.VERTICAL, 0, 66, 137, 10).setButtonsOffset(1).setTextScale(GUISettings.instance().getTextScale());
        this.addElement(this.profilesPanel);       
        GUIScroller scroller = new GUIScroller(MathUtils.clamp(MerchantsManagerClient.instance().getMerchantProfilesManager().getProfilesAmount(), 5, 100), 5);
        this.profilesPanel.initScroller(scroller);
        GUISlider slider = new GUISlider(this.getX() + 138, this.getY() + 66, 2, 54);
        slider.setDynamicBackgroundColor(GUISettings.instance().getEnabledSliderColor(), GUISettings.instance().getDisabledSliderColor(), GUISettings.instance().getHoveredSliderColor());
        scroller.initSlider(slider);

        this.addElement(this.confirmButton = new GUIButton(15, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.confirmButton"), true, GUISettings.instance().getButtonTextScale()).disable());
        this.addElement(this.cancelButton = new GUIButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.cancelButton"), true, GUISettings.instance().getButtonTextScale()));
    }

    private void loadProfiles() {
        List<MerchantProfile> profiles = new ArrayList<MerchantProfile>(MerchantsManagerClient.instance().getMerchantProfilesManager().getProfiles());
        Collections.sort(profiles, new Comparator<MerchantProfile>() {

            @Override
            public int compare(MerchantProfile profile1, MerchantProfile profile2) {
                return profile1.getName().compareTo(profile2.getName());
            }
        });

        this.profilesPanel.reset();

        this.profilesPanel.getScroller().resetPosition();
        this.profilesPanel.getScroller().getSlider().reset();

        ProfileCallbackGUIButton button;
        for (MerchantProfile profile : profiles) {
            button = new ProfileCallbackGUIButton(profile.getId());
            button.enableDynamicBackground(GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getEnabledElementColor(), GUISettings.instance().getHoveredElementColor());
            button.setTextDynamicColor(GUISettings.instance().getEnabledTextColor(), GUISettings.instance().getDisabledTextColor(), GUISettings.instance().getHoveredTextColor());
            button.setDisplayText(profile.getName());
            button.setTextAlignment(EnumGUIAlignment.LEFT, 1);

            this.profilesPanel.addButton(button);            
        }
    }

    @Override
    protected void onOpen() {
        BoundEntityEntry entry = MerchantsManagerClient.instance().getBoundEntitiesManager().getBoundEntityEntry(this.section.getCurrentEntryButton().index);
        this.nameField.setText(this.oldName = entry.getName());
        this.professionField.setText(this.oldProfession = entry.getProfession());

        this.loadProfiles();

        if (this.currentProfile != null) {
            this.currentProfile.setToggled(false);
            this.currentProfile = null;
        }
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) { 
            if (element == this.cancelButton)
                this.close();
            else if (element == this.confirmButton) {
                if (this.currentProfile != null) {
                    String 
                    name = this.nameField.getTypedText().isEmpty() ? this.oldName : this.nameField.getTypedText(),
                            profession = this.professionField.getTypedText().isEmpty() ? this.oldProfession : this.professionField.getTypedText();
                    MerchantsManagerClient.instance().getBoundEntitiesManager().editEntrySynced(this.section.getCurrentEntryButton().index, name, profession, this.currentProfile.index);
                    this.section.sortEntries(0);
                    this.close();
                }
            } else if (element instanceof ProfileCallbackGUIButton) {
                ProfileCallbackGUIButton button = (ProfileCallbackGUIButton) element;
                if (this.currentProfile != button) {
                    if (this.currentProfile != null)
                        this.currentProfile.setToggled(false);
                    button.toggle();                    
                    this.currentProfile = button;

                    this.confirmButton.enable();
                }
            }
        }
    }
}
