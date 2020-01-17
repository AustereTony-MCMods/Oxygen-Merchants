package austeretony.oxygen_merchants.client.gui.settings;

import austeretony.alternateui.screen.framework.GUIElementsFramework;
import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.elements.OxygenDropDownList;
import austeretony.oxygen_core.client.gui.elements.OxygenDropDownList.OxygenDropDownListEntry;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.client.gui.settings.ElementsContainer;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetColorCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetKeyCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetOffsetCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetScaleCallback;
import austeretony.oxygen_merchants.client.setting.gui.EnumMerchantsGUISetting;

public class MerchantsSettingsContainer implements ElementsContainer {

    //interface

    private OxygenDropDownList alignmentFriendsList;

    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_merchants.gui.settings.module.merchants");
    }

    @Override
    public boolean hasCommonSettings() {
        return true;
    }

    @Override
    public boolean hasGUISettings() {
        return true;
    }

    @Override
    public void addCommon(GUIElementsFramework framework) {}

    @Override
    public void addGUI(GUIElementsFramework framework) {
        framework.addElement(new OxygenTextLabel(68, 25, ClientReference.localize("oxygen_core.gui.settings.option.alignment"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //merchant menu alignment

        String currAlignmentStr;
        switch (EnumMerchantsGUISetting.MERCHANT_MENU_ALIGNMENT.get().asInt()) {
        case - 1: 
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.left");
            break;
        case 0:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.center");
            break;
        case 1:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.right");
            break;    
        default:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.center");
            break;
        }
        framework.addElement(this.alignmentFriendsList = new OxygenDropDownList(68, 35, 55, currAlignmentStr));
        this.alignmentFriendsList.addElement(new OxygenDropDownListEntry<Integer>(- 1, ClientReference.localize("oxygen_core.alignment.left")));
        this.alignmentFriendsList.addElement(new OxygenDropDownListEntry<Integer>(0, ClientReference.localize("oxygen_core.alignment.center")));
        this.alignmentFriendsList.addElement(new OxygenDropDownListEntry<Integer>(1, ClientReference.localize("oxygen_core.alignment.right")));

        this.alignmentFriendsList.<OxygenDropDownListEntry<Integer>>setClickListener((element)->{
            EnumMerchantsGUISetting.MERCHANT_MENU_ALIGNMENT.get().setValue(String.valueOf(element.index));
            OxygenManagerClient.instance().getClientSettingManager().changed();
        });

        framework.addElement(new OxygenTextLabel(68, 33, ClientReference.localize("oxygen_merchants.gui.settings.option.alignmentMerchantMenu"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));
    }

    @Override
    public void resetCommon() {}

    @Override
    public void resetGUI() {
        //merchant menu alignment
        this.alignmentFriendsList.setDisplayText(ClientReference.localize("oxygen_core.alignment.center"));
        EnumMerchantsGUISetting.MERCHANT_MENU_ALIGNMENT.get().reset();

        OxygenManagerClient.instance().getClientSettingManager().changed();
    }

    @Override
    public void initSetColorCallback(SetColorCallback callback) {}

    @Override
    public void initSetScaleCallback(SetScaleCallback callback) {}

    @Override
    public void initSetOffsetCallback(SetOffsetCallback callback) {}

    @Override
    public void initSetKeyCallback(SetKeyCallback callback) {}
}
