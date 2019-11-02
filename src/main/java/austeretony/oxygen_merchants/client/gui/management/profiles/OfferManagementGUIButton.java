package austeretony.oxygen_merchants.client.gui.management.profiles;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.ItemRenderHelper;
import austeretony.oxygen_core.client.gui.IndexedGUIButton;
import austeretony.oxygen_core.client.gui.OxygenGUITextures;
import austeretony.oxygen_core.client.gui.elements.CustomRectUtils;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_core.common.util.OxygenUtils;
import austeretony.oxygen_merchants.common.MerchantOffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class OfferManagementGUIButton extends IndexedGUIButton<Long> {

    private final ItemStack offeredStack, currencyStack;

    private final boolean useCurrencyStack, sellingEnabled, sellingOnly, singleItem;

    private final String amount, buyCost, sellingCostNotice, sellingOnlyNotice;

    public OfferManagementGUIButton(MerchantOffer offer, ItemStack currencyStack) {
        super(offer.offerId);
        this.offeredStack = offer.getOfferedStack().getCachedItemStack();
        this.currencyStack = currencyStack;
        this.useCurrencyStack = currencyStack != null;
        this.amount = String.valueOf(offer.getAmount());
        this.singleItem = offer.getAmount() == 1;
        this.buyCost = OxygenUtils.formatCurrencyValue(String.valueOf(offer.getBuyCost()));
        this.sellingEnabled = offer.isSellingEnabled();
        this.sellingOnly = offer.isSellingOnly();
        this.sellingCostNotice = offer.isSellingEnabled() ? ClientReference.localize("oxygen_merchants.gui.management.sellFor", OxygenUtils.formatCurrencyValue(String.valueOf(offer.getSellingCost()))) : ClientReference.localize("oxygen_merchants.gui.management.noSelling");
        this.sellingOnlyNotice = ClientReference.localize("oxygen_merchants.gui.management.sellingOnly");
        this.setDisplayText(this.offeredStack.getDisplayName());
        this.setDynamicBackgroundColor(GUISettings.get().getEnabledElementColor(), GUISettings.get().getDisabledElementColor(), GUISettings.get().getHoveredElementColor());
        this.setTextDynamicColor(GUISettings.get().getEnabledTextColor(), GUISettings.get().getDisabledTextColor(), GUISettings.get().getHoveredTextColor());
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (this.isVisible()) {      
            RenderHelper.enableGUIStandardItemLighting();            
            GlStateManager.enableDepth();
            this.itemRender.renderItemAndEffectIntoGUI(this.offeredStack, this.getX() + 2, this.getY());   
            GlStateManager.disableDepth();            
            RenderHelper.disableStandardItemLighting();

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

            float textScale = GUISettings.get().getSubTextScale();

            if (!this.singleItem) {
                GlStateManager.pushMatrix();           
                GlStateManager.translate(16.0F, 10.0F, 0.0F);            
                GlStateManager.scale(textScale, textScale, 0.0F);   
                this.mc.fontRenderer.drawString(this.amount, 0, 0, color, true); 
                GlStateManager.popMatrix();      
            }

            GlStateManager.pushMatrix();           
            GlStateManager.translate(this.getWidth() - 4 - this.textWidth(this.sellingCostNotice, textScale), 10.0F, 0.0F);            
            GlStateManager.scale(textScale, textScale, 0.0F); 
            this.mc.fontRenderer.drawString(this.sellingCostNotice, 0, 0, color, false);
            GlStateManager.popMatrix();      

            if (!this.sellingOnly) {
                GlStateManager.pushMatrix();           
                GlStateManager.translate(this.getWidth() - 12 - this.textWidth(this.buyCost, textScale), 2.0F, 0.0F);            
                GlStateManager.scale(textScale, textScale, 0.0F); 
                this.mc.fontRenderer.drawString(this.buyCost, 0, 0, color, false);
                GlStateManager.popMatrix(); 

                if (this.useCurrencyStack) {                
                    GlStateManager.pushMatrix();           
                    GlStateManager.translate(this.getWidth() - 10.0F, 0.0F, 0.0F);            
                    GlStateManager.scale(0.5F, 0.5F, 0.5F);    

                    RenderHelper.enableGUIStandardItemLighting();            
                    GlStateManager.enableDepth();
                    ItemRenderHelper.renderItemWithoutEffectIntoGUI(this.currencyStack, 0, 0);                              
                    GlStateManager.disableDepth();
                    RenderHelper.disableStandardItemLighting();

                    GlStateManager.popMatrix();
                } else {
                    GlStateManager.enableBlend(); 
                    this.mc.getTextureManager().bindTexture(OxygenGUITextures.COIN_ICON);
                    GUIAdvancedElement.drawCustomSizedTexturedRect(this.getWidth() - 9, 1, 0, 0, 6, 6, 6, 6);          
                    GlStateManager.disableBlend();
                } 
            } else {
                GlStateManager.pushMatrix();           
                GlStateManager.translate(this.getWidth() - 4 - this.textWidth(this.sellingOnlyNotice, textScale), 1.0F, 0.0F);            
                GlStateManager.scale(textScale, textScale, 0.0F); 
                this.mc.fontRenderer.drawString(this.sellingOnlyNotice, 0, 0, color, false);
                GlStateManager.popMatrix(); 
            }    

            textScale = GUISettings.get().getPanelTextScale();

            GlStateManager.pushMatrix();           
            GlStateManager.translate(28.0F, (this.getHeight() - this.textHeight(textScale)) / 2.0F + 1.0F, 0.0F);            
            GlStateManager.scale(textScale, textScale, 0.0F);           
            this.mc.fontRenderer.drawString(this.getDisplayText(), 0, 0, color, false);
            GlStateManager.popMatrix();     

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); 

            GlStateManager.popMatrix();
        }     
    }

    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        if (mouseX >= this.getX() + 2 && mouseY >= this.getY() && mouseX < this.getX() + 18 && mouseY < this.getY() + this.getHeight())
            this.screen.drawToolTip(this.offeredStack, mouseX + 6, mouseY);
    }
}
