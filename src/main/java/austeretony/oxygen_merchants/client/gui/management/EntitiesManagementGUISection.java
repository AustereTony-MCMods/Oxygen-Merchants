package austeretony.oxygen_merchants.client.gui.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.IndexedGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButtonPanel;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIContextMenu;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIText;
import austeretony.oxygen_core.client.gui.elements.OxygenGUITextField;
import austeretony.oxygen_core.client.gui.elements.OxygenSorterGUIElement;
import austeretony.oxygen_core.client.gui.elements.OxygenSorterGUIElement.EnumSorting;
import austeretony.oxygen_core.client.gui.elements.SectionsGUIDDList;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.gui.management.entities.EntitiesSectionGUIFiller;
import austeretony.oxygen_merchants.client.gui.management.entities.EntityEntryGUIButton;
import austeretony.oxygen_merchants.client.gui.management.entities.callback.EntryCreationGUICallback;
import austeretony.oxygen_merchants.client.gui.management.entities.callback.EntryEditGUICallback;
import austeretony.oxygen_merchants.client.gui.management.entities.callback.RemoveEntryGUICallback;
import austeretony.oxygen_merchants.client.gui.management.entities.callback.VisitEntityGUICallback;
import austeretony.oxygen_merchants.client.gui.management.entities.context.EditBondContextAction;
import austeretony.oxygen_merchants.client.gui.management.entities.context.RemoveBondContextAction;
import austeretony.oxygen_merchants.client.gui.management.entities.context.VisitEntityContextAction;
import austeretony.oxygen_merchants.common.BoundEntityEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntitiesManagementGUISection extends AbstractGUISection {

    private final ManagementMenuGUIScreen screen;

    private OxygenGUIButton createButton;

    private OxygenGUIButtonPanel entitiesPanel;

    private OxygenGUIText entitiesAmountTextLabel;

    private OxygenGUITextField searchField;

    private OxygenSorterGUIElement nameSorterElement, profileSorterElement;

    private AbstractGUICallback entryCreationCallback, entryEditCallback, removeEntryCallback, visitEntityCallback;

    //cache

    private IndexedGUIButton<Long> currentEntryButton;

    public final Entity pointedEntity;

    public EntitiesManagementGUISection(ManagementMenuGUIScreen screen) {
        super(screen);
        this.screen = screen;
        this.pointedEntity = ClientReference.getPointedEntity();
    }

    @Override
    public void init() {
        this.addElement(new EntitiesSectionGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenGUIText(4, 5, ClientReference.localize("oxygen_merchants.gui.management.title"), GUISettings.get().getTitleScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(this.entitiesAmountTextLabel = new OxygenGUIText(0, 18, "", GUISettings.get().getSubTextScale() - 0.05F, GUISettings.get().getEnabledTextColor()));

        this.addElement(this.nameSorterElement = new OxygenSorterGUIElement(6, 18, EnumSorting.DOWN, ClientReference.localize("oxygen.sorting.name")));   
        this.nameSorterElement.setClickListener((sorting)->{
            this.profileSorterElement.reset();
            this.sortEntries(sorting == EnumSorting.DOWN ? 0 : 1);
        });

        this.addElement(this.profileSorterElement = new OxygenSorterGUIElement(12, 18, EnumSorting.INACTIVE, ClientReference.localize("oxygen_merchants.sorting.profile")));  
        this.profileSorterElement.setClickListener((sorting)->{
            this.nameSorterElement.reset();
            this.sortEntries(sorting == EnumSorting.DOWN ? 2 : 3);
        });

        this.addElement(this.entitiesPanel = new OxygenGUIButtonPanel(this.screen, 6, 24, this.getWidth() - 15, 10, 1, MathUtils.clamp(MerchantsManagerClient.instance().getBoundEntitiesContainer().getEntriesAmount(), 10, 100), 10, GUISettings.get().getPanelTextScale(), true));        

        this.entitiesPanel.<IndexedGUIButton<Long>>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (this.currentEntryButton != clicked)                
                this.currentEntryButton = clicked;
        });

        this.entitiesPanel.initContextMenu(new OxygenGUIContextMenu(GUISettings.get().getContextMenuWidth(), 9, 
                new EditBondContextAction(this),
                new VisitEntityContextAction(this),
                new RemoveBondContextAction(this)));  

        this.addElement(new SectionsGUIDDList(this.getWidth() - 4, 5, this, this.screen.getProfilesSection()));

        this.addElement(this.createButton = new OxygenGUIButton(22, 137, 40, 10, ClientReference.localize("oxygen_merchants.gui.management.create"))
                .setEnabled(this.pointedEntity != null && this.pointedEntity instanceof EntityLiving));     

        this.entryCreationCallback = new EntryCreationGUICallback(this.screen, this, 140, 136).enableDefaultBackground();
        this.entryEditCallback = new EntryEditGUICallback(this.screen, this, 140, 136).enableDefaultBackground();
        this.visitEntityCallback = new VisitEntityGUICallback(this.screen, this, 140, 38).enableDefaultBackground();
        this.removeEntryCallback = new RemoveEntryGUICallback(this.screen, this, 140, 38).enableDefaultBackground();
    }

    public static String getProfileName(BoundEntityEntry entry) {
        return MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfile(entry.getProfileId()).getName();
    }

    private void sortEntries(int mode) {
        List<BoundEntityEntry> profiles = new ArrayList<BoundEntityEntry>(MerchantsManagerClient.instance().getBoundEntitiesContainer().getEntries());

        if (mode == 0)
            Collections.sort(profiles, (p1, p2)->p1.getName().compareTo(p2.getName()));
        else if (mode == 1)
            Collections.sort(profiles, (p1, p2)->p2.getName().compareTo(p1.getName()));
        else if (mode == 2)
            Collections.sort(profiles, (p1, p2)->getProfileName(p1).compareTo(getProfileName(p2)));
        else if (mode == 3)
            Collections.sort(profiles, (p1, p2)->getProfileName(p2).compareTo(getProfileName(p1)));

        this.entitiesPanel.reset();
        String profileName;
        for (BoundEntityEntry entry : profiles) {
            if (entry.getProfileId() != 0L)
                profileName = MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfile(entry.getProfileId()).getName();
            else
                profileName = ClientReference.localize("oxygen_merchants.gui.management.emptyProfile");
            this.entitiesPanel.addButton(new EntityEntryGUIButton(entry.getId(), entry.getProfession().isEmpty() ? entry.getName() : entry.getName() + ", " + entry.getProfession(),
                    profileName, entry.isDead(), entry.getProfileId() == 0L));
        }

        this.entitiesAmountTextLabel.setDisplayText(String.valueOf(MerchantsManagerClient.instance().getBoundEntitiesContainer().getEntriesAmount()));     
        this.entitiesAmountTextLabel.setX(this.getWidth() - 9 - this.textWidth(this.entitiesAmountTextLabel.getDisplayText(), GUISettings.get().getSubTextScale() - 0.05F));

        this.entitiesPanel.getScroller().resetPosition();
        this.entitiesPanel.getScroller().getSlider().reset();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.createButton)
                this.entryCreationCallback.open();
        }
    }

    public void entitiesSynchronized() {
        this.sortEntries(0);
    }

    public void entityCreated(BoundEntityEntry entry) {
        this.sortEntries(0);

        this.nameSorterElement.setSorting(EnumSorting.DOWN);
        this.profileSorterElement.reset();
    }

    public void entityUpdated(BoundEntityEntry entry) {
        this.sortEntries(0);

        this.nameSorterElement.setSorting(EnumSorting.DOWN);
        this.profileSorterElement.reset();
    }

    public void entityRemoved(BoundEntityEntry entry) {
        this.sortEntries(0);

        this.nameSorterElement.setSorting(EnumSorting.DOWN);
        this.profileSorterElement.reset();
    }

    public IndexedGUIButton<Long> getCurrentEntryButton() {
        return this.currentEntryButton;
    }

    public void openEntryEditCallback() {
        this.entryEditCallback.open();
    }

    public void openVisitEntityCallback() {
        this.visitEntityCallback.open();
    }

    public void openRemoveEntryCallback() {
        this.removeEntryCallback.open();
    }
}
