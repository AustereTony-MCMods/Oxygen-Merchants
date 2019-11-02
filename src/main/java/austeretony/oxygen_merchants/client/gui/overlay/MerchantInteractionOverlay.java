package austeretony.oxygen_merchants.client.gui.overlay;

import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.config.OxygenConfigClient;
import austeretony.oxygen_core.client.gui.elements.CustomRectUtils;
import austeretony.oxygen_core.client.gui.overlay.Overlay;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_core.client.input.InteractKeyHandler;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class MerchantInteractionOverlay implements Overlay {

    private Entity pointed;

    @Override
    public boolean shouldDraw() {
        this.pointed = ClientReference.getPointedEntity();
        return this.pointed != null 
                && this.pointed instanceof EntityLiving 
                && ClientReference.isEntitiesNear(this.pointed, ClientReference.getClientPlayer(), 3.0D)
                && MerchantsManagerClient.instance().getBoundEntitiesContainer().entryExist(ClientReference.getPersistentUUID(this.pointed));
    }

    @Override
    public boolean drawWhileInGUI() {
        return false;
    }

    @Override
    public void draw(float partialTicks) {
        Minecraft mc = ClientReference.getMinecraft();
        ScaledResolution scaledResolution = new ScaledResolution(mc);  
        int 
        x = scaledResolution.getScaledWidth() / 2 + 10,
        y = scaledResolution.getScaledHeight() / 2;

        GlStateManager.pushMatrix();    
        GlStateManager.translate(x, y, 0.0F);          
        GlStateManager.scale(GUISettings.get().getOverlayScale(), GUISettings.get().getOverlayScale(), 0.0F);         

        BoundEntityEntry entry = MerchantsManagerClient.instance().getBoundEntitiesContainer().getBoundEntityEntry(ClientReference.getPersistentUUID(this.pointed));
        if (!entry.getProfession().isEmpty())
            mc.fontRenderer.drawString(entry.getName() + ", " + entry.getProfession(), 0, 0, GUISettings.get().getAdditionalOverlayTextColor(), true);
        else
            mc.fontRenderer.drawString(entry.getName(), 0, 0, GUISettings.get().getAdditionalOverlayTextColor(), true);

        mc.fontRenderer.drawString(ClientReference.localize("oxygen_merchants.gui.management.merchantProfession"), 0, 12, GUISettings.get().getBaseOverlayTextColor(), true);

        if (!OxygenConfigClient.INTERACT_WITH_RMB.getBooleanValue()) {
            String interactionKeyName = InteractKeyHandler.INTERACT.getDisplayName();
            int
            keyNameWidth = mc.fontRenderer.getStringWidth(interactionKeyName),
            frameWidth = keyNameWidth + 6,
            frameHeight = 12;

            this.drawKeyFrame(0, 24, frameWidth, frameHeight);

            mc.fontRenderer.drawString(interactionKeyName, 3, 27, GUISettings.get().getAdditionalOverlayTextColor());
            mc.fontRenderer.drawString(ClientReference.localize("key.oxygen.interact"), 10 + keyNameWidth, 27, GUISettings.get().getAdditionalOverlayTextColor(), true);
        } else
            mc.fontRenderer.drawString(ClientReference.localize("key.oxygen.interact"), 0, 27, GUISettings.get().getAdditionalOverlayTextColor(), true);

        GlStateManager.popMatrix();
    }

    private void drawKeyFrame(int x, int y, int width, int height) {
        //background
        GUISimpleElement.drawRect(x, y, x + width, y + height, GUISettings.get().getBaseGUIBackgroundColor());

        //frame
        CustomRectUtils.drawRect(x, y, x + 0.4D, y + height, GUISettings.get().getAdditionalGUIBackgroundColor());
        CustomRectUtils.drawRect(x + width - 0.4D, y, x + width, y + height, GUISettings.get().getAdditionalGUIBackgroundColor());
        CustomRectUtils.drawRect(x, y, x + width, y + 0.4D, GUISettings.get().getAdditionalGUIBackgroundColor());
        CustomRectUtils.drawRect(x, y + height - 0.4D, x + width, y + height, GUISettings.get().getAdditionalGUIBackgroundColor());
    }
}
