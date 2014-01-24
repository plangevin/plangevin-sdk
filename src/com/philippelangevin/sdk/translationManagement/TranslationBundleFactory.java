package com.philippelangevin.sdk.translationManagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.TreeMap;

public class TranslationBundleFactory {
	public static final String DEFAULT_COUNTRY = "";
	public static final String DEFAULT_LANGUAGE = "en";
	public static final String[] SUPPORTED_LANGUAGES = { "en", "fr" };
	
	/* The first message bundle is being defined by this class */
	public static final String DEFAULT_FIRST_BUNDLE_PACKAGE = "ctec";
	public static final String DEFAULT_FIRST_BUNDLE_BASENAME = "messages";
	
	/* The TreeMap contains all MessageBundle instances created */
	private static TreeMap <String, MessageBundle> resourceBundleMap = new TreeMap <String, MessageBundle>();
	private static Locale locale = null;
	
	/**
	 * This method will create/return the MessageBundle associated with the calling class.
	 * @param currentClass
	 * @return
	 */
	public static MessageBundle getTranslationBundle () {
		/*
		 * Thread.currentThread().getStackTrace()[0] = Thread
		 * Thread.currentThread().getStackTrace()[1] = TranslationBundleFactory
		 * Thread.currentThread().getStackTrace()[2] = Calling class
		 */
		String className = Thread.currentThread().getStackTrace()[2].getClassName();
		
		/*
		 * We only need the name of the package (excluding the class name).
		 */
		return getPackageTranslationBundle(className.substring(0, className.lastIndexOf('.')));
	}
	
	/*
	 * This method will create/return the MessageBundle associated with a specific class.
	 * You can use this method if you want someone else's MessageBundle.
	 * @param currentClass
	 * @return
	 */
	public static MessageBundle getTranslationBundle (Class<?> someClass) {
		return getPackageTranslationBundle(someClass.getPackage().getName());
	}
	

	/**
	 * This recursive method will create/return the MessageBundle associated with a package.
	 * It will go up in the parent packages if it doesn't find the resource it's looking for.
	 * @param  STRING
	 * @return MessageBundle
	 * @author Philippe Langevin
	 */
	private static MessageBundle getPackageTranslationBundle (String currentPackage) {
		/* We need to know which language to use before getting the message bundles */
		if (locale == null) {
			System.err.println("getTranslationBundle() should never be called before setLocale()... Now setting default language and country [setLocale(null, null)].");
			setLocale(null, null);
		}
		
		/* No bundle is being requested */
		if (currentPackage == null) {
			return null;
		}
		
		/* We return the message bundle if it already exists */
		MessageBundle currentBundle = resourceBundleMap.get(currentPackage);
		if (currentBundle != null) {
			return currentBundle;
		}
		
		/* We have to create a new message bundle if it doesn't exist */
		MessageBundle newBundle = new MessageBundle(currentPackage);
		
		/* Locating the parent through recursion, if we're at the root
		   package (ctec) the parent will be the first generic bundle instead
		*/
		MessageBundle bundleParent = (currentPackage.lastIndexOf('.') != -1?
				getPackageTranslationBundle(currentPackage.substring(0, currentPackage.lastIndexOf('.'))) :
				MessageBundle.getGenericBundle());
		
		/* If no resource file can be found for the new bundle, we try with its parent instead */
		if (newBundle.containsResource()) {
			newBundle.setParent(bundleParent);
			resourceBundleMap.put(currentPackage, newBundle);
			return newBundle;
		} else {
			return bundleParent;
		}
	}
	
	/**
	 * setLocale determines which is the best locale (country/language) to use for the application:
	 *   - Step 1: It tries to read it from the config.xml file
	 *   - Step 2: Upon failure (missing/unsupported) it tries to use the system's language
	 *   - Step 3: Upon failure (unsupported) it uses the defined default locale (DEFAULT_COUNTRY/DEFAULT_LANGUAGE)
	 */
	public static void setLocale (String configLanguage, String configCountry) {
		String country = null;
		String language = null;
		
		String systemLanguage = Locale.getDefault().getLanguage();
		ArrayList<String> supportedLanguagesList = new ArrayList<String>(Arrays.asList(SUPPORTED_LANGUAGES));
		
		if (configLanguage != null && supportedLanguagesList.contains(configLanguage.toLowerCase())) {
			language = configLanguage.toLowerCase();
		} else if (supportedLanguagesList.contains(systemLanguage)) {
			language = systemLanguage;
		} else {
			language = DEFAULT_LANGUAGE;
		}
		
		country = (configCountry == null)? "": configCountry.toUpperCase();
		
		locale = new Locale(language, country);
		
		/* Creating the first generic MessageBundle after choosing the proper locale */
		MessageBundle.addGenericBundle(DEFAULT_FIRST_BUNDLE_PACKAGE, DEFAULT_FIRST_BUNDLE_BASENAME);
		
		for (MessageBundle messageBundle : resourceBundleMap.values())
			messageBundle.loadResource();
	}
	
	public static String getLanguage() {
		if (locale == null) {
			System.err.println("getLanguage() should never be called before setLocale()... I will be setting a default language.");
			setLocale(DEFAULT_LANGUAGE, DEFAULT_COUNTRY);
		}
		return locale.getLanguage();
	}
	
	public static LanguageEnum getLanguageEnum() {
		String languageStr = getLanguage();
		if (languageStr != null) {
			return LanguageEnum.valueOfFromAbbreviation(languageStr.toUpperCase());
		} else {
			return null;
		}
	}
	
	public static String getCountry() {
		if (locale == null) {
			System.err.println("getCountry() should never be called before setLocale()... I will be setting a default country.");
			return null;
		}
		return locale.getCountry();
	}
	
	public static Locale getLocale() {
		return locale;
	}
}
