package austeretony.oxygen_merchants.client.gui.merchant;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.alternateui.util.EnumGUIAlignment;
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

public class OfferGUIButton extends IndexedGUIButton<Long> {

    private final ItemStack offeredStack, currencyStack;

    private final String amountStr, costStr;

    private String playerStockStr;

    public final long cost;

    private final boolean singleItem;

    private boolean useCurrencyStack, available;

    public OfferGUIButton(MerchantOffer offer, long cost, int playerStock, ItemStack currencyStack) {
        super(offer.offerId);
        this.playerStockStr = String.valueOf(playerStock);
        this.offeredStack = offer.getOfferedStack().getCachedItemStack();
        this.currencyStack = currencyStack;
        this.useCurrencyStack = currencyStack != null;
        this.amountStr = String.valueOf(offer.getAmount());
        this.cost = cost;
        this.costStr = OxygenUtils.formatCurrencyValue(String.valueOf(cost));
        this.singleItem = offer.getAmount() == 1;
        this.setDisplayText(this.offeredStack.getDisplayName());//for search
        this.enableDynamicBackground(GUISettings.get().getEnabledElementColor(), GUISettings.get().getEnabledElementColor(), GUISettings.get().getHoveredElementColor());
        this.setTextDynamicColor(GUISettings.get().getEnabledTextColor(), GUISettings.get().getDisabledTextColor(), GUISettings.get().getHoveredTextColor());
        this.requireDoubleClick();
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

            int color = this.getEnabledBackgroundColor();                     
            if (!this.isEnabled())                  
                color = this.getDisabledBackgroundColor();
            else if (this.isHovered())                  
                color = this.getHoveredBackgroundColor();      

            int third = this.getWidth() / 3;
            CustomRectUtils.drawGradientRect(0.0D, 0.0D, third, this.getHeight(), 0x00000000, color, EnumGUIAlignment.RIGHT);
            drawRect(third, 0, this.getWidth() - third, this.getHeight(), color);
            CustomRectUtils.drawGradientRect(this.getWidth() - third, 0.0D, this.getWidth(), this.getHeight(), 0x00000000, color, EnumGUIAlignment.LEFT);

            color = this.getEnabledTextColor();
            if (!this.isEnabled())                  
                color = this.getDisabledTextColor();           
            else if (this.isHovered())                                          
                color = this.getHoveredTextColor();

            float textScale = GUISettings.get().getSubTextScale() - 0.05F;

            if (this.isHovered()) {
                GlStateManager.pushMatrix();           
                GlStateManager.translate(16.0F, 1.0F, 0.0F);            
                GlStateManager.scale(textScale, textScale, 0.0F);   
                this.mc.fontRenderer.drawString(this.playerStockStr, 0, 0, color, true); 
                GlStateManager.popMatrix();
            }

            if (!this.singleItem) {
                GlStateManager.pushMatrix();           
                GlStateManager.translate(16.0F, 10.0F, 0.0F);            
                GlStateManager.scale(textScale, textScale, 0.0F);   
                this.mc.fontRenderer.drawString(this.amountStr, 0, 0, color, true);           
                GlStateManager.popMatrix();      
            }

            GlStateManager.pushMatrix();           
            GlStateManager.translate(this.getWidth() - 12.0F - this.textWidth(this.costStr, textScale), (this.getHeight() - this.textHeight(textScale)) / 2.0F, 0.0F);            
            GlStateManager.scale(textScale, textScale, 0.0F); 
            this.mc.fontRenderer.drawString(this.costStr, 0, 0, this.available ? color : 0xFFCC0000, false);
            GlStateManager.popMatrix();    

            GlStateManager.pushMatrix();           
            GlStateManager.translate(28.0F, (this.getHeight() - this.textHeight(textScale)) / 2.0F, 0.0F);            
            GlStateManager.scale(textScale + 0.1F, textScale + 0.1F, 0.0F);           
            this.mc.fontRenderer.drawString(this.getDisplayText(), 0, 0, this.available ? color : GUISettings.get().getInactiveElementColor(), false);
            GlStateManager.popMatrix();     

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);  

            if (this.useCurrencyStack) {
                GlStateManager.pushMatrix();           
                GlStateManager.translate(this.getWidth() - 10.0F, 4.0F, 0.0F);            
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
                GUIAdvancedElement.drawCustomSizedTexturedRect(this.getWidth() - 10, 5, 0, 0, 6, 6, 6, 6);          
                GlStateManager.disableBlend();
            } 

            GlStateManager.popMatrix();
        }     
    }

    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        if (mouseX >= this.getX() + 2 && mouseY >= this.getY() && mouseX < this.getX() + 18 && mouseY < this.getY() + this.getHeight())
            this.screen.drawToolTip(this.offeredStack, mouseX + 6, mouseY);
    }

    public void setPlayerStock(int value) {
        this.playerStockStr = String.valueOf(value);
    }

    public ItemStack getOfferedStack() {
        return this.offeredStack;
    }

    public OfferGUIButton setAvailable(boolean flag) {
        this.available = flag;
        return this;
    }

    public boolean isAvailable() {
        return this.available;
    }
}
