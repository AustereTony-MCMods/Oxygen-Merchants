package austeretony.oxygen_merchants.client.gui.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import austeretony.alternateui.screen.button.GUIButton;
import austeretony.alternateui.screen.callback.AbstractGUICallback;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.elements.OxygenButton;
import austeretony.oxygen_core.client.gui.elements.OxygenContextMenu;
import austeretony.oxygen_core.client.gui.elements.OxygenCurrencyWidget;
import austeretony.oxygen_core.client.gui.elements.OxygenScrollablePanel;
import austeretony.oxygen_core.client.gui.elements.OxygenSorter;
import austeretony.oxygen_core.client.gui.elements.OxygenSorter.EnumSorting;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.gui.management.profiles.MerchantOfferPanelEntry;
import austeretony.oxygen_merchants.client.gui.management.profiles.MerchantProfilePanelEntry;
import austeretony.oxygen_merchants.client.gui.management.profiles.MerchantProfilesBackgroundFiller;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.CurrencyManagementCallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.OfferCreationCallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.OfferEditingCallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.ProfileCreationCallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.ProfileNameEditCallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.RemoveProfileCallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.SaveChangesCallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.context.EditOfferContextAction;
import austeretony.oxygen_merchants.client.gui.management.profiles.context.EditProfileCurrencyContextAction;
import austeretony.oxygen_merchants.client.gui.management.profiles.context.EditProfileNameContextAction;
import austeretony.oxygen_merchants.client.gui.management.profiles.context.LinkIdToChatContextAction;
import austeretony.oxygen_merchants.client.gui.management.profiles.context.OfferCreationContextAction;
import austeretony.oxygen_merchants.client.gui.management.profiles.context.RemoveOfferContextAction;
import austeretony.oxygen_merchants.client.gui.management.profiles.context.RemoveProfileContextAction;
import austeretony.oxygen_merchants.client.gui.merchant.MerchantScreen;
import austeretony.oxygen_merchants.common.MerchantOffer;
import austeretony.oxygen_merchants.common.MerchantProfile;
import net.minecraft.item.ItemStack;

public class MerchantProfilesSection extends AbstractGUISection {

    private final ManagementScreen screen;

    private OxygenButton createButton;

    private OxygenScrollablePanel profilesPanel;

    private OxygenTextLabel profilesAmountTextLabel;

    private OxygenSorter profilesNameSorter;

    private AbstractGUICallback profileCreationCallback, removeProfileCallback, profileNameEditingCallback, 
    profileCurrencyManagementCallback, offerCreationCallback, offerEditingCallback, saveChangesCallback;

    //profile

    private OxygenTextLabel profileNameTextLabel, profileOffersAmountTextLabel;

    private OxygenButton profileSaveChangesButton, profileOpenButton;

    private OxygenCurrencyWidget profileCurrency;

    private OxygenScrollablePanel profileOffersPanel;

    //cache 

    private MerchantProfile cachedProfile;

    private MerchantProfilePanelEntry currentProfileEntry;

    private MerchantOfferPanelEntry currentOfferButton;

    public MerchantProfilesSection(ManagementScreen screen) {
        super(screen);
        this.screen = screen;
        this.setDisplayText(ClientReference.localize("oxygen_merchants.gui.management.profiles.title"));
    }

    @Override
    public void init() {
        this.addElement(new MerchantProfilesBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_merchants.gui.management.title"), EnumBaseGUISetting.TEXT_TITLE_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.profilesAmountTextLabel = new OxygenTextLabel(0, 22, "", EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));   

        this.addElement(this.profilesNameSorter = new OxygenSorter(6, 18, EnumSorting.DOWN, ClientReference.localize("oxygen_core.gui.name")));   
        this.profilesNameSorter.setClickListener((sorting)->this.sortProfiles(sorting == EnumSorting.DOWN ? 0 : 1));

        this.addElement(this.profilesPanel = new OxygenScrollablePanel(this.screen, 6, 25, 75, 10, 1, 100, 10, EnumBaseGUISetting.TEXT_PANEL_SCALE.get().asFloat(), true));   

        this.profilesPanel.<MerchantProfilePanelEntry>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (this.currentProfileEntry != clicked) {
                if (this.currentProfileEntry != null)
                    this.currentProfileEntry.setToggled(false);
                clicked.toggle();                    
                this.currentProfileEntry = clicked;
                this.loadProfileData(clicked.index);
            }
        });

        this.profilesPanel.initContextMenu(new OxygenContextMenu( 
                new EditProfileNameContextAction(this),
                new EditProfileCurrencyContextAction(this),
                new OfferCreationContextAction(this),
                new RemoveProfileContextAction(this),
                new LinkIdToChatContextAction(this)));    

        this.addElement(this.createButton = new OxygenButton(6, this.getHeight() - 11, 40, 10, ClientReference.localize("oxygen_core.gui.create")));     

        this.initProfileElements();

        this.profileCreationCallback = new ProfileCreationCallback(this.screen, this, 140, 48).enableDefaultBackground();
        this.removeProfileCallback = new RemoveProfileCallback(this.screen, this, 140, 38).enableDefaultBackground();
        this.profileNameEditingCallback = new ProfileNameEditCallback(this.screen, this, 140, 50).enableDefaultBackground();
        this.profileCurrencyManagementCallback = new CurrencyManagementCallback(this.screen, this, 140, 148).enableDefaultBackground();
        this.offerCreationCallback = new OfferCreationCallback(this.screen, this, 140, 176);
        this.offerEditingCallback = new OfferEditingCallback(this.screen, this, 140, 176);
        this.saveChangesCallback = new SaveChangesCallback(this.screen, this, 140, 38).enableDefaultBackground();
    }

    private void initProfileElements() {
        this.addElement(this.profileNameTextLabel = new OxygenTextLabel(90, 25, "", EnumBaseGUISetting.TEXT_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()).disableFull());
        this.addElement(this.profileCurrency = new OxygenCurrencyWidget(90, 18).disableFull());
        this.addElement(this.profileOffersAmountTextLabel = new OxygenTextLabel(0, 25, "", EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()).disableFull());

        this.addElement(this.profileSaveChangesButton = new OxygenButton(90, this.getHeight() - 11, 40, 10, ClientReference.localize("oxygen_merchants.gui.management.saveChangesButton")).disableFull());     
        this.addElement(this.profileOpenButton = new OxygenButton(134, this.getHeight() - 11, 40, 10, ClientReference.localize("oxygen_merchants.gui.management.openProfileButton")).disableFull());           

        this.addElement(this.profileOffersPanel = new OxygenScrollablePanel(this.screen, 90, 33, this.getWidth() - 99, 16, 1, 100, 6, EnumBaseGUISetting.TEXT_PANEL_SCALE.get().asFloat(), true));   
        this.profileOffersPanel.disableFull();

        this.profileOffersPanel.<MerchantOfferPanelEntry>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (this.currentOfferButton != clicked)                
                this.currentOfferButton = clicked;
        });

        this.profileOffersPanel.initContextMenu(new OxygenContextMenu(
                new EditOfferContextAction(this),
                new RemoveOfferContextAction(this))); 
    }

    private void sortProfiles(int mode) {
        List<MerchantProfile> profiles = new ArrayList<>(MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfiles());

        if (mode == 0)
            Collections.sort(profiles, (p1, p2)->p1.getName().compareTo(p2.getName()));
        else
            Collections.sort(profiles, (p1, p2)->p2.getName().compareTo(p1.getName()));

        this.profilesPanel.reset();
        for (MerchantProfile profile : profiles)
            this.profilesPanel.addEntry(new MerchantProfilePanelEntry(profile));

        this.profilesAmountTextLabel.setDisplayText(String.valueOf(profiles.size()));     
        this.profilesAmountTextLabel.setX(84 - this.textWidth(this.profilesAmountTextLabel.getDisplayText(), this.profilesAmountTextLabel.getTextScale()));

        this.profilesPanel.getScroller().reset();
        this.profilesPanel.getScroller().updateRowsAmount(MathUtils.clamp(profiles.size(), 10, MathUtils.greaterOfTwo(profiles.size(), 100)));
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) { 
            if (element == this.createButton)
                this.profileCreationCallback.open();
            else if (element == this.profileSaveChangesButton)
                this.openSaveChangesCallback();
            else if (element == this.profileOpenButton)
                ClientReference.displayGuiScreen(new MerchantScreen(this.cachedProfile.getId()));
        }
    }

    public void loadProfileData(long profileId) {
        this.cachedProfile = MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfile(profileId).copy();

        this.profileNameTextLabel.enableFull();

        if (this.cachedProfile.isUsingVirtalCurrency())
            this.profileCurrency.setCurrency(this.cachedProfile.getCurrencyIndex());
        else
            this.profileCurrency.setCurrency(this.cachedProfile.getCurrencyStack().getCachedItemStack());
        this.profileCurrency.enable();

        this.profileOffersAmountTextLabel.enableFull();

        this.loadOffers(this.cachedProfile);
        this.profileOffersPanel.enableFull();

        this.profileSaveChangesButton.enableFull();
        this.profileOpenButton.enableFull();

        this.profileNameTextLabel.setDisplayText(this.cachedProfile.getName());
        this.profileCurrency.setX(this.profileNameTextLabel.getX() + this.textWidth(this.profileNameTextLabel.getDisplayText(), this.profileNameTextLabel.getTextScale()) + 4);
    }

    private void loadOffers(MerchantProfile profile) {
        List<MerchantOffer> offers = new ArrayList<>(profile.getOffers());

        Collections.sort(offers, (o1, o2)->o1.offerId < o2.offerId ? - 1 : o1.offerId > o2.offerId ? 1 : 0);

        ItemStack currencyStack = profile.isUsingVirtalCurrency() ? null : profile.getCurrencyStack().getCachedItemStack();

        this.profileOffersPanel.reset();
        for (MerchantOffer offer : offers)
            this.profileOffersPanel.addEntry(new MerchantOfferPanelEntry(offer, currencyStack));

        this.profileOffersAmountTextLabel.setDisplayText(String.valueOf(offers.size()));     
        this.profileOffersAmountTextLabel.setX(this.getWidth() - 6 - this.textWidth(this.profileOffersAmountTextLabel.getDisplayText(), this.profileOffersAmountTextLabel.getTextScale()));

        this.profileOffersPanel.getScroller().reset();
        this.profileOffersPanel.getScroller().updateRowsAmount(MathUtils.clamp(offers.size(), 6, MathUtils.greaterOfTwo(offers.size(), 100)));
    }

    public void resetProfileData() {
        this.profileNameTextLabel.disableFull();

        this.profileCurrency.disable();
        this.profileOffersAmountTextLabel.disableFull();

        this.profileOffersPanel.disableFull();

        this.profileSaveChangesButton.disableFull();
        this.profileOpenButton.disableFull();
    }

    public void profilesSynchronized() {
        this.sortProfiles(0);
    }

    public void profileCreated(MerchantProfile profile) {
        this.sortProfiles(0);
        this.resetProfileData();

        this.profilesNameSorter.setSorting(EnumSorting.DOWN);
    }

    public void profileUpdated(MerchantProfile profile) {
        this.sortProfiles(0);
        MerchantProfilePanelEntry profileEntry;
        for (GUIButton button : this.profilesPanel.buttonsBuffer) {
            profileEntry = (MerchantProfilePanelEntry) button;
            if (profileEntry.index == profile.getId()) {
                profileEntry.toggle();
                this.currentProfileEntry = profileEntry;
            }
        }
        this.loadProfileData(profile.getId());

        this.profilesNameSorter.setSorting(EnumSorting.DOWN);
    }

    public void profileRemoved(MerchantProfile profile) {
        this.sortProfiles(0);
        this.resetProfileData();

        this.profilesNameSorter.setSorting(EnumSorting.DOWN);
    }

    public MerchantProfile getCurrentProfileChangesBuffer() {
        return this.cachedProfile;
    }

    public MerchantOfferPanelEntry getCurrentOfferButton() {
        return this.currentOfferButton;
    }

    public void updateProfileName(String name) {       
        this.cachedProfile.setName(name);
        this.profileNameTextLabel.setDisplayText(name);
        this.profileCurrency.setX(this.profileNameTextLabel.getX() + this.textWidth(this.cachedProfile.getName(), this.profileNameTextLabel.getTextScale()) + 4);
    }

    public void setProfileUseCurrency(int currencyIndex) {
        this.cachedProfile.setCurrencyIndex(currencyIndex);
        this.profileCurrency.setCurrency(currencyIndex);
        this.loadOffers(this.cachedProfile);
    }

    public void setProfileUseItem(ItemStack itemStack) {
        this.cachedProfile.setCurrencyStack(ItemStackWrapper.getFromStack(itemStack));
        this.profileCurrency.setCurrency(itemStack);
        this.loadOffers(this.cachedProfile);
    }

    public void addOfferToCurrentProfile(MerchantOffer offer) {
        this.cachedProfile.addOffer(offer);
        this.loadOffers(this.cachedProfile);
    }

    public void removeOfferFromCurrentProfile(long offerId) {
        this.cachedProfile.removeOffer(offerId);
        this.loadOffers(this.cachedProfile);
    }

    public MerchantProfilePanelEntry getCurrentProfileEntry() {
        return this.currentProfileEntry;
    }

    public void openRemoveProfileCallback() {
        this.removeProfileCallback.open();
    }

    public void openProfileNameEditingCallback() {
        this.profileNameEditingCallback.open();
    }

    public void openProfileCurrencyManagementCallback() {
        this.profileCurrencyManagementCallback.open();
    }

    public void openOfferCreationCallback() {
        this.offerCreationCallback.open();
    }

    public void openOfferEditingCallback() {
        this.offerEditingCallback.open();
    }

    public void openSaveChangesCallback() {
        this.saveChangesCallback.open();
    }
}
