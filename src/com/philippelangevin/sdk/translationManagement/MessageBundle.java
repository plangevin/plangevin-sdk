package com.philippelangevin.sdk.translationManagement;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class MessageBundle {
	public final String DEFAULT_BASENAME = "messages";
	public static final String NOT_FOUND_UNDETAILED = "NOSTRINGFOUND";
	
	protected String currentPackage = "";
	protected String baseName = DEFAULT_BASENAME;
	
	protected MessageBundle parent = null;
	protected ResourceBundle resource = null;
	
	protected static MessageBundle firstGenericBundle = null;
	
	/*
	 * This private constructor is used by addGenericBundle(),
	 * to avoid calling loadResource() upon creation.
	 */
	protected MessageBundle() {}
	
	public MessageBundle(String currentPackage) {
		this.currentPackage = currentPackage;
		loadResource();
	}
	
	/**
	 * This constructor should only be used if you want a message bundle for a
	 * specific file in a specific language without linking it to any parent.
	 * We recommend using TranslationBundleFactory.getTranslationBundle() for
	 * most cases.
	 * @param currentPackage
	 * @param baseName
	 * @param locale
	 */
	public MessageBundle(String currentPackage, String baseName, Locale locale) {
		this.currentPackage = currentPackage;
		this.baseName = baseName;
		loadResource(locale);
	}
	
	public MessageBundle getParent() {
		return parent;
	}
	
	public void setParent (MessageBundle parent) {
		this.parent = parent;
 	}
	
	public boolean containsResource() {
		return (resource != null);
	}
	
	/**
	 * Returns the translation for the specified key, returns !key! if it is missing.
	 * @param key
	 * @return
	 */
	public String getString (String key) {
		return getString(key, true);
	}
	
	/**
	 * Returns the translation for the specified key. Return value if missing determined by detailedNotFound parameter.
	 * @param key
	 * @param detailedNotFound If true, function will return !key! if key is missing.
	 * If false, function will instead return NOT_FOUND_UNDETAILED.
	 * @return
	 */
	public String getString (String key, boolean detailedNotFound) {
		if (key == null || key.length() == 0) {
			return getNotFoundKeyValue("NULL");
		}
		
		try {
			return resource.getString(key);
		} catch (Exception e) {
			/*
			 * Resource is null or the key was not found.
			 */
			try {
				return parent.getString(key, detailedNotFound);
			} catch (Exception e2) {
				/*
				 * Parent is null or the key was not found.
				 */
				if (detailedNotFound){
					System.err.println("Missing translation resource: " + key);
					return getNotFoundKeyValue(key);
				} else {
					return NOT_FOUND_UNDETAILED;
				}
			}
		}
	}
	
	/**
	 * Returns the translation for the specified key. Returns the replacement string if the value is missing.
	 * @param key The translation key to find.
	 * @param replacement The replacement string to return if the key is not found.
	 * @return The translation value or the replacement string.
	 */
	public String getString(String key, String replacement) {
		if (key == null || key.length() == 0) {
			return getNotFoundKeyValue("NULL");
		}
		
		try {
			return resource.getString(key);
		} catch (Exception e) {
			/*
			 * Resource is null or the key was not found.
			 */
			return replacement;
		}
	}
	
	protected void loadResource() {
		loadResource(TranslationBundleFactory.getLocale());
	}
	
	protected void loadResource(Locale locale) {
		try {
			resource = PropertyResourceBundle.getBundle(currentPackage + "." + baseName, locale);
		} catch (Exception e) {
			/*
			 * Resource not found or invalid locale provided.
			 */
			resource = null;
		}
	}
	
	/**
	 * The generic bundles are the parents of all other message bundles. The
	 * first generic bundle is loaded from the package Ctec of the project
	 * CtecSDK by the TranslationBundleFactory. It is meant to contain generic
	 * translations that will be used often throughout the application.
	 * 
	 * @param genericPackage
	 * @param genericBaseName
	 * @return
	 */
	public static MessageBundle addGenericBundle(String genericPackage, String genericBaseName) {
		if (genericPackage == null || genericBaseName == null)
			return null;
		
		MessageBundle newBundle = new MessageBundle();
		newBundle.currentPackage = genericPackage;
		newBundle.baseName = genericBaseName;
		newBundle.parent = null;
		
		if (firstGenericBundle == null) {
			firstGenericBundle = newBundle;
		} else {
			/*
			 * We return the same package if it has already been loaded.
			 */
			MessageBundle findGenericBundle = firstGenericBundle;
			while (findGenericBundle.parent != null) {
				if (findGenericBundle.currentPackage != null && findGenericBundle.baseName != null
						&& findGenericBundle.currentPackage.equals(genericPackage) && findGenericBundle.baseName.equals(genericBaseName)) {
					return findGenericBundle;
				} else {
					findGenericBundle = findGenericBundle.parent;
				}
			}
			/*
			 * We add the new bundle at the end of the linked list of generic bundles.
			 */
			findGenericBundle.parent = newBundle;
		}
		
		newBundle.loadResource();
		return newBundle;
	}
	
	public static MessageBundle getGenericBundle() {
		return firstGenericBundle;
	}
	
	public String getNotFoundKeyValue(String key) {
		return "!" + key + "!";
	}
}
