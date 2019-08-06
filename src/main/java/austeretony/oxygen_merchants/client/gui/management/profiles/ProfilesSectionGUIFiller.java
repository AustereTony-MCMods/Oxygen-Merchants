package austeretony.oxygen_merchants.client.gui.management.profiles;

import austeretony.oxygen.client.gui.BackgroundGUIFiller;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen_merchants.client.gui.management.ManagementMenuGUIScreen;

public class ProfilesSectionGUIFiller extends BackgroundGUIFiller {

    public ProfilesSectionGUIFiller(int xPosition, int yPosition, int width, int height) {             
        super(xPosition, yPosition, width, height, ManagementMenuGUIScreen.PROFILES_MENU_BACKGROUND);
    }

    @Override
    public void drawDefaultBackground() {
        drawRect(- 1, - 1, this.getWidth() + 1, this.getHeight() + 1, GUISettings.instance().getBaseGUIBackgroundColor());//main background
        drawRect(0, 0, this.getWidth(), 13, GUISettings.instance().getAdditionalGUIBackgroundColor());//title background
        drawRect(0, 14, 85, 23, GUISettings.instance().getAdditionalGUIBackgroundColor());//search panel background
        drawRect(0, 24, 82, 133, GUISettings.instance().getPanelGUIBackgroundColor());//panel background
        drawRect(83, 24, 85, 133, GUISettings.instance().getAdditionalGUIBackgroundColor());//slider background
        drawRect(0, 134, 85, 149, GUISettings.instance().getAdditionalGUIBackgroundColor());//create button background

        drawRect(86, 14, this.getWidth(), 31, GUISettings.instance().getAdditionalGUIBackgroundColor());//profile name background
        drawRect(86, 32, this.getWidth() - 3, 133, GUISettings.instance().getPanelGUIBackgroundColor());//offers panel background
        drawRect(this.getWidth() - 2, 32, this.getWidth(), 133, GUISettings.instance().getAdditionalGUIBackgroundColor());//offers slider background
        drawRect(86, 134, this.getWidth(), this.getHeight(), GUISettings.instance().getAdditionalGUIBackgroundColor());//create button background
    }
}
