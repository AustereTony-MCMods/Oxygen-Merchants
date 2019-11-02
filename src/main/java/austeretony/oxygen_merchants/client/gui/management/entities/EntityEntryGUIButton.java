package austeretony.oxygen_merchants.client.gui.management.entities;

import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.gui.IndexedGUIButton;
import austeretony.oxygen_core.client.gui.elements.CustomRectUtils;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import net.minecraft.client.renderer.GlStateManager;

public class EntityEntryGUIButton extends IndexedGUIButton<Long> {

    private final String entityName, merchantProfile;

    private final boolean isDead, emptyProfile;

    public EntityEntryGUIButton(long id, String entityName, String merchantProfile, boolean isDead, boolean emptyProfile) {
        super(id);
        this.entityName = entityName;
        this.merchantProfile = merchantProfile;
        this.isDead = isDead;
        this.emptyProfile = emptyProfile;
        this.enableDynamicBackground(GUISettings.get().getEnabledElementColor(), GUISettings.get().getEnabledElementColor(), GUISettings.get().getHoveredElementColor());
        this.setTextDynamicColor(GUISettings.get().getEnabledTextColor(), GUISettings.get().getDisabledTextColor(), GUISettings.get().getHoveredTextColor());
        this.setDisplayText(entityName);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (this.isVisible()) {          
            GlStateManager.pushMatrix();           
            GlStateManager.translate(this.getX(), this.getY(), 0.0F);            
            GlStateManager.scale(this.getScale(), this.getScale(), 0.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);  

            int color;                      
            if (!this.isEnabled())                  
                color = this.getDisabledBackgroundColor();
            else if (this.isHovered() || this.isToggled())                  
                color = this.getHoveredBackgroundColor();
            else                    
                color = this.getEnabledBackgroundColor();   

            int third = this.getWidth() / 3;
            CustomRectUtils.drawGradientRect(0.0D, 0.0D, third, this.getHeight(), 0x00000000, color, EnumGUIAlignment.RIGHT);
            drawRect(third, 0, this.getWidth() - third, this.getHeight(), color);
            CustomRectUtils.drawGradientRect(this.getWidth() - third, 0.0D, this.getWidth(), this.getHeight(), 0x00000000, color, EnumGUIAlignment.LEFT);

            if (!this.isEnabled())                  
                color = this.getDisabledTextColor();           
            else if (this.isHovered() || this.isToggled())                                          
                color = this.getHoveredTextColor();
            else                    
                color = this.getEnabledTextColor();

            float textScale = GUISettings.get().getPanelTextScale();

            if (this.emptyProfile)
                color = 0xFFFF6A00;

            if (this.isDead)
                color = 0xFFCC0000;

            GlStateManager.pushMatrix();           
            GlStateManager.translate(7.0F, (this.getHeight() - this.textHeight(textScale)) / 2.0F + 1.0F, 0.0F);            
            GlStateManager.scale(textScale, textScale, 0.0F);   
            this.mc.fontRenderer.drawString(this.entityName, 0, 0, color, this.isTextShadowEnabled()); 
            GlStateManager.popMatrix();      

            GlStateManager.pushMatrix();           
            GlStateManager.translate(185.0F, (this.getHeight() - this.textHeight(textScale)) / 2.0F + 1.0F, 0.0F);            
            GlStateManager.scale(textScale, textScale, 0.0F); 
            this.mc.fontRenderer.drawString(this.merchantProfile, 0, 0, color, this.isTextShadowEnabled());
            GlStateManager.popMatrix();      

            GlStateManager.popMatrix();
        }     
    }
}
