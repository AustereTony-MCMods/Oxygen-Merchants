package austeretony.oxygen_merchants.client.gui.management.entities.callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.IndexedGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenCallbackGUIFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButtonPanel;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIText;
import austeretony.oxygen_core.client.gui.elements.OxygenGUITextField;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.gui.management.EntitiesManagementGUISection;
import austeretony.oxygen_merchants.client.gui.management.ManagementMenuGUIScreen;
import austeretony.oxygen_merchants.client.gui.management.ProfileGUIButton;
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import austeretony.oxygen_merchants.common.MerchantProfile;

public class EntryEditGUICallback extends AbstractGUICallback {

    private final ManagementMenuGUIScreen screen;

    private final EntitiesManagementGUISection section;

    private OxygenGUITextField nameField, professionField;

    private OxygenGUIButton confirmButton, cancelButton;

    private OxygenGUIButtonPanel profilesPanel;

    //cache

    private IndexedGUIButton<Long> currentProfile;

    private String oldName, oldProfession;

    public EntryEditGUICallback(ManagementMenuGUIScreen screen, EntitiesManagementGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section;
    }

    @Override
    public void init() {
        this.addElement(new OxygenCallbackGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenGUIText(4, 5, ClientReference.localize("oxygen_merchants.gui.management.callback.entityEdit"), GUISettings.get().getTextScale(), GUISettings.get().getEnabledTextColor()));
        this.addElement(new OxygenGUIText(6, 18, ClientReference.localize("oxygen.gui.name"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColor()));
        this.addElement(new OxygenGUIText(6, 38, ClientReference.localize("oxygen_merchants.gui.management.profession"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(this.nameField = new OxygenGUITextField(6, 25, this.getWidth() - 12, 9, BoundEntityEntry.MAX_NAME_LENGTH, "", 3, false, - 1L));
        this.addElement(this.professionField = new OxygenGUITextField(6, 45, this.getWidth() - 12, 9, BoundEntityEntry.MAX_PROFESSION_LENGTH, "", 3, false, - 1L));

        this.addElement(new OxygenGUIText(6, 58, ClientReference.localize("oxygen_merchants.gui.management.profile"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColor()));
        this.addElement(this.profilesPanel = new OxygenGUIButtonPanel(this.screen, 6, 66, this.getWidth() - 15, 10, 1, MathUtils.clamp(MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfilesAmount(), 5, 100), 10, GUISettings.get().getPanelTextScale(), false));        

        this.profilesPanel.<IndexedGUIButton<Long>>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (this.currentProfile != clicked) {
                if (this.currentProfile != null)
                    this.currentProfile.setToggled(false);
                clicked.toggle();                    
                this.currentProfile = clicked;

                this.confirmButton.enable();
            }
        });

        this.addElement(this.confirmButton = new OxygenGUIButton(15, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen.gui.confirmButton")));
        this.addElement(this.cancelButton = new OxygenGUIButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen.gui.cancelButton")));
    }

    private void loadProfiles() {
        List<MerchantProfile> profiles = new ArrayList<MerchantProfile>(MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfiles());

        Collections.sort(profiles, (p1, p2)->p1.getName().compareTo(p2.getName()));

        this.profilesPanel.reset();

        this.profilesPanel.getScroller().resetPosition();
        this.profilesPanel.getScroller().getSlider().reset();

        for (MerchantProfile profile : profiles)
            this.profilesPanel.addButton(new ProfileGUIButton(profile));
    }

    @Override
    protected void onOpen() {
        BoundEntityEntry entry = MerchantsManagerClient.instance().getBoundEntitiesContainer().getBoundEntityEntry(this.section.getCurrentEntryButton().index);
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
                            profession = this.professionField.getTypedText();
                    MerchantsManagerClient.instance().getBoundEntitiesManager().editEntrySynced(this.section.getCurrentEntryButton().index, name, profession, this.currentProfile.index);
                    this.close();
                }
            }
        }
    }
}
