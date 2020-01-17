package austeretony.oxygen_merchants.client.gui.management.profiles.callback;

import org.lwjgl.input.Keyboard;

import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.elements.OxygenButton;
import austeretony.oxygen_core.client.gui.elements.OxygenCallbackBackgroundFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenTextField;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.gui.management.ManagementScreen;
import austeretony.oxygen_merchants.client.gui.management.MerchantProfilesSection;
import austeretony.oxygen_merchants.common.MerchantProfile;

public class ProfileCreationCallback extends AbstractGUICallback {

    private final ManagementScreen screen;

    private final MerchantProfilesSection section;

    private OxygenTextField nameField;

    private OxygenButton confirmButton, cancelButton;

    public ProfileCreationCallback(ManagementScreen screen, MerchantProfilesSection section, int width, int height) {
        super(screen, section, width, height);
        this.screen = screen;
        this.section = section;
    }

    @Override
    public void init() {
        this.enableDefaultBackground(EnumBaseGUISetting.FILL_CALLBACK_COLOR.get().asInt());
        this.addElement(new OxygenCallbackBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_merchants.gui.management.callback.profileCreation"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));
        this.addElement(new OxygenTextLabel(6, 23, ClientReference.localize("oxygen_merchants.gui.management.callback.profileCreation.request"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.nameField = new OxygenTextField(6, 25, this.getWidth() - 12, MerchantProfile.MAX_PROFILE_NAME_LENGTH, ""));
        this.nameField.setInputListener((keyChar, keyCode)->this.confirmButton.setEnabled(!this.nameField.getTypedText().isEmpty()));

        this.addElement(this.confirmButton = new OxygenButton(15, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen_core.gui.confirm")).disable());
        this.confirmButton.setKeyPressListener(Keyboard.KEY_R, ()->this.confirm(false));

        this.addElement(this.cancelButton = new OxygenButton(this.getWidth() - 55, this.getHeight() - 12, 40, 10, ClientReference.localize("oxygen_core.gui.cancel")));
        this.cancelButton.setKeyPressListener(Keyboard.KEY_X, ()->this.close(false));
    }

    @Override
    protected void onClose() {
        this.nameField.reset();
    }

    private void confirm(boolean mouseClick) {
        if (mouseClick || !this.nameField.isDragged()) {
            MerchantsManagerClient.instance().getMerchantProfilesManager().createProfileSynced(this.nameField.getTypedText());
            this.close();
        }
    }

    private void close(boolean mouseClick) {
        if (mouseClick || !this.nameField.isDragged())
            this.close();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) { 
            if (element == this.cancelButton)
                this.close(true);
            else if (element == this.confirmButton)
                this.confirm(true);
        }
    }
}
