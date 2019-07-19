package austeretony.oxygen_merchants.client.gui.management.profiles;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.util.EnumGUIAlignment;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class InventoryItemGUIButton extends GUIButton {

    private final ItemStack itemStack;

    public InventoryItemGUIButton(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.setTextAlignment(EnumGUIAlignment.LEFT, 24);
        this.setDisplayText(itemStack.getDisplayName());
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();            
        GlStateManager.enableDepth();
        this.itemRender.renderItemAndEffectIntoGUI(this.itemStack, this.getX() + 4, this.getY());                              
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        if (mouseX >= this.getX() + 4 && mouseY >= this.getY() && mouseX < this.getX() + 20 && mouseY < this.getY() + this.getHeight())
            this.screen.drawToolTip(this.itemStack, mouseX + 6, mouseY);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
