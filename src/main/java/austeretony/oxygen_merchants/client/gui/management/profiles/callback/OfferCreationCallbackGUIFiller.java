package austeretony.oxygen_merchants.client.gui.management.profiles.callback;

import austeretony.oxygen.client.gui.BackgroundGUIFiller;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen_merchants.client.gui.management.ManagementMenuGUIScreen;

public class OfferCreationCallbackGUIFiller extends BackgroundGUIFiller {

    public OfferCreationCallbackGUIFiller(int xPosition, int yPosition, int width, int height) {             
        super(xPosition, yPosition, width, height, ManagementMenuGUIScreen.OFFER_CREATION_CALLBACK_BACKGROUND);
    }

    @Override
    public void drawDefaultBackground() {
        drawRect(- 1, - 1, this.getWidth() + 1, this.getHeight() + 1, GUISettings.instance().getBaseGUIBackgroundColor());//main background
        drawRect(0, 0, this.getWidth(), 11, GUISettings.instance().getAdditionalGUIBackgroundColor());//title background
        drawRect(0, 12, this.getWidth() - 3, 96, GUISettings.instance().getPanelGUIBackgroundColor());//panel background
        drawRect(this.getWidth() - 2, 12, this.getWidth(), 96, GUISettings.instance().getAdditionalGUIBackgroundColor());//slider background
        drawRect(0, 97, this.getWidth(), this.getHeight(), GUISettings.instance().getAdditionalGUIBackgroundColor());//fields background
    }
}