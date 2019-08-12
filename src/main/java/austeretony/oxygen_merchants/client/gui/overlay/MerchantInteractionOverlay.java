package austeretony.oxygen_merchants.client.gui.overlay;

import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.overlay.IOverlay;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.client.input.InteractKeyHandler;
import austeretony.oxygen.common.config.OxygenClientConfig;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class MerchantInteractionOverlay implements IOverlay {

    private Entity pointed;

    @Override
    public boolean shouldDraw() {
        this.pointed = ClientReference.getPointedEntity();
        return this.pointed != null 
                && this.pointed instanceof EntityLiving 
                && ClientReference.isEntitiesNear(this.pointed, ClientReference.getClientPlayer(), 3.0D)
                && MerchantsManagerClient.instance().getBoundEntitiesManager().entryExist(ClientReference.getPersistentUUID(this.pointed));
    }

    @Override
    public void draw(float partialTicks) {
        Minecraft mc = ClientReference.getMinecraft();
        ScaledResolution scaledResolution = new ScaledResolution(mc);  
        int 
        x = scaledResolution.getScaledWidth() / 2 + 10,
        y = scaledResolution.getScaledHeight() / 2,
        keyNameWidth;

        GlStateManager.pushMatrix();    
        GlStateManager.translate(x, y, 0.0F);          
        GlStateManager.scale(GUISettings.instance().getOverlayScale(), GUISettings.instance().getOverlayScale(), 0.0F);         

        BoundEntityEntry entry = MerchantsManagerClient.instance().getBoundEntitiesManager().getBoundEntityEntry(ClientReference.getPersistentUUID(this.pointed));
        if (!entry.getProfession().isEmpty())
            mc.fontRenderer.drawString(entry.getName() + ", " + entry.getProfession(), 0, 0, GUISettings.instance().getAdditionalOverlayTextColor(), true);
        else
            mc.fontRenderer.drawString(entry.getName(), 0, 0, GUISettings.instance().getAdditionalOverlayTextColor(), true);

        mc.fontRenderer.drawString(ClientReference.localize("oxygen_merchants.gui.management.merchantProfession"), 0, 12, GUISettings.instance().getBaseOverlayTextColor(), true);

        if (!OxygenClientConfig.INTERACT_WITH_RMB.getBooleanValue()) {
            String interactionKeyName = InteractKeyHandler.INTERACT.getDisplayName();
            keyNameWidth = mc.fontRenderer.getStringWidth(interactionKeyName);

            GUISimpleElement.drawRect(0, 24, keyNameWidth + 6, 36, GUISettings.instance().getBaseGUIBackgroundColor());
            GUISimpleElement.drawRect(1, 25, keyNameWidth + 5, 35, GUISettings.instance().getAdditionalGUIBackgroundColor());
            mc.fontRenderer.drawString(interactionKeyName, 3, 27, GUISettings.instance().getAdditionalOverlayTextColor());
            mc.fontRenderer.drawString(ClientReference.localize("key.oxygen.interact"), 10 + keyNameWidth, 27, GUISettings.instance().getAdditionalOverlayTextColor(), true);
        } else
            mc.fontRenderer.drawString(ClientReference.localize("key.oxygen.interact"), 0, 27, GUISettings.instance().getAdditionalOverlayTextColor(), true);

        GlStateManager.popMatrix();
    }
}
