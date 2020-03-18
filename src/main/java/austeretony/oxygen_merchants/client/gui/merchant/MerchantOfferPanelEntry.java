package austeretony.oxygen_merchants.client.gui.merchant;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.EnumBaseClientSetting;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.currency.CurrencyProperties;
import austeretony.oxygen_core.client.gui.ItemRenderHelper;
import austeretony.oxygen_core.client.gui.OxygenGUIUtils;
import austeretony.oxygen_core.client.gui.elements.OxygenWrapperPanelEntry;
import austeretony.oxygen_core.common.util.OxygenUtils;
import austeretony.oxygen_merchants.common.merchant.MerchantOffer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class MerchantOfferPanelEntry extends OxygenWrapperPanelEntry<Long> {

    private final ItemStack offeredStack, currencyStack;

    private final String idStr, amountStr, costStr;

    private String playerStockStr;

    private final boolean singleItem, enableDurabilityBar, debug;

    private boolean useCurrencyStack, available;

    private CurrencyProperties currencyProperties;

    public MerchantOfferPanelEntry(MerchantOffer offer, long cost, int playerStock, ItemStack currencyStack, CurrencyProperties properties, boolean debug) {
        super(offer.getId());
        this.playerStockStr = String.valueOf(playerStock);
        this.offeredStack = offer.getStackWrapper().getCachedItemStack();
        this.currencyStack = currencyStack;
        this.useCurrencyStack = currencyStack != null;
        this.idStr = String.format("Id: %d", offer.getId());
        this.amountStr = String.valueOf(offer.getAmount());
        this.costStr = OxygenUtils.formatCurrencyValue(String.valueOf(cost));
        this.singleItem = offer.getAmount() == 1;

        this.currencyProperties = properties;
        this.debug = debug;

        this.enableDurabilityBar = EnumBaseClientSetting.ENABLE_ITEMS_DURABILITY_BAR.get().asBoolean();
        this.setDisplayText(EnumBaseClientSetting.ENABLE_RARITY_COLORS.get().asBoolean() ? this.offeredStack.getRarity().rarityColor + this.offeredStack.getDisplayName() : this.offeredStack.getDisplayName());
        this.setDynamicBackgroundColor(EnumBaseGUISetting.ELEMENT_ENABLED_COLOR.get().asInt(), EnumBaseGUISetting.ELEMENT_DISABLED_COLOR.get().asInt(), EnumBaseGUISetting.ELEMENT_HOVERED_COLOR.get().asInt());
        this.setTextDynamicColor(EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt(), EnumBaseGUISetting.TEXT_DISABLED_COLOR.get().asInt(), EnumBaseGUISetting.TEXT_HOVERED_COLOR.get().asInt());
        this.setDebugColor(EnumBaseGUISetting.INACTIVE_ELEMENT_COLOR.get().asInt());
        this.setStaticBackgroundColor(EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt());
        this.requireDoubleClick();
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

            int color = this.getEnabledBackgroundColor();                     
            if (!this.isEnabled())                  
                color = this.getDisabledBackgroundColor();
            else if (this.isHovered())                  
                color = this.getHoveredBackgroundColor();      

            int third = this.getWidth() / 3;
            OxygenGUIUtils.drawGradientRect(0.0D, 0.0D, third, this.getHeight(), 0x00000000, color, EnumGUIAlignment.RIGHT);
            drawRect(third, 0, this.getWidth() - third, this.getHeight(), color);
            OxygenGUIUtils.drawGradientRect(this.getWidth() - third, 0.0D, this.getWidth(), this.getHeight(), 0x00000000, color, EnumGUIAlignment.LEFT);

            color = this.getEnabledTextColor();
            if (!this.isEnabled())                  
                color = this.getDisabledTextColor();           
            else if (this.isHovered())                                          
                color = this.getHoveredTextColor();

            if (this.isHovered()) {
                GlStateManager.pushMatrix();           
                GlStateManager.translate(16.0F, 1.0F, 0.0F);            
                GlStateManager.scale(this.getTextScale() - 0.05F, this.getTextScale() - 0.05F, 0.0F);   
                this.mc.fontRenderer.drawString(this.playerStockStr, 0, 0, color, true); 
                GlStateManager.popMatrix();
            }

            if (!this.singleItem) {
                GlStateManager.pushMatrix();           
                GlStateManager.translate(16.0F, 10.0F, 0.0F);            
                GlStateManager.scale(this.getTextScale() - 0.05F, this.getTextScale() - 0.05F, 0.0F);   
                this.mc.fontRenderer.drawString(this.amountStr, 0, 0, color, true);           
                GlStateManager.popMatrix();      
            }

            GlStateManager.pushMatrix();           
            GlStateManager.translate(this.getWidth() - 12.0F - this.textWidth(this.costStr, this.getTextScale() - 0.05F), (this.getHeight() - this.textHeight(this.getTextScale() - 0.05F)) / 2.0F, 0.0F);            
            GlStateManager.scale(this.getTextScale() - 0.05F, this.getTextScale() - 0.05F, 0.0F); 
            this.mc.fontRenderer.drawString(this.costStr, 0, 0, this.available ? color : 0xFFCC0000, false);
            GlStateManager.popMatrix();    

            GlStateManager.pushMatrix();           
            GlStateManager.translate(28.0F, (this.getHeight() - this.textHeight(this.getTextScale() + 0.05F)) / 2.0F, 0.0F);            
            GlStateManager.scale(this.getTextScale() + 0.05F, this.getTextScale() + 0.05F, 0.0F);           
            this.mc.fontRenderer.drawString(this.getDisplayText(), 0, 0, this.available ? color : this.getDebugColor(), false);
            GlStateManager.popMatrix();     

            if (this.debug) {
                GlStateManager.pushMatrix();           
                GlStateManager.translate(110.0F, 1.0F, 0.0F);            
                GlStateManager.scale(this.getTextScale() - 0.1F, this.getTextScale() - 0.1F, 0.0F);           
                this.mc.fontRenderer.drawString(this.idStr, 0, 0, this.getStaticBackgroundColor(), false);
                GlStateManager.popMatrix(); 
            }

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
                this.mc.getTextureManager().bindTexture(this.currencyProperties.getIcon());
                GUIAdvancedElement.drawCustomSizedTexturedRect(this.getWidth() - 10 + this.currencyProperties.getXOffset(), (this.getHeight() - this.currencyProperties.getIconHeight()) / 2 + this.currencyProperties.getYOffset(), 0, 0, this.currencyProperties.getIconWidth(), this.currencyProperties.getIconHeight(), this.currencyProperties.getIconWidth(), this.currencyProperties.getIconHeight());            
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

    public MerchantOfferPanelEntry setAvailable(boolean flag) {
        this.available = flag;
        if (flag)
            this.setDisplayText(EnumBaseClientSetting.ENABLE_RARITY_COLORS.get().asBoolean() ? this.offeredStack.getRarity().rarityColor + this.offeredStack.getDisplayName() : this.offeredStack.getDisplayName());
        else
            this.setDisplayText(this.offeredStack.getDisplayName());
        return this;
    }

    public boolean isAvailable() {
        return this.available;
    }
}
