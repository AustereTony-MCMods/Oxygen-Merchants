package austeretony.oxygen_merchants.client.gui.management.profiles.callback;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.BackgroundGUIFiller;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen_merchants.client.gui.management.ManagementMenuGUIScreen;
import net.minecraft.client.renderer.GlStateManager;

public class CurrencyManagementCallbackGUIFiller extends BackgroundGUIFiller {

    private final boolean textureExist;

    public CurrencyManagementCallbackGUIFiller(int xPosition, int yPosition, int width, int height) {             
        super(xPosition, yPosition, width, height);
        this.textureExist = ClientReference.isTextureExist(ManagementMenuGUIScreen.CURRENCY_MANAGEMENT_CALLBACK_BACKGROUND);
    }

    @Override
    public void draw(int mouseX, int mouseY) {  
        GlStateManager.pushMatrix();            
        GlStateManager.translate(this.getX(), this.getY(), 0.0F);            
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);                      
        if (this.textureExist) {  
            GlStateManager.enableBlend();    
            this.mc.getTextureManager().bindTexture(ManagementMenuGUIScreen.CURRENCY_MANAGEMENT_CALLBACK_BACKGROUND);                         
            GUIAdvancedElement.drawCustomSizedTexturedRect( - GUISettings.instance().getTextureOffsetX(), - GUISettings.instance().getTextureOffsetY(), 
                    0, 0, this.getTextureWidth(), this.getTextureHeight(), this.getTextureWidth(), this.getTextureHeight());          
            GlStateManager.disableBlend();   
        } else {
            drawRect(- 1, - 1, this.getWidth() + 1, this.getHeight() + 1, GUISettings.instance().getBaseGUIBackgroundColor());//main background
            drawRect(0, 0, this.getWidth(), 11, GUISettings.instance().getAdditionalGUIBackgroundColor());//title background
            drawRect(0, 12, this.getWidth(), 32, GUISettings.instance().getAdditionalGUIBackgroundColor());//check boxes background
            drawRect(0, 33, this.getWidth() - 3, 117, GUISettings.instance().getPanelGUIBackgroundColor());//panel background
            drawRect(this.getWidth() - 2, 33, this.getWidth(), 117, GUISettings.instance().getAdditionalGUIBackgroundColor());//slider background
            drawRect(0, 118, this.getWidth(), 132, GUISettings.instance().getAdditionalGUIBackgroundColor());//buttons background
        }
        GlStateManager.popMatrix();            
    }
}
