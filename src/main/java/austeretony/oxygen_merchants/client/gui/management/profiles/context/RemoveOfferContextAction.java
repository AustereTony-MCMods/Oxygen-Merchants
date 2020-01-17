package austeretony.oxygen_merchants.client.gui.management.profiles.context;

import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu.OxygenContextMenuAction;
import austeretony.oxygen_merchants.client.gui.management.MerchantProfilesSection;
import austeretony.oxygen_merchants.client.gui.management.profiles.MerchantOfferPanelEntry;

public class RemoveOfferContextAction implements OxygenContextMenuAction {

    private final MerchantProfilesSection section;

    public RemoveOfferContextAction(MerchantProfilesSection section) {
        this.section = section;
    }

    @Override
    public String getLocalizedName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen_core.gui.remove");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        return true;
    }

    @Override
    public void execute(GUIBaseElement currElement) {
        this.section.removeOfferFromCurrentProfile(((MerchantOfferPanelEntry) currElement).index); 
    }
}
