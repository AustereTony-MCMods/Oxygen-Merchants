package austeretony.oxygen_merchants.client.gui.merchant;

import austeretony.oxygen.client.gui.BackgroundGUIFiller;
import austeretony.oxygen.client.gui.settings.GUISettings;

public class MerchantBackgroundGUIFiller extends BackgroundGUIFiller {

    public MerchantBackgroundGUIFiller(int xPosition, int yPosition, int width, int height) {             
        super(xPosition, yPosition, width, height, MerchantMenuGUIScreen.MERCHANT_MENU_BACKGROUND);
    }

    @Override
    public void drawDefaultBackground() {
        drawRect(- 1, - 1, this.getWidth() + 1, this.getHeight() + 1, GUISettings.instance().getBaseGUIBackgroundColor());//main background
        drawRect(0, 0, this.getWidth(), 13, GUISettings.instance().getAdditionalGUIBackgroundColor());//title background
        drawRect(0, 14, 85, 23, GUISettings.instance().getAdditionalGUIBackgroundColor());//search field background
        drawRect(86, 14, this.getWidth(), 23, GUISettings.instance().getAdditionalGUIBackgroundColor());//dummy line background
        drawRect(0, 24, this.getWidth() - 3, 176, GUISettings.instance().getPanelGUIBackgroundColor());//panel background
        drawRect(this.getWidth() - 2, 24, this.getWidth(), 176, GUISettings.instance().getAdditionalGUIBackgroundColor());//slider background
        drawRect(0, 177, this.getWidth(), 189, GUISettings.instance().getAdditionalGUIBackgroundColor());//info background
    }
}
