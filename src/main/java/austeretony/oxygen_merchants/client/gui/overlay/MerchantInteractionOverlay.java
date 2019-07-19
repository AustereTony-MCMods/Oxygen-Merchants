package austeretony.oxygen_merchants.client.gui.overlay;

import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.oxygen.client.core.api.ClientReference;
import austeretony.oxygen.client.gui.settings.GUISettings;
import austeretony.oxygen.client.input.OxygenKeyHandler;
import austeretony.oxygen.client.interaction.IInteractionOverlay;
import austeretony.oxygen.common.config.OxygenConfig;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.common.main.BoundEntityEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class MerchantInteractionOverlay implements IInteractionOverlay {

    private Entity pointed;

    @Override
    public boolean isValid() {
        this.pointed = ClientReference.getPointedEntity();
        return this.pointed != null 
                && this.pointed instanceof EntityLiving 
                && ClientReference.isEntitiesNear(this.pointed, ClientReference.getClientPlayer(), 3.0D)
                && MerchantsManagerClient.instance().getBoundEntitiesManager().bondExist(ClientReference.getPersistentUUID(this.pointed));
    }

    @Override
    public void draw(float partialTicks) {
        Minecraft mc = ClientReference.getMinecraft();
        ScaledResolution scaledResolution = new ScaledResolution(mc);  
        String interactionKeyName = OxygenKeyHandler.INTERACT.getDisplayName();
        int 
        x = scaledResolution.getScaledWidth() / 2 + 10,
        y = scaledResolution.getScaledHeight() / 2 - 8,
        keyNameWidth = mc.fontRenderer.getStringWidth(interactionKeyName);

        GlStateManager.pushMatrix();    
        GlStateManager.translate(x, y, 0.0F);          
        GlStateManager.scale(GUISettings.instance().getOverlayScale(), GUISettings.instance().getOverlayScale(), 0.0F);         

        BoundEntityEntry entry = MerchantsManagerClient.instance().getBoundEntitiesManager().getBond(ClientReference.getPersistentUUID(this.pointed));
        mc.fontRenderer.drawString(entry.getName() + ", " + entry.getProfession(), 0, 0, GUISettings.instance().getAdditionalOverlayTextColor(), true);

        if (!OxygenConfig.INTERACT_WITH_RMB.getBooleanValue()) {
            GUISimpleElement.drawRect(0, 12, keyNameWidth + 6, 24, GUISettings.instance().getBaseGUIBackgroundColor());
            GUISimpleElement.drawRect(1, 13, keyNameWidth + 5, 23, GUISettings.instance().getAdditionalGUIBackgroundColor());
            mc.fontRenderer.drawString(interactionKeyName, 3, 15, GUISettings.instance().getAdditionalOverlayTextColor());
            mc.fontRenderer.drawString(ClientReference.localize(OxygenKeyHandler.INTERACT.getKeyDescription()), 10 + keyNameWidth, 15, GUISettings.instance().getBaseOverlayTextColor(), true);
        } else
            mc.fontRenderer.drawString(ClientReference.localize(OxygenKeyHandler.INTERACT.getKeyDescription()), 0, 15, GUISettings.instance().getBaseOverlayTextColor(), true);

        GlStateManager.popMatrix();
    }
}
