package austeretony.oxygen_merchants.client.gui.management.entities.context;

import austeretony.alternateui.screen.contextmenu.AbstractContextAction;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_merchants.client.gui.management.EntitiesManagementGUISection;
import net.minecraft.client.resources.I18n;

public class VisitEntityContextAction extends AbstractContextAction {

    private final EntitiesManagementGUISection section;

    private final String name;

    public VisitEntityContextAction(EntitiesManagementGUISection section) {
        this.section = section;
        this.name = I18n.format("merchants.gui.management.visit");
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
        this.section.openVisitEntityCallback();
    }
}