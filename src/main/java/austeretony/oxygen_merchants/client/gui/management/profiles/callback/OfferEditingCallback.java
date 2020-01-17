package austeretony.oxygen_merchants.client.gui.management.profiles.callback;

import org.lwjgl.input.Keyboard;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.elements.OxygenButton;
import austeretony.oxygen_core.client.gui.elements.OxygenCallbackBackgroundFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenNumberField;
import austeretony.oxygen_core.client.gui.elements.OxygenScrollablePanel;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_merchants.client.gui.management.ManagementScreen;
import austeretony.oxygen_merchants.client.gui.management.MerchantProfilesSection;
import austeretony.oxygen_merchants.client.gui.management.profiles.InventoryItemPanelEntry;
import austeretony.oxygen_merchants.common.MerchantOffer;

public class OfferEditingCallback extends AbstractGUICallback {

    private final ManagementScreen screen;

    private final MerchantProfilesSection section; 

    private OxygenScrollablePanel itemsPanel;

    private OxygenNumberField amountField, buyPriceField, sellingPriceField;

    private OxygenButton confirmButton, cancelButton;

    //cache

    private InventoryItemPanelEntry currentEntry;

    public OfferEditingCallback(ManagementScreen screen, MerchantProfilesSection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;   
        this.section = section;
    }

    @Override
    public void init() {
        this.enableDefaultBackground(EnumBaseGUISetting.FILL_CALLBACK_COLOR.get().asInt());
        this.addElement(new OxygenCallbackBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_merchants.gui.management.callback.editOffer"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(new OxygenTextLabel(6, 110, ClientReference.localize("oxygen_merchants.gui.management.amount"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));
        this.addElement(this.amountField = new OxygenNumberField(6, 112, 45, "", Long.MAX_VALUE, false, 0, true));
        this.amountField.setInputListener((keyChar, keyCode)->this.confirmButton.setEnabled(!this.amountField.getTypedText().isEmpty() && !this.buyPriceField.getTypedText().isEmpty() && !this.sellingPriceField.getTypedText().isEmpty()));

        this.addElement(new OxygenTextLabel(6, 130, ClientReference.localize("oxygen_merchants.gui.management.buyPrice"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));
        this.addElement(this.buyPriceField = new OxygenNumberField(6, 132, 45, "", Long.MAX_VALUE, false, 0, true));
        this.buyPriceField.setInputListener((keyChar, keyCode)->this.confirmButton.setEnabled(!this.amountField.getTypedText().isEmpty() && !this.buyPriceField.getTypedText().isEmpty() && !this.sellingPriceField.getTypedText().isEmpty()));

        this.addElement(new OxygenTextLabel(6, 150, ClientReference.localize("oxygen_merchants.gui.management.sellingPrice"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));
        this.addElement(this.sellingPriceField = new OxygenNumberField(6, 152, 45, "", Long.MAX_VALUE, false, 0, true));
        this.sellingPriceField.setInputListener((keyChar, keyCode)->this.confirmButton.setEnabled(!this.amountField.getTypedText().isEmpty() && !this.buyPriceField.getTypedText().isEmpty() && !this.sellingPriceField.getTypedText().isEmpty()));

        this.addElement(this.itemsPanel = new OxygenScrollablePanel(this.screen, 6, 17, this.getWidth() - 12, 16, 1, 36, 5, EnumBaseGUISetting.TEXT_PANEL_SCALE.get().asFloat(), false));   

        this.itemsPanel.<InventoryItemPanelEntry>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (this.currentEntry != clicked) {       
                if (this.currentEntry != null)
                    this.currentEntry.setToggled(false);
                clicked.toggle();    
                this.currentEntry = clicked;
            }
        });

        this.loadItems();

        this.addElement(this.confirmButton = new OxygenButton(15, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen_core.gui.confirm")).disable());
        this.confirmButton.setKeyPressListener(Keyboard.KEY_R, ()->this.confirm());

        this.addElement(this.cancelButton = new OxygenButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen_core.gui.cancel")));
        this.cancelButton.setKeyPressListener(Keyboard.KEY_X, ()->this.close());
    }

    private void loadItems() {
        this.itemsPanel.reset();
        for (ItemStackWrapper wrapper : this.screen.inventoryContent)
            this.itemsPanel.addEntry(new InventoryItemPanelEntry(wrapper.getCachedItemStack()));    

        this.itemsPanel.getScroller().reset();
    }

    @Override
    protected void onOpen() {
        if (this.currentEntry != null) {
            this.currentEntry.setToggled(false);
            this.currentEntry = null;
        }                
        this.confirmButton.disable();

        this.amountField.reset();
        this.buyPriceField.reset();
        this.sellingPriceField.reset();
    }

    private void confirm() {
        MerchantOffer offer = new MerchantOffer(this.section.getCurrentOfferButton().index, ItemStackWrapper.getFromStack(this.currentEntry.getItemStack()));
        offer.setAmount((int) this.amountField.getTypedNumberAsLong());
        offer.setBuyCost(this.buyPriceField.getTypedNumberAsLong());
        offer.setSellingCost(this.sellingPriceField.getTypedNumberAsLong());
        this.section.addOfferToCurrentProfile(offer);
        this.close();
    }
    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) { 
            if (element == this.cancelButton)
                this.close();
            else if (element == this.confirmButton)
                this.confirm();
        }
    }
}
