package austeretony.oxygen_merchants.client.gui.management.entities.callback;

import austeretony.oxygen.client.gui.BackgroundGUIFiller;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen_merchants.client.gui.management.ManagementMenuGUIScreen;

public class EntryCreationCallbackGUIFiller extends BackgroundGUIFiller {

    public EntryCreationCallbackGUIFiller(int xPosition, int yPosition, int width, int height) {             
        super(xPosition, yPosition, width, height, ManagementMenuGUIScreen.ENTRY_CREATION_CALLBACK_BACKGROUND);
    }

    @Override
    public void drawDefaultBackground() {
        drawRect(- 1, - 1, this.getWidth() + 1, this.getHeight() + 1, GUISettings.instance().getBaseGUIBackgroundColor());//main background
        drawRect(0, 0, this.getWidth(), 11, GUISettings.instance().getAdditionalGUIBackgroundColor());//title background
        drawRect(0, 12, this.getWidth(), 65, GUISettings.instance().getAdditionalGUIBackgroundColor());//fields background
        drawRect(0, 66, this.getWidth() - 3, 120, GUISettings.instance().getPanelGUIBackgroundColor());//panel background
        drawRect(this.getWidth() - 2, 66, this.getWidth(), 120, GUISettings.instance().getAdditionalGUIBackgroundColor());//slider background
        drawRect(0, 121, this.getWidth(), this.getHeight(), GUISettings.instance().getAdditionalGUIBackgroundColor());//buttons background
    }
}
