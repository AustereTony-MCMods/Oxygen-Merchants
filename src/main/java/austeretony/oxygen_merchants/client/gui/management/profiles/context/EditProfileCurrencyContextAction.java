package austeretony.oxygen_merchants.client.gui.management.profiles.context;

import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu.OxygenContextMenuAction;
import austeretony.oxygen_merchants.client.gui.management.MerchantProfilesSection;

public class EditProfileCurrencyContextAction implements OxygenContextMenuAction {

    private final MerchantProfilesSection section;

    public EditProfileCurrencyContextAction(MerchantProfilesSection section) {
        this.section = section;
    }

    @Override
    public String getLocalizedName(GUIBaseElement currElement) {
        return ClientReference.localize("oxygen_merchants.gui.management.editProfileCurrency");
    }

    @Override
    public boolean isValid(GUIBaseElement currElement) {
        return true;
    }

    @Override
    public void execute(GUIBaseElement currElement) {
        this.section.openProfileCurrencyManagementCallback();
    }
}
