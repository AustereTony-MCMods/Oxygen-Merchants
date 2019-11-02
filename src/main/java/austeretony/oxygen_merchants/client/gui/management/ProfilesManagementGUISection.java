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
import austeretony.oxygen_core.client.gui.elements.OxygenSorterGUIElement;
import austeretony.oxygen_core.client.gui.elements.OxygenSorterGUIElement.EnumSorting;
import austeretony.oxygen_core.client.gui.elements.SectionsGUIDDList;
import austeretony.oxygen_core.client.gui.settings.GUISettings;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.util.MathUtils;
import austeretony.oxygen_merchants.client.MerchantsManagerClient;
import austeretony.oxygen_merchants.client.gui.management.profiles.GUICurrency;
import austeretony.oxygen_merchants.client.gui.management.profiles.OfferManagementGUIButton;
import austeretony.oxygen_merchants.client.gui.management.profiles.ProfilesSectionGUIFiller;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.CurrencyManagementGUICallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.OfferCreationGUICallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.OfferEditingGUICallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.ProfileCreationGUICallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.ProfileNameEditGUICallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.RemoveProfileGUICallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.callback.SaveChangesGUICallback;
import austeretony.oxygen_merchants.client.gui.management.profiles.context.EditOfferContextAction;
import austeretony.oxygen_merchants.client.gui.management.profiles.context.EditProfileCurrencyContextAction;
import austeretony.oxygen_merchants.client.gui.management.profiles.context.EditProfileNameContextAction;
import austeretony.oxygen_merchants.client.gui.management.profiles.context.OfferCreationContextAction;
import austeretony.oxygen_merchants.client.gui.management.profiles.context.RemoveOfferContextAction;
import austeretony.oxygen_merchants.client.gui.management.profiles.context.RemoveProfileContextAction;
import austeretony.oxygen_merchants.client.gui.merchant.MerchantMenuGUIScreen;
import austeretony.oxygen_merchants.common.MerchantOffer;
import austeretony.oxygen_merchants.common.MerchantProfile;
import net.minecraft.item.ItemStack;

public class ProfilesManagementGUISection extends AbstractGUISection {

    private final ManagementMenuGUIScreen screen;

    private OxygenGUIButton createButton;

    private OxygenGUIButtonPanel profilesPanel;

    private OxygenGUIText profilesAmountTextLabel;

    private OxygenSorterGUIElement profilesNameSorterElement;

    private AbstractGUICallback profileCreationCallback, removeProfileCallback, profileNameEditingCallback, 
    profileCurrencyManagementCallback, offerCreationCallback, offerEditingCallback, saveChangesCallback;

    //profile

    private OxygenGUIText profileNameTextLabel;

    private OxygenGUIButton profileSaveChangesButton, profileOpenButton;

    private GUICurrency profileCurrencyElement;

    private OxygenGUIButtonPanel profileOffersPanel;

    private MerchantProfile changesBuffer;

    //cache 

    private IndexedGUIButton<Long> currentProfileButton;

    private OfferManagementGUIButton currentOfferButton;

    public ProfilesManagementGUISection(ManagementMenuGUIScreen screen) {
        super(screen);
        this.screen = screen;
    }

    @Override
    public void init() {
        this.addElement(new ProfilesSectionGUIFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenGUIText(4, 5, ClientReference.localize("oxygen_merchants.gui.management.title"), GUISettings.get().getTitleScale(), GUISettings.get().getEnabledTextColor()));

        this.addElement(this.profilesAmountTextLabel = new OxygenGUIText(0, 18, "", GUISettings.get().getSubTextScale() - 0.05F, GUISettings.get().getEnabledTextColor()));   

        this.addElement(this.profilesNameSorterElement = new OxygenSorterGUIElement(6, 18, EnumSorting.DOWN, ClientReference.localize("oxygen_merchants.sorting.profile")));   

        this.profilesNameSorterElement.setClickListener((sorting)->this.sortProfiles(sorting == EnumSorting.DOWN ? 0 : 1));

        this.addElement(this.profilesPanel = new OxygenGUIButtonPanel(this.screen, 6, 24, 75, 10, 1, MathUtils.clamp(MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfilesAmount(), 10, 100), 10, GUISettings.get().getPanelTextScale(), true));   

        this.profilesPanel.<IndexedGUIButton<Long>>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (this.currentProfileButton != clicked) {
                if (this.currentProfileButton != null)
                    this.currentProfileButton.setToggled(false);
                clicked.toggle();                    
                this.currentProfileButton = clicked;
                this.loadProfileData(clicked.index);
            }
        });

        this.profilesPanel.initContextMenu(new OxygenGUIContextMenu(GUISettings.get().getContextMenuWidth(), 9, 
                new EditProfileNameContextAction(this),
                new EditProfileCurrencyContextAction(this),
                new OfferCreationContextAction(this),
                new RemoveProfileContextAction(this)));    

        this.addElement(new SectionsGUIDDList(this.getWidth() - 4, 5, this, this.screen.getEntitiesSection()));

        this.addElement(this.createButton = new OxygenGUIButton(22, 137, 40, 10, ClientReference.localize("oxygen_merchants.gui.management.create")));     

        this.profileCreationCallback = new ProfileCreationGUICallback(this.screen, this, 140, 48).enableDefaultBackground();
        this.removeProfileCallback = new RemoveProfileGUICallback(this.screen, this, 140, 38).enableDefaultBackground();
        this.profileNameEditingCallback = new ProfileNameEditGUICallback(this.screen, this, 140, 50).enableDefaultBackground();
        this.profileCurrencyManagementCallback = new CurrencyManagementGUICallback(this.screen, this, 140, 138).enableDefaultBackground();
        this.offerCreationCallback = new OfferCreationGUICallback(this.screen, this, 140, 200);
        this.offerEditingCallback = new OfferEditingGUICallback(this.screen, this, 140, 200);

        this.saveChangesCallback = new SaveChangesGUICallback(this.screen, this, 140, 38).enableDefaultBackground();

        this.initProfileElements();
    }

    private void initProfileElements() {
        this.addElement(this.profileNameTextLabel = new OxygenGUIText(90, 20, "", GUISettings.get().getTextScale(), GUISettings.get().getEnabledTextColor()).disableFull());
        this.addElement(this.profileCurrencyElement = new GUICurrency(90, 20).disableFull());

        this.addElement(this.profileSaveChangesButton = new OxygenGUIButton(90, 137, 40, 10, ClientReference.localize("oxygen_merchants.gui.management.saveChangesButton")).disableFull());     
        this.addElement(this.profileOpenButton = new OxygenGUIButton(134, 137, 40, 10, ClientReference.localize("oxygen_merchants.gui.management.openProfileButton")).disableFull());           

        this.addElement(this.profileOffersPanel = new OxygenGUIButtonPanel(this.screen, 90, 30, this.getWidth() - 99, 16, 1, 100, 6, GUISettings.get().getPanelTextScale(), true));   
        this.profileOffersPanel.disableFull();

        this.profileOffersPanel.<OfferManagementGUIButton>setClickListener((previous, clicked, mouseX, mouseY, mouseButton)->{
            if (this.currentOfferButton != clicked)                
                this.currentOfferButton = clicked;
        });

        this.profileOffersPanel.initContextMenu(new OxygenGUIContextMenu(GUISettings.get().getContextMenuWidth(), 9, 
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
            this.profilesPanel.addButton(new ProfileGUIButton(profile));

        this.profilesAmountTextLabel.setDisplayText(String.valueOf(MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfilesAmount()));     
        this.profilesAmountTextLabel.setX(80 - this.textWidth(this.profilesAmountTextLabel.getDisplayText(), GUISettings.get().getSubTextScale() - 0.05F));

        this.profilesPanel.getScroller().resetPosition();
        this.profilesPanel.getScroller().getSlider().reset();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) { 
            if (element == this.createButton)
                this.profileCreationCallback.open();
            else if (element == this.profileSaveChangesButton)
                this.openSaveChangesCallback();
            else if (element == this.profileOpenButton)
                ClientReference.displayGuiScreen(new MerchantMenuGUIScreen(this.changesBuffer.getId()));
        }
    }

    public void loadProfileData(long profileId) {
        this.changesBuffer = MerchantsManagerClient.instance().getMerchantProfilesContainer().getProfile(profileId).copy();

        this.profileNameTextLabel.enableFull();

        if (this.changesBuffer.isUsingCurrency())
            this.profileCurrencyElement.setUseCurrency();
        else
            this.profileCurrencyElement.setUseItem(this.changesBuffer.getCurrencyStack().getCachedItemStack());
        this.profileCurrencyElement.enable();

        this.loadOffers(this.changesBuffer);
        this.profileOffersPanel.enableFull();
        this.profileOffersPanel.getScroller().getSlider().enableFull();

        this.profileSaveChangesButton.enableFull();
        this.profileOpenButton.enableFull();

        this.profileNameTextLabel.setDisplayText(this.changesBuffer.getName());
        this.profileCurrencyElement.setX(this.profileNameTextLabel.getX() + this.textWidth(this.changesBuffer.getName(), GUISettings.get().getTextScale()) + 4);
    }

    private void loadOffers(MerchantProfile profile) {
        List<MerchantOffer> offers = new ArrayList<>(profile.getOffers());

        Collections.sort(offers, (o1, o2)->(int) ((o1.offerId - o2.offerId) / 5_000L));

        ItemStack currencyStack = profile.isUsingCurrency() ? null : profile.getCurrencyStack().getCachedItemStack();

        this.profileOffersPanel.reset();
        for (MerchantOffer offer : offers)
            this.profileOffersPanel.addButton(new OfferManagementGUIButton(offer, currencyStack));

        this.profileOffersPanel.getScroller().resetPosition();
        this.profileOffersPanel.getScroller().getSlider().reset();
    }

    public void resetProfileData() {
        this.profileNameTextLabel.disableFull();

        this.profileCurrencyElement.disable();

        this.profileOffersPanel.disableFull();
        this.profileOffersPanel.getScroller().getSlider().disableFull();

        this.profileSaveChangesButton.disableFull();
        this.profileOpenButton.disableFull();
    }

    public void profilesSynchronized() {
        this.sortProfiles(0);
    }

    public void profileCreated(MerchantProfile profile) {
        this.sortProfiles(0);
        this.resetProfileData();

        this.profilesNameSorterElement.setSorting(EnumSorting.DOWN);
    }

    public void profileUpdated(MerchantProfile profile) {
        this.sortProfiles(0);
        this.loadProfileData(profile.getId());

        this.profilesNameSorterElement.setSorting(EnumSorting.DOWN);
    }

    public void profileRemoved(MerchantProfile profile) {
        this.sortProfiles(0);
        this.resetProfileData();

        this.profilesNameSorterElement.setSorting(EnumSorting.DOWN);
    }

    public MerchantProfile getCurrentProfileChangesBuffer() {
        return this.changesBuffer;
    }

    public OfferManagementGUIButton getCurrentOfferButton() {
        return this.currentOfferButton;
    }

    public void updateProfileName(String name) {       
        this.changesBuffer.setName(name);
        this.profileNameTextLabel.setDisplayText(name);
        this.profileCurrencyElement.setX(this.profileNameTextLabel.getX() + this.textWidth(this.changesBuffer.getName(), GUISettings.get().getTextScale()) + 4);
    }

    public void setProfileUseCurrency() {
        this.changesBuffer.setUseCurrency(true);
        this.profileCurrencyElement.setUseCurrency();
        this.loadOffers(this.changesBuffer);
    }

    public void setProfileUseItem(ItemStack itemStack) {
        this.changesBuffer.setUseCurrency(false);
        this.changesBuffer.setCurrencyStack(ItemStackWrapper.getFromStack(itemStack));
        this.profileCurrencyElement.setUseItem(itemStack);
        this.loadOffers(this.changesBuffer);
    }

    public void addOfferToCurrentProfile(MerchantOffer offer) {
        this.changesBuffer.addOffer(offer);
        this.loadOffers(this.changesBuffer);
    }

    public void removeOfferFromCurrentProfile(long offerId) {
        this.changesBuffer.removeOffer(offerId);
        this.loadOffers(this.changesBuffer);
    }

    public IndexedGUIButton<Long> getCurrentProfileButton() {
        return this.currentProfileButton;
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
