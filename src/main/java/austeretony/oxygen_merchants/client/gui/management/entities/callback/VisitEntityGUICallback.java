package austeretony.oxygen_merchants.client.gui.management.entities.callback;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.text.GUITextLabel;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.common.main.OxygenSoundEffects;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.gui.management.EntitiesManagementGUISection;
import austeretony.oxygen_merchants.client.gui.management.ManagementMenuGUIScreen;

public class VisitEntityGUICallback extends AbstractGUICallback {

    private final ManagementMenuGUIScreen screen;

    private final EntitiesManagementGUISection section;

    private GUITextLabel requestLabel;

    private GUIButton confirmButton, cancelButton;

    public VisitEntityGUICallback(ManagementMenuGUIScreen screen, EntitiesManagementGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section;
    }

    @Override
    public void init() {
        this.addElement(new VisitEntityCallbackGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new GUITextLabel(2, 2).setDisplayText(ClientReference.localize("oxygen_merchants.gui.management.visitEntityCallback"), true, GUISettings.instance().getTitleScale()));

        this.addElement(this.requestLabel = new GUITextLabel(2, 16));     

        this.addElement(this.confirmButton = new GUIButton(15, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.confirmButton"), true, GUISettings.instance().getButtonTextScale()));
        this.addElement(this.cancelButton = new GUIButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10).setSound(OxygenSoundEffects.BUTTON_CLICK.soundEvent).enableDynamicBackground().setDisplayText(ClientReference.localize("oxygen.gui.cancelButton"), true, GUISettings.instance().getButtonTextScale()));
    }

    @Override   
    protected void onOpen() {
        this.requestLabel.setDisplayText(ClientReference.localize("oxygen_merchants.gui.management.visitEntityCallback.request", 
                MerchantsManagerClient.instance().getBoundEntitiesManager().getBoundEntityEntry(this.section.getCurrentEntryButton().index).getName()), false, GUISettings.instance().getTextScale());
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {      
        if (mouseButton == 0) { 
            if (element == this.cancelButton)
                this.close();
            else if (element == this.confirmButton) {
                MerchantsManagerClient.instance().getBoundEntitiesManager().visitEntitySynced(this.section.getCurrentEntryButton().index);
                this.close();
                this.screen.close();
            }
        }
    }
}