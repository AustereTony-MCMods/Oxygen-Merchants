package austeretony.oxygen_merchants.client.gui.management.profiles.callback;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.elements.OxygenCallbackGUIFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenCheckBoxGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButton;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIButtonPanel;
import austeretony.oxygen_core.client.gui.elements.OxygenGUIText;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_merchants.client.gui.management.ManagementMenuGUIScreen;
import austeretony.oxygen_merchants.client.gui.management.ProfilesManagementGUISection;
import austeretony.oxygen_merchants.client.gui.management.profiles.InventoryItemGUIButton;

public class CurrencyManagementGUICallback extends AbstractGUICallback {

    private final ManagementMenuGUIScreen screen;

    private final ProfilesManagementGUISection section; 

    private OxygenCheckBoxGUIButton useCurrencyButton, useItemButton;

    private OxygenGUIButton confirmButton, cancelButton;

    private OxygenGUIButtonPanel itemsPanel;

    private InventoryItemGUIButton currentButton;

    public CurrencyManagementGUICallback(ManagementMenuGUIScreen screen, ProfilesManagementGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;   
        this.section = section;
    }

    @Override
    public void init() {
        this.addElement(new OxygenCallbackGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenGUIText(4, 5, ClientReference.localize("oxygen_merchants.gui.management.callback.currencyManagement"), GUISettings.get().getTextScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(this.useCurrencyButton = new OxygenCheckBoxGUIButton(6, 18));
        this.addElement(this.useItemButton = new OxygenCheckBoxGUIButton(6, 28));

        this.addElement(new OxygenGUIText(18, 17, ClientReference.localize("oxygen_merchants.gui.management.useCurrency"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColor()));
        this.addElement(new OxygenGUIText(18, 27, ClientReference.localize("oxygen_merchants.gui.management.useItem"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(this.itemsPanel = new OxygenGUIButtonPanel(this.screen, 6, 38, this.getWidth() - 12, 16, 1, 36, 5, GUISettings.get().getPanelTextScale(), false));   

        this.itemsPanel.<InventoryItemGUIButton>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (this.currentButton != clicked) {       
                if (this.currentButton != null)
                    this.currentButton.setToggled(false);
                clicked.toggle();    
                this.currentButton = clicked;

                this.useCurrencyButton.setToggled(false);
                this.useItemButton.toggle();
            }
        });

        this.loadItems();

        this.addElement(this.confirmButton = new OxygenGUIButton(15, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen.gui.confirmButton")));
        this.addElement(this.cancelButton = new OxygenGUIButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen.gui.cancelButton")));
    }

    @Override
    protected void onOpen() {
        this.useCurrencyButton.toggle();
        this.useItemButton.setToggled(false);
        if (this.currentButton != null) {
            this.currentButton.setToggled(false);
            this.currentButton = null;
        }
    }

    private void loadItems() {
        this.itemsPanel.reset();
        this.itemsPanel.getScroller().resetPosition();

        for (ItemStackWrapper wrapper : this.screen.inventoryContent)
            this.itemsPanel.addButton(new InventoryItemGUIButton(wrapper.getCachedItemStack()));     
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) { 
            if (element == this.cancelButton)
                this.close();
            else if (element == this.confirmButton) {
                if (this.useCurrencyButton.isToggled())
                    this.section.setProfileUseCurrency();
                else if (this.useItemButton.isToggled() && this.currentButton != null)
                    this.section.setProfileUseItem(this.currentButton.getItemStack());
                this.close();
            } else if (element == this.useCurrencyButton) {
                if (this.useCurrencyButton.isToggled()) {
                    this.useItemButton.setToggled(false);
                    if (this.currentButton != null) {
                        this.currentButton.setToggled(false);
                        this.currentButton = null;
                    }
                }
            } else if (element == this.useItemButton) {
                if (this.useItemButton.isToggled())
                    this.useCurrencyButton.setToggled(false);
            }
        }
    }
}
