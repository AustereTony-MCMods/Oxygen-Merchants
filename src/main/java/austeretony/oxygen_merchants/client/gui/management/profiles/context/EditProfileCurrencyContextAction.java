package austeretony.oxygen_merchants.client.gui.management.profiles.context;

import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_merchants.client.gui.management.ProfilesManagementGUISection;
import net.minecraft.client.resources.I18n;

public class EditProfileCurrencyContextAction extends AbstractContextAction {

    private final ProfilesManagementGUISection section;

    private final String name;

    public EditProfileCurrencyContextAction(ProfilesManagementGUISection section) {
        this.section = section;
        this.name = I18n.format("merchants.gui.management.editProfileCurrency");
    }

    @Override
    protected String getName(GUIBaseElement currElement) {
        return this.name;
    }

    @Override
    protected boolean isValid(GUIBaseElement currElement) {
        return true;
    }

    @Override
    protected void execute(GUIBaseElement currElement) {
        this.section.openProfileCurrencyManagementCallback();
    }
}
