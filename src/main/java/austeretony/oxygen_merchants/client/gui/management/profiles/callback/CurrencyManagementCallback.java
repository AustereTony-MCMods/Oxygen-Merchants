package austeretony.oxygen_merchants.client.gui.management.profiles.callback;

import org.lwjgl.input.Keyboard;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.elements.OxygenButton;
import austeretony.oxygen_core.client.gui.elements.OxygenCallbackBackgroundFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenCheckBoxButton;
import austeretony.oxygen_core.client.gui.elements.OxygenCurrencySwitcher;
import austeretony.oxygen_core.client.gui.elements.OxygenCurrencySwitcher.OxygenCurrencySwitcherEntry;
import austeretony.oxygen_core.client.gui.elements.OxygenDropDownList.OxygenDropDownListEntry;
import austeretony.oxygen_core.client.gui.elements.OxygenScrollablePanel;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_merchants.client.gui.management.ManagementScreen;
import austeretony.oxygen_merchants.client.gui.management.MerchantProfilesSection;
import austeretony.oxygen_merchants.client.gui.management.profiles.InventoryItemPanelEntry;

public class CurrencyManagementCallback extends AbstractGUICallback {

    private final ManagementScreen screen;

    private final MerchantProfilesSection section; 

    private OxygenCheckBoxButton useCurrencyButton, useItemButton;

    private OxygenCurrencySwitcher currenciesSwitcher;

    private OxygenScrollablePanel itemsPanel;

    private OxygenButton confirmButton, cancelButton;

    //cache

    private int currentCurrencyIndex;

    private InventoryItemPanelEntry currentEntry;

    public CurrencyManagementCallback(ManagementScreen screen, MerchantProfilesSection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;   
        this.section = section;
    }

    @Override
    public void init() {
        this.enableDefaultBackground(EnumBaseGUISetting.FILL_CALLBACK_COLOR.get().asInt());
        this.addElement(new OxygenCallbackBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_merchants.gui.management.callback.currencyManagement"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.useCurrencyButton = new OxygenCheckBoxButton(6, 18));
        this.addElement(this.useItemButton = new OxygenCheckBoxButton(6, 40));

        this.addElement(new OxygenTextLabel(16, 24, ClientReference.localize("oxygen_merchants.gui.management.useCurrency"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));
        this.addElement(new OxygenTextLabel(16, 46, ClientReference.localize("oxygen_merchants.gui.management.useItem"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.itemsPanel = new OxygenScrollablePanel(this.screen, 6, 48, this.getWidth() - 12, 16, 1, 36, 5, EnumBaseGUISetting.TEXT_PANEL_SCALE.get().asFloat(), false));   

        this.itemsPanel.<InventoryItemPanelEntry>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (this.currentEntry != clicked) {       
                if (this.currentEntry != null)
                    this.currentEntry.setToggled(false);
                clicked.toggle();    
                this.currentEntry = clicked;

                this.useCurrencyButton.setToggled(false);
                this.useItemButton.toggle();
            }
        });

        this.addElement(this.currenciesSwitcher = new OxygenCurrencySwitcher(6, 27));         
        for (OxygenDropDownListEntry entry : this.currenciesSwitcher.getElements())
            entry.setPosition(this.getX() + entry.getX(), this.getY() + entry.getY());       
        this.currenciesSwitcher.<OxygenCurrencySwitcherEntry>setClickListener((entry)->this.currentCurrencyIndex = entry.index.getIndex());

        this.addElement(this.confirmButton = new OxygenButton(15, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen_core.gui.confirm")));
        this.confirmButton.setKeyPressListener(Keyboard.KEY_R, ()->this.confirm());

        this.addElement(this.cancelButton = new OxygenButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen_core.gui.cancel")));
        this.cancelButton.setKeyPressListener(Keyboard.KEY_X, ()->this.close());

        this.loadItems();
    }

    @Override
    protected void onOpen() {
        this.useCurrencyButton.toggle();
        this.useItemButton.setToggled(false);

        this.currentCurrencyIndex = OxygenMain.COMMON_CURRENCY_INDEX;        
        if (this.currentEntry != null) {
            this.currentEntry.setToggled(false);
            this.currentEntry = null;
        }
    }

    private void loadItems() {
        this.itemsPanel.reset();
        for (ItemStackWrapper wrapper : this.screen.inventoryContent)
            this.itemsPanel.addEntry(new InventoryItemPanelEntry(wrapper.getCachedItemStack()));     

        this.itemsPanel.getScroller().reset();
    }

    private void confirm() {
        if (this.useCurrencyButton.isToggled())
            this.section.setProfileUseCurrency(this.currentCurrencyIndex);
        else if (this.useItemButton.isToggled() && this.currentEntry != null)
            this.section.setProfileUseItem(this.currentEntry.getItemStack());
        this.close();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) { 
            if (element == this.cancelButton)
                this.close();
            else if (element == this.confirmButton) 
                this.confirm();
            else if (element == this.useCurrencyButton) {
                if (this.useCurrencyButton.isToggled()) {
                    this.useItemButton.setToggled(false);
                    if (this.currentEntry != null) {
                        this.currentEntry.setToggled(false);
                        this.currentEntry = null;
                    }
                }
            } else if (element == this.useItemButton) {
                if (this.useItemButton.isToggled())
                    this.useCurrencyButton.setToggled(false);
            }
        }
    }
}
