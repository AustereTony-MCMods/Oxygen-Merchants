package austeretony.oxygen_merchants.client.gui.merchant;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.BackgroundGUIFiller;
import austeretony.oxygen.client.gui.settings.GUISettings;
import net.minecraft.client.renderer.GlStateManager;

public class MerchantBackgroundGUIFiller extends BackgroundGUIFiller {

    private final boolean textureExist;

    public MerchantBackgroundGUIFiller(int xPosition, int yPosition, int width, int height) {             
        super(xPosition, yPosition, width, height);
        this.textureExist = ClientReference.isTextureExist(MerchantMenuGUIScreen.BACKGROUND_TEXTURE);
    }

    @Override
    public void draw(int mouseX, int mouseY) {  
        GlStateManager.pushMatrix();            
        GlStateManager.translate(this.getX(), this.getY(), 0.0F);            
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);                      
        if (this.textureExist) {  
            GlStateManager.enableBlend();    
            this.mc.getTextureManager().bindTexture(MerchantMenuGUIScreen.BACKGROUND_TEXTURE);                         
            GUIAdvancedElement.drawCustomSizedTexturedRect( - GUISettings.instance().getTextureOffsetX(), - GUISettings.instance().getTextureOffsetY(), 
                    0, 0, this.getTextureWidth(), this.getTextureHeight(), this.getTextureWidth(), this.getTextureHeight());             
            GlStateManager.disableBlend();   
        } else {
            drawRect(- 1, - 1, this.getWidth() + 1, this.getHeight() + 1, GUISettings.instance().getBaseGUIBackgroundColor());//main background
            drawRect(0, 0, this.getWidth(), 13, GUISettings.instance().getAdditionalGUIBackgroundColor());//title background
            drawRect(0, 14, 85, 23, GUISettings.instance().getAdditionalGUIBackgroundColor());//search field background
            drawRect(86, 14, this.getWidth(), 23, GUISettings.instance().getAdditionalGUIBackgroundColor());//dummy line background
            drawRect(0, 24, this.getWidth() - 3, 176, GUISettings.instance().getPanelGUIBackgroundColor());//panel background
            drawRect(this.getWidth() - 2, 24, this.getWidth(), 176, GUISettings.instance().getAdditionalGUIBackgroundColor());//slider background
            drawRect(0, 177, this.getWidth(), 189, GUISettings.instance().getAdditionalGUIBackgroundColor());//info background
        }
        GlStateManager.popMatrix();            
    }
}
