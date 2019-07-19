package austeretony.oxygen_merchants.client.gui.management.entities.callback;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.BackgroundGUIFiller;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen_merchants.client.gui.management.ManagementMenuGUIScreen;
import net.minecraft.client.renderer.GlStateManager;

public class BondCreationCallbackGUIFiller extends BackgroundGUIFiller {

    private final boolean textureExist;

    public BondCreationCallbackGUIFiller(int xPosition, int yPosition, int width, int height) {             
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
            drawRect(0, 12, this.getWidth(), 65, GUISettings.instance().getAdditionalGUIBackgroundColor());//fields background
            drawRect(0, 66, this.getWidth() - 3, 119, GUISettings.instance().getPanelGUIBackgroundColor());//panel background
            drawRect(this.getWidth() - 2, 66, this.getWidth(), 119, GUISettings.instance().getAdditionalGUIBackgroundColor());//slider background
            drawRect(0, 120, this.getWidth(), this.getHeight(), GUISettings.instance().getAdditionalGUIBackgroundColor());//buttons background
        }
        GlStateManager.popMatrix();            
    }
}
