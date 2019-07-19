package austeretony.oxygen_merchants.client.gui.management.profiles.context;

import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_merchants.client.gui.management.ProfilesManagementGUISection;
import austeretony.oxygen_merchants.client.gui.management.profiles.OfferFullGUIButton;
import net.minecraft.client.resources.I18n;

public class RemoveOfferContextAction extends AbstractContextAction {

    private final ProfilesManagementGUISection section;

    private final String name;

    public RemoveOfferContextAction(ProfilesManagementGUISection section) {
        this.section = section;
        this.name = I18n.format("merchants.gui.management.remove");
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
        this.section.removeOfferFromCurrentProfile(((OfferFullGUIButton) currElement).id); 
    }
}
