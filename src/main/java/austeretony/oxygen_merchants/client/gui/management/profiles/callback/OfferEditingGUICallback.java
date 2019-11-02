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
import austeretony.oxygen_core.client.gui.elements.OxygenGUITextField;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_merchants.client.gui.management.ManagementMenuGUIScreen;
import austeretony.oxygen_merchants.client.gui.management.ProfilesManagementGUISection;
import austeretony.oxygen_merchants.client.gui.management.profiles.InventoryItemGUIButton;
import austeretony.oxygen_merchants.common.MerchantOffer;
import austeretony.oxygen_merchants.common.MerchantProfile;

public class OfferEditingGUICallback extends AbstractGUICallback {

    private final ManagementMenuGUIScreen screen;

    private final ProfilesManagementGUISection section; 

    private OxygenGUIButton confirmButton, cancelButton;

    private OxygenGUIButtonPanel itemsPanel;

    private OxygenGUITextField amountField, buyCostField, sellingCostField;

    private OxygenCheckBoxGUIButton enableSellingButton, sellingOnlyButton;

    private InventoryItemGUIButton currentButton;

    public OfferEditingGUICallback(ManagementMenuGUIScreen screen, ProfilesManagementGUISection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;   
        this.section = section;
    }

    @Override
    public void init() {
        this.addElement(new OxygenCallbackGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenGUIText(4, 5, ClientReference.localize("oxygen_merchants.gui.management.callback.editOffer"), GUISettings.get().getTextScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(new OxygenGUIText(6, 105, ClientReference.localize("oxygen_merchants.gui.management.amount"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColor()));
        this.addElement(this.amountField = new OxygenGUITextField(6, 112, 45, 9, MerchantProfile.MAX_PROFILE_NAME_LENGTH, "", 3, true, Integer.MAX_VALUE));

        this.addElement(new OxygenGUIText(6, 126, ClientReference.localize("oxygen_merchants.gui.management.cost"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColor()));
        this.addElement(this.buyCostField = new OxygenGUITextField(6, 132, 45, 9, MerchantProfile.MAX_PROFILE_NAME_LENGTH, "", 3, true, Integer.MAX_VALUE));

        this.addElement(this.enableSellingButton = new OxygenCheckBoxGUIButton(6, 146));
        this.addElement(new OxygenGUIText(14, 147, ClientReference.localize("oxygen_merchants.gui.management.enableSelling"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(new OxygenGUIText(6, 157, ClientReference.localize("oxygen_merchants.gui.management.cost"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColor()));
        this.addElement(this.sellingCostField = new OxygenGUITextField(6, 163, 45, 9, MerchantProfile.MAX_PROFILE_NAME_LENGTH, "", 3, true, Integer.MAX_VALUE));

        this.addElement(this.sellingOnlyButton = new OxygenCheckBoxGUIButton(6, 177));
        this.addElement(new OxygenGUIText(14, 178, ClientReference.localize("oxygen_merchants.gui.management.sellingOnly"), GUISettings.get().getSubTextScale(), GUISettings.get().getEnabledTextColor()));    

        this.addElement(this.itemsPanel = new OxygenGUIButtonPanel(this.screen, 6, 17, this.getWidth() - 12, 16, 1, 36, 5, GUISettings.get().getPanelTextScale(), false));   

        this.itemsPanel.<InventoryItemGUIButton>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (this.currentButton != clicked) {       
                if (this.currentButton != null)
                    this.currentButton.setToggled(false);
                clicked.toggle();    
                this.currentButton = clicked;

                this.confirmButton.enable();
            }
        });

        this.loadItems();

        this.addElement(this.confirmButton = new OxygenGUIButton(15, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen.gui.confirmButton")));
        this.addElement(this.cancelButton = new OxygenGUIButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen.gui.cancelButton")));
    }

    @Override
    protected void onOpen() {
        if (this.currentButton != null) {
            this.currentButton.setToggled(false);
            this.currentButton = null;
        }

        this.amountField.setText("1");
        this.buyCostField.setText("0");
        this.enableSellingButton.setToggled(false);
        this.sellingCostField.setText("0");
        this.sellingCostField.disable();
        this.sellingOnlyButton.setToggled(false);
        this.sellingOnlyButton.disable();
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
                MerchantOffer offer = new MerchantOffer(this.section.getCurrentOfferButton().index, ItemStackWrapper.getFromStack(this.currentButton.getItemStack()));
                offer.setAmount((int) this.amountField.getTypedNumber());
                offer.setBuyCost(this.buyCostField.getTypedNumber());
                offer.setSellingEnabled(this.enableSellingButton.isToggled());
                offer.setSellingOnly(this.sellingOnlyButton.isToggled());
                offer.setSellingCost(this.sellingCostField.getTypedNumber());
                this.section.addOfferToCurrentProfile(offer);
                this.close();
            } else if (element == this.enableSellingButton) {
                if (this.enableSellingButton.isToggled()) {
                    this.sellingCostField.enable();
                    this.sellingOnlyButton.enable();
                } else {
                    this.sellingCostField.disable();
                    this.sellingOnlyButton.disable();
                    this.sellingOnlyButton.setToggled(false);
                }
            }
        }
    }
}
