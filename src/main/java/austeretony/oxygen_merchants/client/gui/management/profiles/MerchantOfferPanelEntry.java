package austeretony.oxygen_merchants.client.gui.management.profiles;

import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseClientSetting;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.OxygenGUIUtils;
import austeretony.oxygen_core.client.gui.elements.OxygenIndexedPanelEntry;
import austeretony.oxygen_core.common.util.OxygenUtils;
import austeretony.oxygen_merchants.common.MerchantOffer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class MerchantOfferPanelEntry extends OxygenIndexedPanelEntry<Long> {

    private final ItemStack offeredStack, currencyStack;

    private final boolean useCurrencyStack, singleItem, enableDurabilityBar;

    private final String amountStr, buyPriceStr, sellingPriceStr;

    public MerchantOfferPanelEntry(MerchantOffer offer, ItemStack currencyStack) {
        super(offer.offerId);
        this.offeredStack = offer.getOfferedStack().getCachedItemStack();
        this.currencyStack = currencyStack;
        this.useCurrencyStack = currencyStack != null;
        this.amountStr = String.valueOf(offer.getAmount());
        this.singleItem = offer.getAmount() == 1;
        this.buyPriceStr = offer.isBuyEnabled() ? ClientReference.localize("oxygen_merchants.gui.management.buyFor", OxygenUtils.formatCurrencyValue(String.valueOf(offer.getBuyCost()))) : ClientReference.localize("oxygen_merchants.gui.management.noBuy");
        this.sellingPriceStr = offer.isSellingEnabled() ? ClientReference.localize("oxygen_merchants.gui.management.sellFor", OxygenUtils.formatCurrencyValue(String.valueOf(offer.getSellingCost()))) : ClientReference.localize("oxygen_merchants.gui.management.noSelling");

        this.enableDurabilityBar = EnumBaseClientSetting.ENABLE_ITEMS_DURABILITY_BAR.get().asBoolean();
        this.setDisplayText(EnumBaseClientSetting.ENABLE_RARITY_COLORS.get().asBoolean() ? this.offeredStack.getRarity().rarityColor + this.offeredStack.getDisplayName() : this.offeredStack.getDisplayName());
        this.setDynamicBackgroundColor(EnumBaseGUISetting.ELEMENT_ENABLED_COLOR.get().asInt(), EnumBaseGUISetting.ELEMENT_DISABLED_COLOR.get().asInt(), EnumBaseGUISetting.ELEMENT_HOVERED_COLOR.get().asInt());
        this.setTextDynamicColor(EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt(), EnumBaseGUISetting.TEXT_DISABLED_COLOR.get().asInt(), EnumBaseGUISetting.TEXT_HOVERED_COLOR.get().asInt());
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (this.isVisible()) {      
            RenderHelper.enableGUIStandardItemLighting();            
            GlStateManager.enableDepth();
            this.itemRender.renderItemAndEffectIntoGUI(this.offeredStack, this.getX() + 2, this.getY());   

            if (this.enableDurabilityBar) {
                FontRenderer font = this.offeredStack.getItem().getFontRenderer(this.offeredStack);
                if (font == null) 
                    font = this.mc.fontRenderer;
                this.itemRender.renderItemOverlayIntoGUI(font, this.offeredStack, this.getX() + 2, this.getY(), null);
            }

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
            OxygenGUIUtils.drawGradientRect(0.0D, 0.0D, third, this.getHeight(), 0x00000000, color, EnumGUIAlignment.RIGHT);
            drawRect(third, 0, this.getWidth() - third, this.getHeight(), color);
            OxygenGUIUtils.drawGradientRect(this.getWidth() - third, 0.0D, this.getWidth(), this.getHeight(), 0x00000000, color, EnumGUIAlignment.LEFT);

            if (!this.isEnabled())                  
                color = this.getDisabledTextColor();           
            else if (this.isHovered() || this.isToggled())                                          
                color = this.getHoveredTextColor();
            else                    
                color = this.getEnabledTextColor();

            if (!this.singleItem) {
                GlStateManager.pushMatrix();           
                GlStateManager.translate(16.0F, 10.0F, 0.0F);            
                GlStateManager.scale(this.getTextScale() - 0.05F, this.getTextScale() - 0.05F, 0.0F);   
                this.mc.fontRenderer.drawString(this.amountStr, 0, 0, color, true); 
                GlStateManager.popMatrix();      
            }

            GlStateManager.pushMatrix();           
            GlStateManager.translate(this.getWidth() - 4 - this.textWidth(this.buyPriceStr, this.getTextScale() - 0.05F), 1.0F, 0.0F);            
            GlStateManager.scale(this.getTextScale() - 0.05F, this.getTextScale() - 0.05F, 0.0F); 
            this.mc.fontRenderer.drawString(this.buyPriceStr, 0, 0, color, false);
            GlStateManager.popMatrix(); 

            GlStateManager.pushMatrix();           
            GlStateManager.translate(this.getWidth() - 4 - this.textWidth(this.sellingPriceStr, this.getTextScale() - 0.05F), 10.0F, 0.0F);            
            GlStateManager.scale(this.getTextScale() - 0.05F, this.getTextScale() - 0.05F, 0.0F); 
            this.mc.fontRenderer.drawString(this.sellingPriceStr, 0, 0, color, false);
            GlStateManager.popMatrix(); 

            GlStateManager.pushMatrix();           
            GlStateManager.translate(28.0F, (this.getHeight() - this.textHeight(this.getTextScale() + 0.05F)) / 2.0F, 0.0F);            
            GlStateManager.scale(this.getTextScale() + 0.05F, this.getTextScale() + 0.05F, 0.0F);           
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
