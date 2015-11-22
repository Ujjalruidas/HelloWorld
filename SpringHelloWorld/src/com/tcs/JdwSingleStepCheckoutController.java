package com.tcs;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.AddressValidator;
import de.hybris.platform.acceleratorstorefrontcommons.forms.verification.AddressVerificationResultHandler;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;
import de.hybris.platform.storelocator.route.DistanceAndRoute;
import de.hybris.platform.storelocator.location.impl.DefaultLocation;
import de.hybris.platform.storelocator.location.DistanceAwareLocation;
import de.hybris.platform.store.BaseStoreModel;
import co.uk.jdwilliams.jdwcheckoutaddon.util.JDWLocation;
import co.uk.jdwilliams.jdwcheckoutaddon.datas.JdwSearchAddressForm;
import co.uk.jdwilliams.jdwcheckoutaddon.datas.JsonData;
import co.uk.jdwilliams.jdwcheckoutaddon.facades.LocationServiceFacades;
import co.uk.jdwilliams.storefront.forms.validation.JdwAddressValidator;


import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.location.LocationMapService;
import de.hybris.platform.storelocator.location.LocationService;
import de.hybris.platform.storelocator.location.impl.DefaultLocationMapService;
import de.hybris.platform.storelocator.location.impl.DefaultLocationService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.uk.jdwilliams.jdwcheckoutaddon.util.JDWLocation;
import co.uk.jdwilliams.jdwcheckoutaddon.datas.JdwSearchAddressForm;
import co.uk.jdwilliams.jdwcheckoutaddon.datas.JsonData;
import co.uk.jdwilliams.facades.flow.impl.JDWUserFacade;
import co.uk.jdwilliams.jdwcheckoutaddon.facades.LocationServiceFacades;
import co.uk.jdwilliams.jdwcheckoutaddon.constants.JdwcheckoutaddonConstants;
import co.uk.jdwilliams.jdwcheckoutaddon.controllers.JdwcheckoutaddonControllerConstants;
import co.uk.jdwilliams.jdwcheckoutaddon.facades.JdwPaymentDetailsFacade;
import co.uk.jdwilliams.jdwcheckoutaddon.facades.impl.JdwAcceleratorCheckoutFacade;
import co.uk.jdwilliams.jdwcheckoutaddon.facades.impl.JdwDeliveryDetailsFacade;
import co.uk.jdwilliams.jdwcheckoutaddon.util.CSUtil;
import co.uk.jdwilliams.jdwcheckoutaddon.util.JDWCheckoutAddonUtil;
import co.uk.jdwilliams.jdwcheckoutaddon.util.SignatureUtil;
import co.uk.jdwilliams.jdwfacades.data.PaymentTransactionData;
import co.uk.jdwilliams.jdwpaymentservices.JdwPaymentService;
import co.uk.jdwilliams.storefront.constants.JDWConstants;
import co.uk.jdwilliams.storefront.forms.JdwAddressForm;
import co.uk.jdwilliams.storefront.forms.validation.JdwAddressValidator;
import co.uk.jdwilliams.storefront.util.JDWDeliveryAddressUtil;


/**
 * @author Gautam Kumar
 * 
 *         This class is written to customise the checkout steps written in b2ccheckoutaddon.Main funtionality of this
 *         class is checking the delivery address, changing delivery address and customer's phone number and manage
 *         checkout data.
 */

@Controller
@RequestMapping(value = JdwcheckoutaddonControllerConstants.CHECK_MULTI_DELIVERY_ADDRESS)
public class JdwSingleStepCheckoutController extends AbstractCheckoutController
{
	private static final Logger LOG = Logger.getLogger(JdwSingleStepCheckoutController.class);

	@Autowired
	private SessionService sessionService;
//	
//	@Autowired
//	private LocationServiceFacades locationServiceFacades;
//	
	
	@Resource(name = "locationServiceFacades")
	private LocationServiceFacades locationServiceFacades;
	
	@Autowired
	BaseStoreService baseStoreService;

	@Autowired
	private DefaultLocationService defaultLocationService;
	
	@Autowired
	DefaultLocationMapService defaultLocationMapService;
	
	@Autowired
	LocationMapService locationMapService;
	
	
	@Autowired
	private JDWCheckoutAddonUtil jdwCheckoutAddonUtil;
	

	@Resource(name = "jdwDeliveryDetailsFacade")
	private JdwDeliveryDetailsFacade jdwDeliveryDetailsFacade;

	@Resource(name = "userFacade")
	protected UserFacade userFacade;

	@Resource(name = "jdwUserFacade")
	protected JDWUserFacade jdwUserFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource
	private JdwPaymentDetailsFacade jdwPaymentDetailsFacade;

	@Resource(name = "jdwAcceleratorCheckoutFacade")
	private JdwAcceleratorCheckoutFacade jdwAcceleratorCheckoutFacade;

	@Resource
	private ConfigurationService configurationService;

	@Resource
	private JdwPaymentService jdwPaymentService;

	@Resource(name = "addressValidator")
	private AddressValidator addressValidator;

	@Resource(name = "addressVerificationResultHandler")
	private AddressVerificationResultHandler addressVerificationResultHandler;

	@Autowired
	private UserService userService;
	
	@Autowired
	private JDWDeliveryAddressUtil jdwDeliveryAddressUtil;
	
	@Autowired
	private JdwAddressValidator JdwAddressValidator;
	
	@Autowired
	private CartService cartService;

	private final static String DEFAULT_LAST_NAME = "lastName";
	private static final String ZERO = "0";
	private static final boolean IS_CVV_ENABLED = Config.getBoolean(JdwcheckoutaddonConstants.SHOW_CVV_FIELD, false);
	private static final Boolean IGNORE_AVS_RESULT = Boolean.valueOf(Config.getBoolean(JdwcheckoutaddonConstants.RULE_IGNORE_AVS_RESULT, false));
	private static final Boolean IGNORE_CVS_RESULT = Boolean.valueOf(Config.getBoolean(JdwcheckoutaddonConstants.RULE_IGNORE_CVS_RESULT, false));
	protected static final String REDIRECT_CARTPAGE_BACK_BUTTON =REDIRECT_PREFIX + "/cart";
	protected static final String CALL_BACK_UPDATE_URL ="jdwcheckoutaddon.checkout.cybersource.callback.update.url";
	protected static final Map<String, String> cybersourceSopCardTypes = new HashMap<String, String>();
	protected static final String MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL = "multiStepCheckoutSummary";
	protected static final String REDIRECT_URL_ADD_DELIVERY_ADDRESS = REDIRECT_PREFIX + "/checkout/multi/delivery-address/add";
	final static String SIGNED_FIELD_NAMES_VALUE = "access_key," + "profile_id," + "transaction_uuid," + "signed_field_names,"
			+ "unsigned_field_names," + "signed_date_time," + "locale," + "transaction_type," + "reference_number,"
			+ "amount,currency," + "payment_method," + "bill_to_forename," + "bill_to_surname," + "bill_to_email,"
			+ "bill_to_address_line1," + "bill_to_address_city," + JdwcheckoutaddonConstants.IGNORE_AVS_RESULT_KEY + "," + JdwcheckoutaddonConstants.IGNORE_CVN_RESULT_KEY + ","
			+ "bill_to_address_country," + "bill_to_address_postal_code," + "override_custom_receipt_page";

	
	@Value("#{configurationService.configuration.getProperty('cybersource.auth.accept.codes')}")
	private String acceptCodes;

	@Value("#{configurationService.configuration.getProperty('cybersource.auth.decline.codes')}")
	private String declineCodes;

	@ModelAttribute("months")
	public List<SelectOption> getMonths()
	{
		final List<SelectOption> months = new ArrayList<SelectOption>();
		String prefix = ZERO;
		for (int i = 1; i <= 12; i++)
		{
			if (i >= 10)
			{
				prefix = JdwcheckoutaddonConstants.BLANK_SPACE;
			}
			months.add(new SelectOption(String.valueOf(i), prefix + String.valueOf(i)));
		}

		return months;
	}

	@ModelAttribute("startYears")
	public List<SelectOption> getStartYears()
	{
		final List<SelectOption> startYears = new ArrayList<SelectOption>();
		final Calendar calender = new GregorianCalendar();

		for (int i = calender.get(Calendar.YEAR); i > (calender.get(Calendar.YEAR) - 6); i--)
		{
			startYears.add(new SelectOption(String.valueOf(i), String.valueOf(i)));
		}

		return startYears;
	}

	@ModelAttribute("expiryYears")
	public List<SelectOption> getExpiryYears()
	{
		final List<SelectOption> expiryYears = new ArrayList<SelectOption>();
		final Calendar calender = new GregorianCalendar();

		for (int i = calender.get(Calendar.YEAR); i < (calender.get(Calendar.YEAR) + 8); i++)
		{
			expiryYears.add(new SelectOption(String.valueOf(i), String.valueOf(i)));
		}

		return expiryYears;
	}

	@ModelAttribute("countries")
	public Collection<CountryData> getCountries()
	{
		return getCheckoutFacade().getDeliveryCountries();
	}
	
	@ModelAttribute("standardDeliveryLowerLimit")
	public String getStandardDeliveryLowerLimit()
	{
		return configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.STANDARD_DELIVERY_LOWER_LIMIT);
	}

	/**
	 * This method is overridden to render the checkout page.
	 * 
	 */
	@RequestMapping(value = JdwcheckoutaddonControllerConstants.ADD, method = RequestMethod.GET)
	@RequireHardLogIn
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		CartData cartData;
		final Map<String, Double> modifiedProductPrice = sessionService
				.getAttribute(JdwcheckoutaddonControllerConstants.MODIFIED_PRICES_OF_CART);

		final BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
		if (null != currentBaseSite)
		{
			final MediaModel checkoutSitelogo = currentBaseSite.getCheckoutsitelogo();
			final MediaModel checkoutFooterlogo = currentBaseSite.getCheckoutfooterlogo();
			final String securityPageUrl = currentBaseSite.getSecurityPageUrl();
			if (null != checkoutSitelogo)
			{
				final String checkoutSiteLogo = checkoutSitelogo.getURL();
				model.addAttribute("checkoutSiteLogo", checkoutSiteLogo);
			}
			if (null != checkoutFooterlogo)
			{
				final String checkoutFooterLogo = checkoutFooterlogo.getURL();
				model.addAttribute("checkoutFooterLogo", checkoutFooterLogo);
			}
			if (null != securityPageUrl)
			{

				model.addAttribute("securityPageUrl", securityPageUrl);
			}
		}

		if (null != modifiedProductPrice && !modifiedProductPrice.isEmpty())
		{
			cartData = jdwCheckoutAddonUtil.populateCartData(modifiedProductPrice);
			GlobalMessages.addInfoMessage(model, JdwcheckoutaddonConstants.CART_PRICE_MODIFIED);
		}
		else
		{
			cartData = getCheckoutFacade().getCheckoutCart();
		}
		
		if (null != cartData)
		{
			jdwCheckoutAddonUtil.sortCartDataEntries(cartData);
		}

		String resultView = JdwcheckoutaddonControllerConstants.ERROR;
		getCheckoutFacade().setDeliveryAddressIfAvailable();
		
		cartData = getCheckoutFacade().getCheckoutCart();

		boolean editShipmentAddress = true;
		if (null != sessionService.getAttribute(JdwcheckoutaddonControllerConstants.EDITSHIPMENTADDRESS))
		{
			editShipmentAddress = ((Boolean) sessionService.getAttribute(JdwcheckoutaddonControllerConstants.EDITSHIPMENTADDRESS))
					.booleanValue();
		}

		if (null != cartData.getDeliveryAddress() && !editShipmentAddress)
		{
			final AddressData defaultDeliveryAddress = cartData.getDeliveryAddress();
			final String selectedAddressCode = defaultDeliveryAddress.getId();
			model.addAttribute(JdwcheckoutaddonControllerConstants.ADDRESSFORM, new JdwAddressForm());
			storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
			prepareDataForCheckoutPage(selectedAddressCode);
			sessionService.setAttribute(JdwcheckoutaddonControllerConstants.EDITSHIPMENTADDRESS, Boolean.TRUE);
			resultView = REDIRECT_URL_ADD_DELIVERY_ADDRESS;
		}
		else
		{
			model.addAttribute(JdwcheckoutaddonControllerConstants.CARTDATA, cartData);
			model.addAttribute(JdwcheckoutaddonControllerConstants.DELIVERYADDRESS,
					getDeliveryAddresses(cartData.getDeliveryAddress()));
			model.addAttribute(JdwcheckoutaddonControllerConstants.NOADDRESS,
					Boolean.valueOf(getCheckoutFlowFacade().hasNoDeliveryAddress()));
			model.addAttribute(JdwcheckoutaddonControllerConstants.ADDRESSFORM, new JdwAddressForm());
			model.addAttribute(JdwcheckoutaddonControllerConstants.SHOWSAVETOADDRESSBOOK, Boolean.TRUE);
			storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
			model.addAttribute(JdwcheckoutaddonControllerConstants.METAROBOTS, JdwcheckoutaddonControllerConstants.NOINDEX_NOFLOW);
			sessionService.setAttribute(JdwcheckoutaddonControllerConstants.EDITSHIPMENTADDRESS, Boolean.TRUE);
			resultView = JdwcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.CompleteCheckoutPage;
			sessionService.removeAttribute(JdwcheckoutaddonControllerConstants.MODIFIED_PRICES_OF_CART);
		}

		final String cartCode = getCartFacade().hasSessionCart() ? getCartFacade().getSessionCart().getCode() : null;

		jdwPaymentService.fillDeviceDetailsInModel(model, getCartFacade().hasSessionCart(), cartCode);

		getCheckoutFacade().setDeliveryModeIfAvailable();
		model.addAttribute(JdwcheckoutaddonControllerConstants.DELIVRY_METHODS, getCheckoutFacade().getSupportedDeliveryModes());
		prepareListOfPhoneNumbers(model, cartData);
		model.addAttribute(cartData);
		addPaymentUrls(model);
		populatePaymentDetails(model, cartData);

		if (!jdwUserFacade.isBillingAddressPresent())
		{
			resultView = JdwcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.customerBillingAddress;
		}
		
		if(null != cartData && null== cartData.getEntries())
		{
			resultView = REDIRECT_CARTPAGE_BACK_BUTTON;
		}

		return resultView;
	}

	/**
	 * This method is used to set billing address.
	 */
	@RequestMapping(value = JdwcheckoutaddonControllerConstants.ADD_BILLING_ADDRESS, method = RequestMethod.POST)
	@RequireHardLogIn
	public String addBillingAddress(@ModelAttribute("addressForm") final JdwAddressForm addressForm,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException
	{
		model.addAttribute(JDWConstants.METAROBOTS, JDWConstants.METAROBOTS_VALUE);
		final BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
		if (null != currentBaseSite)
		{
			final MediaModel mediaModel = currentBaseSite.getCheckoutsitelogo();
			if (null != mediaModel)
			{
				final String checkoutSiteLogo = mediaModel.getURL();
				model.addAttribute("checkoutSiteLogo", checkoutSiteLogo);
			}
		}
		String resultView = JdwcheckoutaddonControllerConstants.ERROR;
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		AddressData newAddress = new AddressData();


		if (null != addressForm)
		{
			newAddress = jdwDeliveryAddressUtil.getAddressDataFromAddressForm(newAddress, addressForm);
			newAddress.setTitleCode(customerData.getTitleCode());
			newAddress.setFirstName(customerData.getFirstName());
			newAddress.setLastName(customerData.getLastName());  
			newAddress.setBillingAddress(true);
			newAddress.setShippingAddress(true);
			newAddress.setDefaultAddress(true);
			newAddress.setVisibleInAddressBook(true);

			getUserFacade().addAddress(newAddress);

			final AddressData previousSelectedAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
			getCheckoutFacade().setDeliveryAddress(newAddress);
			if (previousSelectedAddress != null && !previousSelectedAddress.isVisibleInAddressBook())
			{
				getUserFacade().removeAddress(previousSelectedAddress);
			}
			getCheckoutFacade().setDeliveryAddress(newAddress);
			resultView = REDIRECT_URL_ADD_DELIVERY_ADDRESS;
		}
		else
		{
			resultView = JdwcheckoutaddonControllerConstants.ERROR;
		}

		return resultView;
	}
	
	/**
	 * This method is used handle the get request for my-account add address page.It takes View model as an input and
	 * return countryData and titleData to the view page
	 * 
	 */
	@RequestMapping(value = JdwcheckoutaddonConstants.REQUEST_MAPPING_ADD_ADDRESS_CHECKOUT, method = RequestMethod.GET)
	@RequireHardLogIn
	public String addAddress(final Model model) throws CMSItemNotFoundException
	{
		
		
		
		model.addAttribute(JDWConstants.METAROBOTS, JDWConstants.METAROBOTS_VALUE);
		model.addAttribute(JDWConstants.TITLE_DATA, userFacade.getTitles());
		final JdwAddressForm addressForm = getPopulatedAddressForm();
		model.addAttribute(JDWConstants.ADDRESS_FORM, addressForm);
		model.addAttribute(JDWConstants.ADDRESS_BOOK_EMPTY, Boolean.valueOf(userFacade.isAddressBookEmpty()));
		model.addAttribute(JDWConstants.IS_DEFAULT_ADDRESS, Boolean.FALSE);

		final String resultView = JdwcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.customerDeliveryAddress;

		addAddAddressPageDetailsToModel(model);
		return resultView;
	} 
	
	/**
	 * This method is used to handle the post request for my-account add-address page.It takes addressForm,bindingResult,
	 * view model ,redirectModel and request as inputs and returns view for the address book page.
	 */
	@RequestMapping(value = JdwcheckoutaddonConstants.REQUEST_MAPPING_ADD_ADDRESS_CHECKOUT, method = RequestMethod.POST)
	@RequireHardLogIn
	public String addAddress(@ModelAttribute("addressForm") final JdwAddressForm addressForm, final BindingResult bindingResult,
			final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(JDWConstants.METAROBOTS, JDWConstants.METAROBOTS_VALUE);
		String resultView = JDWConstants.BLANK_SPACE;
		

		
		
		setGoogleAPIProperties();
		getAddressValidator().validate(addressForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			resultView = JdwcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.customerDeliveryAddress;

			addAddAddressPageDetailsToModel(model);

			//If the toggle was open, the toggle needs to remain opened after redirection
			if (!addressForm.getVerifiedAddress().booleanValue())
			{
				model.addAttribute(JDWConstants.EDIT_ADDRESS_FLAG, Boolean.TRUE);
			}


		}
		else
		{
			AddressData newAddress = new AddressData();
			newAddress = jdwDeliveryAddressUtil.getAddressDataFromAddressForm(newAddress, addressForm);

			if (userFacade.isAddressBookEmpty())
			{
				newAddress.setDefaultAddress(true);
				newAddress.setVisibleInAddressBook(true);
			}
			else
			{
				newAddress.setDefaultAddress(addressForm.getDefaultAddress() != null
						&& addressForm.getDefaultAddress().booleanValue());
			}


			newAddress.setBillingAddress(addressForm.getBillingAddress() != null && addressForm.getBillingAddress().booleanValue());

			jdwUserFacade.addAddress(newAddress);

			getCheckoutFacade().setDeliveryAddress(newAddress);

			resultView = REDIRECT_PREFIX + JdwcheckoutaddonControllerConstants.CHECKOUT_FROM_DELIVERY_ADDRESS;


		}
		return resultView;
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * This method is used handle the get request for my-account search  address page.It takes View model as an input and
	 * return countryData and titleData to the view page
	 * 
	 */
	
	@RequestMapping(value = JdwcheckoutaddonConstants.REQUEST_MAPPING_SEARCH_ADDRESS, method = RequestMethod.GET)
	@RequireHardLogIn
	public String searchAddress(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(JDWConstants.METAROBOTS, JDWConstants.METAROBOTS_VALUE);
		model.addAttribute(JDWConstants.TITLE_DATA, userFacade.getTitles());
		final JdwSearchAddressForm searchAddressForm = getPopulatedSearchAddressForm();
		
		model.addAttribute(JDWConstants.SEARCH_ADDRESS_FORM, searchAddressForm);
		model.addAttribute(JDWConstants.ADDRESS_BOOK_EMPTY, Boolean.valueOf(userFacade.isAddressBookEmpty()));
		model.addAttribute(JDWConstants.IS_DEFAULT_ADDRESS, Boolean.FALSE);

		final String resultView = JdwcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.customerSearchAddress;

		//addAddAddressPageDetailsToModel(model);
		return resultView;
	} 
	
	
	/**
	 * @param searchAddressForm
	 * @param addressData
	 * @param name
	 * @param zip
	 * @param city
	 * @param street
	 * @param desc
	 * @param model
	 * @return  method is used to confirm the address location
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(method = RequestMethod.GET, value = JdwcheckoutaddonConstants.SAVE_ADDRESS_LOCATION)
	@RequireHardLogIn
	public String saveAddressLocation(@ModelAttribute("searchAddressForm") final JdwSearchAddressForm searchAddressForm,
			@RequestParam(required = false, value = "addressData") AddressData addressData,			
			@RequestParam(required = false, value = "name") String name,
			@RequestParam(required = false, value = "zip") String zip,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "street") String street,
			@RequestParam(required = false, value = "desc") String desc,			
			final Model model) throws CMSItemNotFoundException
	{
	
	

		AddressData newAddress = new AddressData();
		newAddress.setLine1(city);
		newAddress.setStreet(street);
		newAddress.setPostalCode(zip);
		newAddress.setLine2(desc);
		newAddress.setLine3(name);
		
		newAddress.setDefaultAddress(true);
		newAddress.setVisibleInAddressBook(true);		
		jdwUserFacade.addAddress(newAddress);		
		getCheckoutFacade().setDeliveryAddress(newAddress);
		model.addAttribute(JDWConstants.METAROBOTS, JDWConstants.METAROBOTS_VALUE);		
		model.addAttribute(JDWConstants.SEARCH_ADDRESS_FORM, searchAddressForm);		
		final String resultView = JdwcheckoutaddonControllerConstants.CHECKOUT_FROM_DELIVERY_ADDRESS;		
		return resultView;		
	}
	
	
	

	/**
	 * @param searchAddressForm
	 * @param lat
	 * @param lng
	 * @param model
	 * @return method is used to select from one of the result
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = JdwcheckoutaddonConstants.SELECT_RESULT, method = RequestMethod.GET)
	@RequireHardLogIn
	public String selectResult(@ModelAttribute("searchAddressForm") final JdwSearchAddressForm searchAddressForm,@RequestParam(required = false, value = "lat") final double lat,@RequestParam(required = false, value = "lng") final double lng,final Model model) throws CMSItemNotFoundException
	{
		
		BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
		GPS gps = new DefaultGPS(lat,lng);
		searchAddressForm.setLimitLocationsCount(5);
		de.hybris.platform.storelocator.map.Map locationMap = defaultLocationMapService.getMapOfLocations(gps, searchAddressForm.getLimitLocationsCount(), currentBaseStore);
		List<Location> list = locationMap.getPointsOfInterest();		
		List<JDWLocation> simplybeStoreList = new ArrayList<JDWLocation>();
		
		for (int i = 0; i < list.size(); i++) {
			
			Location loc = list.get(i);
			
			PointOfServiceModel posModel = new PointOfServiceModel();
			posModel.setLatitude(loc.getGPS().getDecimalLatitude());
			posModel.setLongitude(loc.getGPS().getDecimalLongitude());
			double distance = jdwDeliveryAddressUtil.calculateDistance(gps, posModel);			
			JDWLocation jloc = new JDWLocation();
			jloc.setName(loc.getName());
			jloc.setDescription(loc.getDescription());
			jloc.setAddressData(loc.getAddressData());			
			jloc.setDistance(distance);			
			simplybeStoreList.add(jloc);
			}
		
		model.addAttribute("simplybeStores", simplybeStoreList);		
		model.addAttribute(JDWConstants.SEARCH_ADDRESS_FORM, searchAddressForm);
		final String resultView = JdwcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.customerSearchAddressResult;
		return resultView;
	}
	
	
	
	

	/**
	 * @param searchAddressForm
	 * @param addressData
	 * @param name
	 * @param zip
	 * @param city
	 * @param street
	 * @param desc
	 * @param model
	 * @return  method is used to confirm the address location
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = JdwcheckoutaddonConstants.CONFIRM_ADDRESS, method = RequestMethod.GET)
	@RequireHardLogIn
	public String confirmAddress(@ModelAttribute("searchAddressForm") final JdwSearchAddressForm searchAddressForm,
			@RequestParam(required = false, value = "addressData") AddressData addressData,			
			@RequestParam(required = false, value = "name") String name,
			@RequestParam(required = false, value = "zip") String zip,
			@RequestParam(required = false, value = "city") String city,
			@RequestParam(required = false, value = "street") String street,
			@RequestParam(required = false, value = "desc") String desc,			
			final Model model) throws CMSItemNotFoundException
	{
		
		List<JDWLocation> simplybeStoreList = new ArrayList<JDWLocation>();
			JDWLocation jloc = new JDWLocation();
			jloc.setName(name);
			jloc.setDescription(zip);
			jloc.setCity(city);	
			jloc.setStreet(street);	
			jloc.setZip(zip);
			jloc.setType("simplybe");
			simplybeStoreList.add(jloc);
		model.addAttribute("simplybeStores", simplybeStoreList);
		model.addAttribute("addressData", jloc);
		model.addAttribute(JDWConstants.METAROBOTS, JDWConstants.METAROBOTS_VALUE);
		model.addAttribute(JDWConstants.TITLE_DATA, userFacade.getTitles());
		model.addAttribute(JDWConstants.SEARCH_ADDRESS_FORM, searchAddressForm);
		model.addAttribute(JDWConstants.ADDRESS_BOOK_EMPTY, Boolean.valueOf(userFacade.isAddressBookEmpty()));
		model.addAttribute(JDWConstants.IS_DEFAULT_ADDRESS, Boolean.FALSE);
		addAddAddressPageDetailsToModel(model);
		final String resultView = JdwcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.confirmAddress;
		return resultView;
	}
	
	/**
	 * @param searchAddressForm
	 * @param searchTerm
	 * @param model
	 * @return this method is used to search the result
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = JdwcheckoutaddonConstants.REQUEST_MAPPING_SEARCH_RESULT, method = RequestMethod.GET)
	@RequireHardLogIn
	
	
	public String searchResult(@ModelAttribute("searchAddressForm") final JdwSearchAddressForm searchAddressForm,@RequestParam(required = false, value = "searchTerm") final String searchTerm,final Model model) throws CMSItemNotFoundException
	{
		String tcsproxy = configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.TCS_PROXY);
		model.addAttribute("searchTerm", searchTerm);
		int tcsport = Integer.parseInt(configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.TCS_PORT));		
		String GmapAPI="";
		if (null != configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.GOOGLE_MAPS_API)){
			GmapAPI = configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.GOOGLE_MAPS_API);
		}
		BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();
		searchAddressForm.setGoogleMapsApiURL(GmapAPI);
		searchAddressForm.setTcsProxy(tcsproxy);
		searchAddressForm.setTcsPort(tcsport);
		searchAddressForm.setLimitLocationsCount(5);
		if(searchAddressForm.getSearchTerm() != null && searchAddressForm.getSearchTerm() != ""){
			searchAddressForm.setSearchTerm(searchAddressForm.getSearchTerm().trim());
		}
		if(null==searchAddressForm.getSearchTerm() || "".equalsIgnoreCase(searchAddressForm.getSearchTerm())){
			searchAddressForm.setSearchTerm("London");
		}
		model.addAttribute("searchTerm", searchTerm);
		List<JsonData> locationList = locationServiceFacades.getLocationsForSearchTerm(searchAddressForm, currentBaseStore);
		
		List<JDWLocation> parcelShopList = locationServiceFacades.getLocationsForSearchTerm(searchAddressForm, currentBaseStore);
		
		
		model.addAttribute("searchAddresses", locationList);
		GPS gps = new DefaultGPS(Double.parseDouble(((JsonData)locationList.get(0)).getGeometry_location_lat()),Double.parseDouble(((JsonData)locationList.get(0)).getGeometry_location_lng()));
		de.hybris.platform.storelocator.map.Map locationMap = defaultLocationMapService.getMapOfLocations(gps, searchAddressForm.getLimitLocationsCount(), currentBaseStore);
		List<Location> list = locationMap.getPointsOfInterest();
		List<JDWLocation> simplybeStoreList = new ArrayList<JDWLocation>();
		DecimalFormat df = new DecimalFormat("#.000");
		for (int i = 0; i < list.size(); i++) {			
			Location loc = list.get(i);
			
			PointOfServiceModel posModel = new PointOfServiceModel();
			posModel.setLatitude(loc.getGPS().getDecimalLatitude());
			posModel.setLongitude(loc.getGPS().getDecimalLongitude());
			double distance = jdwDeliveryAddressUtil.calculateDistance(gps, posModel);			
			JDWLocation jloc = new JDWLocation();
			jloc.setName(loc.getName());
			jloc.setDescription(loc.getDescription());
			jloc.setAddressData(loc.getAddressData());			
			jloc.setDistance(df.format(distance));
			jloc.setType("simplybe");
			simplybeStoreList.add(jloc);
			}
		
		for (int j = 0; j < parcelShopList.size(); j++) {	
			
			JDWLocation jdwl = parcelShopList.get(j);
			de.hybris.platform.storelocator.data.AddressData addata = new AddressData();
			PointOfServiceModel posModel = new PointOfServiceModel();
			posModel.setLatitude(Double.parseDouble(jdwl.getGeometry_location_lat()));
			posModel.setLongitude(Double.parseDouble(jdwl.getGeometry_location_lng()));
			double distance = jdwDeliveryAddressUtil.calculateDistance(gps, posModel);
			jdwl.setName(jdwl.getCity()+" "+jdwl.getDistrict() +"  "+jdwl.getParcelShopNumber() +"  "+jdwl.getTelephone());
			jloc.setDescription(jdwl.getDescription());	
			jloc.setAddressData(addata);	
			jdwl.setDistance(df.format(distance));
			jdwl.setType("parcelshop");
			simplybeStoreList.add(jdwl);
		}
		
		
		
		
		
		model.addAttribute("simplybeStores", simplybeStoreList);
		model.addAttribute(JDWConstants.METAROBOTS, JDWConstants.METAROBOTS_VALUE);
		model.addAttribute(JDWConstants.TITLE_DATA, userFacade.getTitles());
		model.addAttribute(JDWConstants.SEARCH_ADDRESS_FORM, searchAddressForm);
		model.addAttribute(JDWConstants.ADDRESS_BOOK_EMPTY, Boolean.valueOf(userFacade.isAddressBookEmpty()));
		model.addAttribute(JDWConstants.IS_DEFAULT_ADDRESS, Boolean.FALSE);
		addAddAddressPageDetailsToModel(model);
		final String resultView = JdwcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.customerSearchAddressResult;
		return resultView;
	} 
	
	
	/**
	 * This method is used to handle the post request for my-account search-address page.It takes addressForm,bindingResult,
	 * view model ,redirectModel and request as inputs and returns view for the address book page.
	 */
	@RequestMapping(value = JdwcheckoutaddonConstants.REQUEST_MAPPING_SEARCH_ADDRESS, method = RequestMethod.POST)
	@RequireHardLogIn
	public String searchAddress(@ModelAttribute("addressForm") final JdwAddressForm addressForm, final BindingResult bindingResult,
			final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(JDWConstants.METAROBOTS, JDWConstants.METAROBOTS_VALUE);
		String resultView = JDWConstants.BLANK_SPACE;
		
		setGoogleAPIProperties();
		getAddressValidator().validate(addressForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			resultView = JdwcheckoutaddonControllerConstants.Views.Pages.MultiStepCheckout.customerDeliveryAddress;

			addAddAddressPageDetailsToModel(model);

			//If the toggle was open, the toggle needs to remain opened after redirection
			if (!addressForm.getVerifiedAddress().booleanValue())
			{
				model.addAttribute(JDWConstants.EDIT_ADDRESS_FLAG, Boolean.TRUE);
			}


		}
		else
		{
			AddressData newAddress = new AddressData();
			newAddress = jdwDeliveryAddressUtil.getAddressDataFromAddressForm(newAddress, addressForm);

			if (userFacade.isAddressBookEmpty())
			{
				newAddress.setDefaultAddress(true);
				newAddress.setVisibleInAddressBook(true);
			}
			else
			{
				newAddress.setDefaultAddress(addressForm.getDefaultAddress() != null
						&& addressForm.getDefaultAddress().booleanValue());
			}


			newAddress.setBillingAddress(addressForm.getBillingAddress() != null && addressForm.getBillingAddress().booleanValue());

			jdwUserFacade.addAddress(newAddress);

			getCheckoutFacade().setDeliveryAddress(newAddress);

			resultView = REDIRECT_PREFIX + JdwcheckoutaddonControllerConstants.CHECKOUT_FROM_DELIVERY_ADDRESS;


		}
		return resultView;
	}
	
	
	/**
	 * This method is used for setting the google map api related properties.
	 */
	private void setGoogleAPIProperties()
	{		
		String tcsproxy = configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.TCS_PROXY);		
		int tcsport = Integer.parseInt(configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.TCS_PORT));		
		String GmapAPI="";
		if (null != configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.GOOGLE_MAPS_API)){
			GmapAPI = configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.GOOGLE_MAPS_API);
		}
				
		JDWDeliveryAddressUtil.GOOGLE_MAPS_API = GmapAPI;
		JDWDeliveryAddressUtil.TCS_PROXY = tcsproxy;
		JDWDeliveryAddressUtil.TCS_PORT = tcsport;
		
	}
	
	/**
	 * This method is used for entering the create and update urls in the model.
	 */
	private void addPaymentUrls(final Model model)
	{
		model.addAttribute(JdwcheckoutaddonConstants.NEW_CARD_URL, configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.NEW_CARD_URL_KEY));
		model.addAttribute(JdwcheckoutaddonConstants.UPDATE_CARD_URL, configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.UPDATE_CARD_URL_KEY));
	}

	/**
	 * This Method is used to update the cart model against the given phone Number
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET, value = JdwcheckoutaddonControllerConstants.CHECK_MULTI_UPDATED_PHONENUMBERS)
	@RequireHardLogIn
	public @ResponseBody
	String updatePhoneNumber(@RequestParam("updatedPhoneNumber") final String updatedPhoneNumber)
	{

		return jdwDeliveryDetailsFacade.updatePhoneNumberInCart(updatedPhoneNumber);
	}


	/**
	 * This method gets called when the "Use These Payment Details" button is clicked. It sets the selected payment
	 * method on the checkout facade and reloads the page highlighting the selected payment method.
	 * 
	 */
	@RequestMapping(value = JdwcheckoutaddonControllerConstants.CHOOSE_PAYMENT, method = RequestMethod.GET)
	@RequireHardLogIn
	public @ResponseBody
	String doSelectPaymentMethod(@RequestParam("selectedPaymentMethodId") final String selectedPaymentMethodId)
	{
		String result = JdwcheckoutaddonConstants.ERROR;
		if (StringUtils.isNotBlank(selectedPaymentMethodId))
		{
			getCheckoutFacade().setPaymentDetails(selectedPaymentMethodId);
			result = JdwcheckoutaddonConstants.SUCCESS;
		}
		return result;

	}

	/**
	 * This method creates the address data from the request for payment using existing card.
	 */
	@RequestMapping(value =
	{ JdwcheckoutaddonConstants.PAY_WITH_EXISTING_CARD_URL }, method = RequestMethod.POST)
	@RequireHardLogIn
	public String payUsingExistingCard(final HttpServletRequest request, final Model model, final RedirectAttributes redirectAttributes)
	{
		String resultView = REDIRECT_URL_ADD_DELIVERY_ADDRESS;
		final String decision = getValue(request, JdwcheckoutaddonConstants.DECISION2);
		final String reason_code = getValue(request, JdwcheckoutaddonConstants.REASON_CODE2);
		model.addAttribute(JdwcheckoutaddonConstants.REASON_CODE2, reason_code);
		redirectAttributes.addAttribute(JdwcheckoutaddonConstants.REASON_CODE2, reason_code);
		
		if (!JdwcheckoutaddonConstants.ACCEPT.equals(decision))
		{
			model.addAttribute(JdwcheckoutaddonConstants.PAYMENT_INFO_DATA, getUserFacade().getCCPaymentInfos(true));
			redirectAttributes.addFlashAttribute(JdwcheckoutaddonConstants.PAYMENT_MSG, JdwcheckoutaddonConstants.REASON_CODE_KEY + reason_code);
		}
		else
		{
			PaymentTransactionData  PaymentTransactionData =jdwPaymentService.createPaymentTransactionData(request, getCart());
			final String token = getValue(request, JdwcheckoutaddonConstants.REQ_PAYMENT_TOKEN);
			final CartData cartData = getCheckoutFacade().getCheckoutCart();
			String expiryDate;

			final CCPaymentInfoData paymentInfoData = new CCPaymentInfoData();

			paymentInfoData.setCardType(getHybrisCardType(getValue(request, JdwcheckoutaddonConstants.REQ_CARD_TYPE)));
			paymentInfoData.setAccountHolderName(getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_FORENAME) + JdwcheckoutaddonConstants.WHITE_SPACE
					+ getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_SURNAME));
			paymentInfoData.setCardNumber(getValue(request, JdwcheckoutaddonConstants.REQ_CARD_NUMBER));
			expiryDate = getValue(request, JdwcheckoutaddonConstants.REQ_CARD_EXPIRY_DATE);
			if (expiryDate.contains(JdwcheckoutaddonConstants.HIPHEN))
			{
				final String[] expiryDateArr = getValue(request, JdwcheckoutaddonConstants.REQ_CARD_EXPIRY_DATE).split(JdwcheckoutaddonConstants.HIPHEN);
				paymentInfoData.setExpiryMonth(expiryDateArr[0]);
				paymentInfoData.setExpiryYear(expiryDateArr[1]);
			}
			
			paymentInfoData.setSubscriptionId(token);
			paymentInfoData.setId(token);	
			
			final AddressData addressData = createAddressDataForExistingCard(request);
			paymentInfoData.setBillingAddress(addressData);

			sessionService.setAttribute(JdwcheckoutaddonConstants.PAYMENT_INFO_DATA, paymentInfoData);
			try
			{
				savePaymentDetails(cartData, Boolean.FALSE);
				resultView = proceedToPlaceOrder(redirectAttributes, PaymentTransactionData);
			}
			catch (final Exception e)
			{
				LOG.error(e);
				model.addAttribute(JdwcheckoutaddonConstants.PAYMENT_MSG, JdwcheckoutaddonConstants.SAVE_PAYMENT_DETAILS_FAILED);
			}
		}
		
		return resultView;
	}

	/**
	 * This method is used to handle the request coming from cybersource after token creation
	 */
	@RequestMapping(value =
	{ JdwcheckoutaddonConstants.CALLBACK_URL }, method = RequestMethod.POST)
	@RequireHardLogIn
	public String payWithNewCard(final HttpServletRequest request, final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException, InvalidCartException, CommerceCartModificationException
	{
		String resultView = REDIRECT_URL_ADD_DELIVERY_ADDRESS;
		final String decision = getValue(request, JdwcheckoutaddonConstants.DECISION2);
		final String req_card_type = getValue(request, JdwcheckoutaddonConstants.REQ_CARD_TYPE2);
		final String req_card_number = getValue(request, JdwcheckoutaddonConstants.REQ_CARD_NUMBER2);
		final String req_card_expiry_date = getValue(request, JdwcheckoutaddonConstants.REQ_CARD_EXPIRY_DATE2);
		final String reason_code = getValue(request, JdwcheckoutaddonConstants.REASON_CODE2);
		model.addAttribute(JdwcheckoutaddonConstants.REASON_CODE2, reason_code);
		redirectAttributes.addAttribute(JdwcheckoutaddonConstants.REASON_CODE2, reason_code);

		final boolean req_card_expiry_date_found = (!JdwcheckoutaddonConstants.HIPHEN.equals(req_card_expiry_date)) ? true : false;
		final boolean req_card_type_found = StringUtils.isBlank(req_card_type) ? false : true;
		final boolean req_card_number_found = StringUtils.isBlank(req_card_number) ? false : true;
		final boolean formComplete = req_card_expiry_date_found && req_card_type_found && req_card_number_found;

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		fillHiddenFields(model, cartData, JdwcheckoutaddonConstants.CREATE_PAYMENT_TOKEN);
		model.addAttribute(JdwcheckoutaddonControllerConstants.CARTDATA, cartData);
		model.addAttribute(JdwcheckoutaddonConstants.SOP_CARD_TYPES, getSOPCardTypes());
		
		redirectAttributes.addFlashAttribute(JdwcheckoutaddonConstants.PAYMENT_MSG, JdwcheckoutaddonConstants.REASON_CODE_KEY + reason_code);
		
		if (!formComplete || !JdwcheckoutaddonConstants.ACCEPT.equals(decision))
		{
			model.addAttribute(JdwcheckoutaddonConstants.PAYMENT_INFO_DATA, getUserFacade().getCCPaymentInfos(true));
			redirectAttributes.addFlashAttribute(JdwcheckoutaddonConstants.PAYMENT_MSG, JdwcheckoutaddonConstants.REASON_CODE_KEY + reason_code);
		}
		else
		{
			PaymentTransactionData  PaymentTransactionData =jdwPaymentService.createPaymentTransactionData(request, getCart());
			final AddressData billingAddress = jdwPaymentDetailsFacade.getBillingAddressForCurrentCustomer();
			final CCPaymentInfoData paymentInfoData = jdwPaymentService.createPaymentInfoData(request, cartData, billingAddress);
			sessionService.setAttribute(JdwcheckoutaddonConstants.PAYMENT_INFO_DATA, paymentInfoData);
			try
			{
				final Boolean saveToAccFlag = (Boolean) sessionService.getAttribute(JdwcheckoutaddonConstants.SAVE_TO_ACC_FLAG);
				savePaymentDetails(cartData, saveToAccFlag);
			}
			catch (final Exception e)
			{
				LOG.error(e);
				model.addAttribute(JdwcheckoutaddonConstants.PAYMENT_MSG, JdwcheckoutaddonConstants.SAVE_PAYMENT_DETAILS_FAILED);
			}
			
			final String devID;
			if(JdwcheckoutaddonConstants.BLANK_SPACE.equalsIgnoreCase(getValue(request, JdwcheckoutaddonConstants.DEVICE_FINGERPRINT_ID)))
			{
				devID = getValue(request, JdwcheckoutaddonConstants.DEVICE_FINGERPRINT_ID);
			}
			else
			{
				devID = getValue(request, JdwcheckoutaddonConstants.REQ_DEVICE_FINGERPRINT_ID);
			}
			if (StringUtils.isNotBlank(devID))
			{
				sessionService.setAttribute(JdwcheckoutaddonConstants.REQ_DEVICE_FINGERPRINT_ID, devID);
			}

			model.addAttribute(JdwcheckoutaddonControllerConstants.CARTDATA, cartData);
			model.addAttribute(JdwcheckoutaddonConstants.PAYMENT_ID, paymentInfoData.getId());

			resultView = proceedToPlaceOrder(redirectAttributes, PaymentTransactionData);
		}

		return resultView;
	}
	
	/**
	 * This method handles the scenario for paying with existing card.
	 */
	private AddressData createAddressDataForExistingCard(final HttpServletRequest request)
	{
		final AddressData addressData = new AddressData();
		addressData.setFirstName(getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_FORENAME));
		addressData.setLastName(getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_SURNAME));
		addressData.setLine1(getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_ADDRESS_LINE1));
		addressData.setLine2(getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_ADDRESS_LINE2));
		addressData.setTown(getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_ADDRESS_CITY));
		addressData.setPostalCode(getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_ADDRESS_POSTALCODE));
		addressData.setEmail(getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_EMAIL));
		addressData.setPhone(getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_PHONE));
		addressData.setCountry(getI18NFacade().getCountryForIsocode(getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_ADDRESS_COUNTRY)));

		if (StringUtils.isNotBlank(getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_ADDRESS_STATE)))
		{
			addressData.setRegion(getI18NFacade().getRegion(getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_ADDRESS_COUNTRY),
					getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_ADDRESS_COUNTRY) + JdwcheckoutaddonConstants.HIPHEN + getValue(request, JdwcheckoutaddonConstants.REQ_BILL_TO_ADDRESS_STATE)));
		}
		addressData.setShippingAddress(false);
		addressData.setBillingAddress(true);

		getAddressVerificationFacade().verifyAddressData(addressData);
		return addressData;
	}

	/**
	 * This method gets called when the address is clicked. It sets the selected delivery address on the checkout facade
	 * - if it has changed, and reloads the page highlighting the selected delivery address.
	 * 
	 */

	@RequestMapping(value = JdwcheckoutaddonControllerConstants.SELECT_ADDRESS, method = RequestMethod.GET)
	@RequireHardLogIn
	@ResponseBody
	public String doSelectDeliveryAddress(@RequestParam("selectedAddressCode") final String selectedAddressCode,
			final RedirectAttributes redirectAttributes)
	{

		String firstName;
		String lastName;
		String lineAddress1;
		String lineAddress2;
		String city;
		String postalCode;
		String country;
		String title;
		final String region = JdwcheckoutaddonControllerConstants.BLANK_SPACE;

		String response = JdwcheckoutaddonConstants.RESPONSE_NO;
		if (StringUtils.isNotBlank(selectedAddressCode))
		{
			final AddressData selectedAddressData = getCheckoutFacade().getDeliveryAddressForCode(selectedAddressCode);
			final boolean hasSelectedAddressData = selectedAddressData != null;
			if (hasSelectedAddressData)
			{
				final AddressData cartCheckoutDeliveryAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
				if (isAddressIdChanged(cartCheckoutDeliveryAddress, selectedAddressData))
				{
					jdwDeliveryDetailsFacade.updateAddressInCart(selectedAddressData);
					getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
					if (cartCheckoutDeliveryAddress != null && !cartCheckoutDeliveryAddress.isVisibleInAddressBook())
					{
						getUserFacade().removeAddress(cartCheckoutDeliveryAddress);
					}
				}
			}
			final List<TitleData> titles = userFacade.getTitles();

			title = findTitleForCode(titles, selectedAddressData.getTitleCode()).getName();
			firstName = selectedAddressData.getFirstName();
			lastName = (selectedAddressData.getLastName() == null
					|| selectedAddressData.getLastName().equals(JdwcheckoutaddonControllerConstants.BLANK_SPACE) ? DEFAULT_LAST_NAME
					: selectedAddressData.getLastName());
			lineAddress1 = selectedAddressData.getLine1();
			lineAddress2 = selectedAddressData.getLine2();
			city = selectedAddressData.getTown();
			postalCode = selectedAddressData.getPostalCode();
			country = selectedAddressData.getCountry().getName();

			response = title + JdwcheckoutaddonConstants.HASH + firstName + JdwcheckoutaddonConstants.HASH + lastName + JdwcheckoutaddonConstants.HASH + lineAddress1 + JdwcheckoutaddonConstants.HASH + lineAddress2 + JdwcheckoutaddonConstants.HASH + city + JdwcheckoutaddonConstants.HASH
					+ region + JdwcheckoutaddonConstants.HASH + postalCode + JdwcheckoutaddonConstants.HASH + country;
		}
		return response;
	}

	/**
	 * This Method is used to update the cart model against the given phone Number
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET, value = JdwcheckoutaddonControllerConstants.SAVE_TO_ACCOUNT)
	@RequireHardLogIn
	public @ResponseBody
	String maintainSaveToAccFlagInSession(@RequestParam("saveToAccount") final boolean saveToAccount)
	{
		sessionService.setAttribute(JdwcheckoutaddonConstants.SAVE_TO_ACC_FLAG, Boolean.valueOf(saveToAccount));
		return JdwcheckoutaddonConstants.SUCCESS;
	}

	private String proceedToPlaceOrder(final RedirectAttributes redirectAttributes,final PaymentTransactionData paymentTransactionData)
	{
		String result = REDIRECT_URL_ADD_DELIVERY_ADDRESS;
		OrderData orderData = null;
		try
		{
			String paymentInfoPk = null;
			if (null != sessionService.getAttribute(JdwcheckoutaddonConstants.SAVED_PAYMENT_INFO))
			{
				paymentInfoPk = (String) sessionService.getAttribute(JdwcheckoutaddonConstants.SAVED_PAYMENT_INFO);

			}
			orderData = jdwAcceleratorCheckoutFacade.placeOrder(paymentInfoPk, paymentTransactionData);
			result = redirectToOrderConfirmationPage(orderData);
		}
		catch (final Exception e)
		{
			LOG.error(e);
			redirectAttributes.addFlashAttribute(JdwcheckoutaddonConstants.PAYMENT_MSG, JdwcheckoutaddonConstants.PLACE_ORDER_FAILED_MSG);
		}
		return result;
	}

	/**
	 * This method is used to redirect to the order confirmation page after successful order creation
	 */
	@Override
	protected String redirectToOrderConfirmationPage(final OrderData orderData)
	{
		return REDIRECT_URL_ORDER_CONFIRMATION
				+ (getCheckoutCustomerStrategy().isAnonymousCheckout() ? orderData.getGuid() : orderData.getCode());
	}

	protected TitleData findTitleForCode(final List<TitleData> titles, final String code)
	{
		if (code != null && !code.isEmpty() && titles != null && !titles.isEmpty())
		{
			for (final TitleData title : titles)
			{
				if (code.equals(title.getCode()))
				{
					return title;
				}
			}
		}
		return null;
	}

	/**
	 * This method is method is used to prepare the data for checkout page
	 */
	private void prepareDataForCheckoutPage(final String selectedAddressCode)
	{
		if (StringUtils.isNotBlank(selectedAddressCode))
		{
			final AddressData selectedAddressData = getCheckoutFacade().getDeliveryAddressForCode(selectedAddressCode);
			final boolean hasSelectedAddressData = selectedAddressData != null;
			if (hasSelectedAddressData)
			{
				final AddressData cartCheckoutDeliveryAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
				removeAddressIfAddressChanged(cartCheckoutDeliveryAddress, selectedAddressData);
			}
		}
	}

	/**
	 * This method firstly checks the selected address is same as the checkoutDeliveryAddress and if not same then remove
	 * the checkoutDeliveryAddress
	 */
	private void removeAddressIfAddressChanged(final AddressData cartCheckoutDeliveryAddress, final AddressData selectedAddressData)
	{
		if (isAddressIdChanged(cartCheckoutDeliveryAddress, selectedAddressData))
		{
			getCheckoutFacade().setDeliveryAddress(selectedAddressData);
			if (cartCheckoutDeliveryAddress != null && !cartCheckoutDeliveryAddress.isVisibleInAddressBook())
			{
				// temporary address should be removed
				getUserFacade().removeAddress(cartCheckoutDeliveryAddress);
			}
		}
	}

	/**
	 * This method is used to prepare list of phone numbers from the address book
	 */
	private void prepareListOfPhoneNumbers(final Model model, final CartData cartData)
	{

		if (null != cartData && null != cartData.getPhoneNumber())
		{
			jdwDeliveryDetailsFacade.updatePhoneNumberInCart(cartData.getPhoneNumber());
		}
		model.addAttribute(JdwcheckoutaddonConstants.LIST_OF_PHONENUMBERS, jdwDeliveryDetailsFacade.getPhoneNumbersFromAddressBook());
	}

	/**
	 * This method is used to populate payment details for checkout
	 */
	private void populatePaymentDetails(final Model model, final CartData cartData)
	{
		final Map<String, Object> paymentInfosMap = new HashMap<String, Object>();
		final List<CCPaymentInfoData> paymentInfosList = new ArrayList<CCPaymentInfoData>(getUserFacade().getCCPaymentInfos(true));
		int count = 0;

		model.addAttribute(JdwcheckoutaddonConstants.SOP_CARD_TYPES, getSOPCardTypes());
		model.addAttribute(JdwcheckoutaddonConstants.HAS_NO_PAYMENT_INFO, Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));


		for (final CCPaymentInfoData paymentInfo : paymentInfosList)
		{
			paymentInfosMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, paymentInfo);
			count++;
		}

		model.addAttribute(JdwcheckoutaddonConstants.PAYMENT_INFOS, paymentInfosMap);


		final PaymentDetailsForm paymentDetailsForm = new PaymentDetailsForm();
		final AddressForm addressForm = new AddressForm();
		paymentDetailsForm.setBillingAddress(addressForm);
		model.addAttribute(paymentDetailsForm);
		allowPaymentTokenUpdate(model, cartData);
		fillHiddenFields(model, cartData, JdwcheckoutaddonConstants.CREATE_PAYMENT_TOKEN);
	}

	/**
	 * This method is used to fill the hidden fields for token creation for cybersource
	 */
	private void fillHiddenFields(final Model model, final CartData cartData, final String transactionType)
	{

		final Map<String, Object> params = populateBillingDetails(cartData);
		String signed_fields = SIGNED_FIELD_NAMES_VALUE;

		if (StringUtils.isNotBlank((String) params.get(JdwcheckoutaddonConstants.BILL_TO_ADDRESS_STATE)))
		{
			signed_fields += JdwcheckoutaddonControllerConstants.COMMA + JdwcheckoutaddonConstants.BILL_TO_ADDRESS_STATE;
		}
		else
		{
			params.remove(JdwcheckoutaddonConstants.BILL_TO_ADDRESS_STATE);
		}
		getI18nService().getCurrentLocale();
		final String callbackUrl = getContextPath() + configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.CYBERSOURCE_CALLBACK_URL);
		params.put(JdwcheckoutaddonConstants.CUSTOM_RECEIPT_PAGE, callbackUrl);
		signed_fields += JdwcheckoutaddonControllerConstants.COMMA + JdwcheckoutaddonConstants.DEVICE_FINGERPRINT_ID;
		params.put(JdwcheckoutaddonConstants.DEVICE_FINGERPRINT_ID, cartData.getCode());

		params.put(JdwcheckoutaddonConstants.SIGNED_FIELD_NAMES, signed_fields);
		if (IS_CVV_ENABLED)
		{
			params.put(JdwcheckoutaddonConstants.UNSIGNED_FIELD_NAMES, JdwcheckoutaddonConstants.UNSIGNED_FIELD_NAMES_VALUE + JdwcheckoutaddonControllerConstants.COMMA + JdwcheckoutaddonConstants.CVV_FIELD_NAME);
		}
		else
		{
			params.put(JdwcheckoutaddonConstants.UNSIGNED_FIELD_NAMES, JdwcheckoutaddonConstants.UNSIGNED_FIELD_NAMES_VALUE);
		}
		model.addAttribute(JdwcheckoutaddonConstants.IS_CVV_ENBL, Boolean.valueOf(IS_CVV_ENABLED));

		params.put(JdwcheckoutaddonConstants.ACCESS_KEY2, jdwPaymentService.getAccessKey());
		params.put(JdwcheckoutaddonConstants.PROFILE_ID, jdwPaymentService.getProfileId());
		params.put(JdwcheckoutaddonConstants.TRANSACTION_UUID, UUID.randomUUID().toString());
		params.put(JdwcheckoutaddonConstants.TRANSACTION_TYPE, transactionType);
		params.put(JdwcheckoutaddonConstants.SIGNED_DATE_TIME, CSUtil.getUTCDateTime());

		params.put(JdwcheckoutaddonConstants.LOCALE, getI18nService().getCurrentLocale());
		params.put(JdwcheckoutaddonConstants.REFERENCE_NUMBER, cartData.getCode());
		
		if(null != cartData.getTotalPriceWithTax()){
		params.put(JdwcheckoutaddonConstants.AMOUNT, String.valueOf(cartData.getTotalPriceWithTax().getValue()));
		params.put(JdwcheckoutaddonConstants.CURRENCY, cartData.getTotalPriceWithTax().getCurrencyIso());
		}
		params.put(JdwcheckoutaddonConstants.PAYMENT_METHOD2, JdwcheckoutaddonConstants.CARD);

		params.put(JdwcheckoutaddonConstants.IGNORE_AVS_RESULT_KEY, IGNORE_AVS_RESULT);
		params.put(JdwcheckoutaddonConstants.IGNORE_CVN_RESULT_KEY, IGNORE_CVS_RESULT);

		model.addAttribute(JdwcheckoutaddonControllerConstants.PARAMS, params);

		final String signature = SignatureUtil.sign(params, jdwPaymentService.getSecretKey());
		model.addAttribute(JdwcheckoutaddonConstants.SIGNATURE2, signature);
	}
	
	private void addAddAddressPageDetailsToModel(final Model model)
	{
		model.addAttribute(
				JDWConstants.ADDRESS_PAGE_TITLE,
				getMessageSource().getMessage(JDWConstants.TEXT_ACCOUNT_ADDRESSBOOK_ADD_DELIVERY_ADDRESS, null,
						getI18nService().getCurrentLocale()));


		model.addAttribute(JDWConstants.IS_BILLING_ADDR_PRESENT, Boolean.valueOf(jdwUserFacade.isBillingAddressPresent()));

	}
	
	protected JdwAddressForm getPopulatedAddressForm()
	{
		final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
		final JdwAddressForm addressForm = new JdwAddressForm();
		addressForm.setFirstName(currentCustomerData.getFirstName());
		addressForm.setLastName(currentCustomerData.getLastName());
		addressForm.setTitleCode(currentCustomerData.getTitleCode());
		return addressForm;
	}
	
	
/*	protected JdwAddressForm getPopulatedSearchAddressForm()
	{
		final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
		final JdwAddressForm addressForm = new JdwAddressForm();
		addressForm.setFirstName(currentCustomerData.getFirstName());
		addressForm.setLastName(currentCustomerData.getLastName());
		addressForm.setTitleCode(currentCustomerData.getTitleCode());
		return addressForm;
	}
	*/
	
	
	protected JdwSearchAddressForm getPopulatedSearchAddressForm()
	{
		final CustomerData currentCustomerData = customerFacade.getCurrentCustomer();
		final JdwSearchAddressForm searchAddressForm = new JdwSearchAddressForm();
		searchAddressForm.setFirstName(currentCustomerData.getFirstName());
//		addressForm.setLastName(currentCustomerData.getLastName());
//		addressForm.setTitleCode(currentCustomerData.getTitleCode());
		return searchAddressForm;
	}

	public String getContextPath()
	{
		configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.CONTEXTPATH_INITIAL);
		return configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.CONTEXTPATH_INITIAL) + getCurrentSiteId()
				+ JdwcheckoutaddonControllerConstants.FRONT_SLASH + getI18nService().getCurrentLocale();
	}

	public String getCurrentSiteId()
	{
		return baseSiteService.getCurrentBaseSite().getUid();
	}

	/**
	 * This method is used to populate billing details for checkout
	 */
	private Map<String, Object> populateBillingDetails(final CartData cartData)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final AddressData billingAddress = jdwPaymentDetailsFacade.getBillingAddressForCurrentCustomer();

		final CustomerData customerData = customerFacade.getCurrentCustomer();
		params.put(JdwcheckoutaddonConstants.BILL_TO_EMAIL, customerData.getUid());
		if (null != billingAddress)
		{
			params.put(JdwcheckoutaddonConstants.BILL_TO_FORENAME, billingAddress.getFirstName());
			params.put(JdwcheckoutaddonConstants.BILL_TO_SURNAME, billingAddress.getLastName());
			params.put(JdwcheckoutaddonConstants.BILL_TO_ADDRESS_LINE1, billingAddress.getLine1());

			params.put(JdwcheckoutaddonConstants.BILL_TO_ADDRESS_CITY, billingAddress.getTown());
			params.put(JdwcheckoutaddonConstants.BILL_TO_ADDRESS_POSTALCODE, billingAddress.getPostalCode());
			params.put(JdwcheckoutaddonConstants.BILL_TO_ADDRESS_COUNTRY, billingAddress.getCountry() != null ? billingAddress.getCountry().getIsocode()
					: null);
		}
		return params;
	}


	private List<CardTypeData> getSOPCardTypes()
	{
		return jdwCheckoutAddonUtil.fetchCardTypesForCurrentSite();
	}

	private void savePaymentDetails(final CartData cartData, final Boolean saveToAccFlag)
	{
		final CCPaymentInfoData paymentInfoData = (CCPaymentInfoData) sessionService.getAttribute(JdwcheckoutaddonConstants.PAYMENT_INFO_DATA);
		final String paymentInfoPk = jdwPaymentDetailsFacade.saveToken(paymentInfoData, saveToAccFlag);
		sessionService.removeAttribute(JdwcheckoutaddonConstants.SAVE_TO_ACC_FLAG);
		sessionService.setAttribute(JdwcheckoutaddonConstants.SAVED_PAYMENT_INFO, paymentInfoPk);
		getCheckoutFacade().setPaymentDetails(paymentInfoData.getId());
		cartData.setPaymentInfo(paymentInfoData);
	}


	private String getValue(final HttpServletRequest request, final String key)
	{
		final Map<String, String[]> paramMap = request.getParameterMap();
		String value = JdwcheckoutaddonConstants.BLANK_SPACE;
		final String[] array = paramMap.get(key);
		if (array != null && array.length > 0)
		{
			value = array[0];
		}
		if (array == null || value == null)
		{
			value = JdwcheckoutaddonConstants.BLANK_SPACE;
		}
		return value;
	}

	private String getHybrisCardType(final String cybersourceCardType)
	{
		return configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.CARD_TYPE_PREFIX + cybersourceCardType);
	}

	protected void allowPaymentTokenUpdate(final Model model, final CartData cartdata)
	{

		String month = null;
		String year = null;
		int count = 0;
		final List<CCPaymentInfoData> ccPaymentInfos = getUserFacade().getCCPaymentInfos(true);
		Map<String, Object> params;
		final Map<String, Map<String, Object>> paramsSet = new HashMap<String, Map<String, Object>>();
		final UserModel currentUser = userService.getCurrentUser();
		CustomerModel currentCustomer = null;
		PaymentInfoModel defaultPaymentInfo = null;
		String defaultSubscriptionPK = null;
		Boolean defaultCardPresent = Boolean.FALSE;

		final Map<String, Object> cardTypeMap = new HashMap<String, Object>();
		final Map<String, Object> cardNumberMap = new HashMap<String, Object>();
		final Map<String, Object> signatureMap = new HashMap<String, Object>();
		final Map<String, String> expiryMonthStatusMap = new HashMap<String, String>();
		final Map<String, String> expiryYearStatusMap = new HashMap<String, String>();
		final Map<String, Boolean> expiredStatusMap = new HashMap<String, Boolean>();
		final Map<String, Boolean> soonExpireStatusMap = new HashMap<String, Boolean>();
		final Map<String, Boolean> defaultCardMap = new HashMap<String, Boolean>();

		if (null!=currentUser && currentUser instanceof CustomerModel)
		{
			currentCustomer = (CustomerModel) currentUser;
			defaultPaymentInfo = currentCustomer.getDefaultPaymentInfo();
			if(null!=defaultPaymentInfo)
			{
				defaultSubscriptionPK = defaultPaymentInfo.getPk() == null ? null : defaultPaymentInfo.getPk().toString();
			}
		}

		for (final CCPaymentInfoData ccPaymentInfoData : ccPaymentInfos)
		{
			params = new HashMap<String, Object>();
			params = populateBillingDetails(cartdata);
			month = ccPaymentInfoData.getExpiryMonth();
			year = ccPaymentInfoData.getExpiryYear();
			if (Integer.parseInt(year) < (Calendar.getInstance().get(Calendar.YEAR))
					|| (Integer.parseInt(year) == Calendar.getInstance().get(Calendar.YEAR) && ((Integer.parseInt(month) - (Calendar
							.getInstance().get(Calendar.MONTH) + 1)) >= -1)))
			{
				ccPaymentInfoData.setAllowPaymentTokenUpdate(true);
			}
			else
			{
				ccPaymentInfoData.setAllowPaymentTokenUpdate(false);
			}

			
			cardTypeMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, getCybersourceCardType(ccPaymentInfoData.getCardType()));
			cardNumberMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, ccPaymentInfoData.getCardNumber());
			updateFillHiddenFields(params, model, cartdata, count, signatureMap,
					ccPaymentInfoData.getSubscriptionId());

			paramsSet.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, params);

			expiryMonthStatusMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, month);
			expiryYearStatusMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, year);

			if (Integer.parseInt(year) < (Calendar.getInstance().get(Calendar.YEAR)))
			{
				expiredStatusMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, Boolean.TRUE);
				soonExpireStatusMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, Boolean.FALSE);
			}
			else if (Integer.parseInt(year) == (Calendar.getInstance().get(Calendar.YEAR)))
			{
				if (Integer.parseInt(month) - (Calendar.getInstance().get(Calendar.MONTH) + 1) < 0)
				{
					expiredStatusMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, Boolean.TRUE);
					soonExpireStatusMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, Boolean.FALSE);
				}
				else if (Integer.parseInt(month) - (Calendar.getInstance().get(Calendar.MONTH) + 1) == 1
						|| Integer.parseInt(month) - (Calendar.getInstance().get(Calendar.MONTH) + 1) == 0)
				{
					expiredStatusMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, Boolean.FALSE);
					soonExpireStatusMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, Boolean.TRUE);
				}
			}
			else
			{
				expiredStatusMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, Boolean.FALSE);
				soonExpireStatusMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, Boolean.FALSE);
			}

			if (ccPaymentInfoData.getId().equals(defaultSubscriptionPK))
			{
				defaultCardMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, Boolean.TRUE);
				defaultCardPresent = Boolean.TRUE;
			}
			else
			{
				defaultCardMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, Boolean.FALSE);
			}

			count++;
		}

		model.addAttribute(JdwcheckoutaddonConstants.PARAMS_SET, paramsSet);
		model.addAttribute(JdwcheckoutaddonConstants.CARD_TYPE_MAP, cardTypeMap);
		model.addAttribute(JdwcheckoutaddonConstants.CARD_NUMBER_MAP, cardNumberMap);
		model.addAttribute(JdwcheckoutaddonConstants.SIGNATURE_MAP, signatureMap);
		model.addAttribute(JdwcheckoutaddonConstants.EXPIRED_STATUS_MAP, expiredStatusMap);
		model.addAttribute(JdwcheckoutaddonConstants.SOON_EXPIRE_STATUS_MAP, soonExpireStatusMap);
		model.addAttribute(JdwcheckoutaddonConstants.EXP_MONTHS_STATUS_MAP, expiryMonthStatusMap);
		model.addAttribute(JdwcheckoutaddonConstants.EXP_YEARS_STATUS_MAP, expiryYearStatusMap);
		model.addAttribute(JdwcheckoutaddonConstants.DEFAUTL_CARD_MAP, defaultCardMap);
		model.addAttribute(JdwcheckoutaddonConstants.DEFAUTL_CARD_PRESENT, defaultCardPresent);

	}

	private void updateFillHiddenFields(final Map<String, Object> params, final Model model, final CartData cartData,
			final int count, final Map<String, Object> signatureMap, final String paymentToken)
	{
		String signed_fields = SIGNED_FIELD_NAMES_VALUE;
		String unsigned_fields = JdwcheckoutaddonConstants.UPDATE_UNSIGNED_FIELDS_VALUE;

		signed_fields += JdwcheckoutaddonControllerConstants.COMMA + JdwcheckoutaddonConstants.ALLOW_PAYMENT_TOKEN_UPDATE;
		signed_fields += JdwcheckoutaddonControllerConstants.COMMA + JdwcheckoutaddonConstants.PAYMENT_TOKEN;

		if (StringUtils.isNotBlank((String) params.get(JdwcheckoutaddonConstants.BILL_TO_ADDRESS_STATE)))
		{
			signed_fields += JdwcheckoutaddonControllerConstants.COMMA + JdwcheckoutaddonConstants.BILL_TO_ADDRESS_STATE;
		}
		else
		{
			params.remove(JdwcheckoutaddonConstants.BILL_TO_ADDRESS_STATE);
		}
		if (IS_CVV_ENABLED)
		{
			unsigned_fields = unsigned_fields + JdwcheckoutaddonControllerConstants.COMMA + JdwcheckoutaddonConstants.CVV_FIELD_NAME;
		}
		getI18nService().getCurrentLocale();
		final String callbackUrl = getContextPath() + configurationService.getConfiguration().getString(CALL_BACK_UPDATE_URL);
		params.put(JdwcheckoutaddonConstants.CUSTOM_RECEIPT_PAGE, callbackUrl);
		signed_fields += JdwcheckoutaddonControllerConstants.COMMA + JdwcheckoutaddonConstants.DEVICE_FINGERPRINT_ID;
		params.put(JdwcheckoutaddonConstants.DEVICE_FINGERPRINT_ID, cartData.getCode());
		params.put(JdwcheckoutaddonConstants.SIGNED_FIELD_NAMES, signed_fields);
		params.put(JdwcheckoutaddonConstants.UNSIGNED_FIELD_NAMES, unsigned_fields);
		model.addAttribute(JdwcheckoutaddonConstants.IS_CVV_ENBL, Boolean.valueOf(IS_CVV_ENABLED));
		params.put(JdwcheckoutaddonConstants.ACCESS_KEY2, jdwPaymentService.getAccessKey());
		params.put(JdwcheckoutaddonConstants.PROFILE_ID, jdwPaymentService.getProfileId());
		params.put(JdwcheckoutaddonConstants.TRANSACTION_UUID, UUID.randomUUID().toString());
		params.put(JdwcheckoutaddonConstants.SIGNED_DATE_TIME, CSUtil.getUTCDateTime());
		params.put(JdwcheckoutaddonConstants.LOCALE, getI18nService().getCurrentLocale());
		params.put(JdwcheckoutaddonConstants.REFERENCE_NUMBER, cartData.getCode());
		
		if(null != cartData.getTotalPriceWithTax())
		{
		params.put(JdwcheckoutaddonConstants.AMOUNT, String.valueOf(cartData.getTotalPriceWithTax().getValue()));
		params.put(JdwcheckoutaddonConstants.CURRENCY, cartData.getTotalPriceWithTax().getCurrencyIso());
		}
		params.put(JdwcheckoutaddonConstants.PAYMENT_METHOD2, JdwcheckoutaddonConstants.CARD);
		params.put(JdwcheckoutaddonConstants.IGNORE_AVS_RESULT_KEY, IGNORE_AVS_RESULT);
		params.put(JdwcheckoutaddonConstants.IGNORE_CVN_RESULT_KEY, IGNORE_CVS_RESULT);
		params.put(JdwcheckoutaddonConstants.ALLOW_PAYMENT_TOKEN_UPDATE, Boolean.TRUE);
		params.put(JdwcheckoutaddonConstants.PAYMENT_TOKEN, paymentToken);
		
		params.put(JdwcheckoutaddonConstants.TRANSACTION_TYPE, JdwcheckoutaddonConstants.AUTH_UPDATE_PAYMENT_TOKEN);
		final String signatureForUpdateAndAuth = SignatureUtil.sign(params, jdwPaymentService.getSecretKey());
		
		params.remove(JdwcheckoutaddonConstants.TRANSACTION_TYPE);
		params.put(JdwcheckoutaddonConstants.TRANSACTION_TYPE, JdwcheckoutaddonConstants.AUTH_NOT_UPDATE_TOKEN);
		final String signatureForOnlyAuth = SignatureUtil.sign(params, jdwPaymentService.getSecretKey());
		
		params.put(JdwcheckoutaddonConstants.TRANSACTION_TYPE_AUTH_UPDATE, JdwcheckoutaddonConstants.AUTH_UPDATE_PAYMENT_TOKEN);
		
		signatureMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count, signatureForOnlyAuth);
		signatureMap.put(JdwcheckoutaddonConstants.PARAMS + JdwcheckoutaddonConstants.UNDERSCORE + count + JdwcheckoutaddonConstants.UNDERSCORE + JdwcheckoutaddonConstants.SIG_FOR_AUTH_UPDATE, signatureForUpdateAndAuth);
	}

	/**
	 * This Method is used to get cybersource card type from hybris card type.
	 * 
	 */
	private String getCybersourceCardType(final String hybrisCardType)
	{
		return configurationService.getConfiguration().getString(JdwcheckoutaddonConstants.CARD_TYPE_PREFIX + hybrisCardType);
	}

	/**
	 * This Method is used to set the existing token in session.
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET, value = JdwcheckoutaddonConstants.SET_UPDATE_TOKEN_URL)
	@RequireHardLogIn
	public @ResponseBody
	String setUpdateToken(final HttpServletRequest request, @RequestParam("token") final String token)
	{

		sessionService.setAttribute(JdwcheckoutaddonConstants.UPDATE_CARD_TOKEN, token);
		return JdwcheckoutaddonConstants.SUCCESS;
	}
	
	protected CartModel getCart()
	{
		if (jdwAcceleratorCheckoutFacade.hasCheckoutCart())
		{
			return cartService.getSessionCart();
		}

		return null;
	}

	public JdwAddressValidator getAddressValidator()
	{
		return JdwAddressValidator;
	}

	public LocationServiceFacades getLocationServiceFacades() {
		return locationServiceFacades;
	}

	public void setLocationServiceFacades(
			LocationServiceFacades locationServiceFacades) {
		this.locationServiceFacades = locationServiceFacades;
	}

	public void setAddressValidator(final AddressValidator addressValidator)
	{
		this.addressValidator = addressValidator;
	}

	public AddressVerificationResultHandler getAddressVerificationResultHandler()
	{
		return addressVerificationResultHandler;
	}

	public void setAddressVerificationResultHandler(final AddressVerificationResultHandler addressVerificationResultHandler)
	{
		this.addressVerificationResultHandler = addressVerificationResultHandler;
	}
}