/**
  * <p> Title: {@link MaskFormatterFactory} <p>
  * <p> Description: This class is a factory to create special MaskFormatter
  * <p> to set in JFormattedTextField
  * <p> Company : C-Tec <p>
  * 
  * @author cgendreau
  * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
  */
package com.philippelangevin.sdk.uiUtil.formatter;

import java.sql.Time;
import java.text.ParseException;

import javax.swing.text.MaskFormatter;

import com.philippelangevin.sdk.time.TimeUtil;

public final class MaskFormatterFactory {
	
	private static final String PHONE_MASK = "(###)###-####";
	
	/**
	 * This method creates a phone {@link MaskFormatter} like {@link #PHONE_MASK}
	 * * @return the new MaskFormatter or null if the creation failed
	 */
	public static MaskFormatter createPhoneMaskFormatter(){
		return createPhoneMaskFormatter(true);
	}
	
	/**
	 * This method creates a phone {@link MaskFormatter} like {@link #PHONE_MASK}
	 * with the possibility to set the valueContainsLiteralCharacters.
	 * @param valueContainsLiteralCharacters a boolean indicating if the value must
	 * 	      contain the literal character of the mask
	 * @return the new MaskFormatter or null if the creation failed
	 * 
	 */
	public static MaskFormatter createPhoneMaskFormatter(boolean valueContainsLiteralCharacters){
		MaskFormatter textMask = null;
		try {
			textMask = new MaskFormatter(PHONE_MASK);
			textMask.setPlaceholderCharacter('_');
			textMask.setValueContainsLiteralCharacters(valueContainsLiteralCharacters);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return textMask;
	}
	
	
	
	/**
	 * This method creates a phone extension {@link MaskFormatter} with 6 digits.
	 * @return the new MaskFormatter or null if the creation failed
	 */
	public static MaskFormatter createPhoneExtMaskFormatter() {
		MaskFormatter textMask = null;
		try {
			textMask = new MaskFormatter("******");
			textMask.setPlaceholderCharacter(' ');
			textMask.setValidCharacters("0123456789 ");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return textMask;
	}
	
	/**
	 * This method sets a {@link MaskFormatter} for a
	 * Canadian postal code like U#U #U#
	 * 
	 * @return the new MaskFormatter or null if the creation failed
	 */
	public static MaskFormatter createPostalCodeMask() {
		MaskFormatter textMask = null;
		try {
			textMask = new MaskFormatter("U#U #U#");
			textMask.setPlaceholderCharacter('_');
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return textMask;
	}
	
	/**
	 * This method sets a {@link MaskFormatter} with a phone number
	 * mask that accepts incomplete phone number, useful for searching.
	 * 
	 * @return the new MaskFormatter or null if the creation failed
	 */
	public static MaskFormatter createSearchFieldPhoneMask() {
		MaskFormatter textMask = null;
		try {
			textMask = new MaskFormatter("(***)***-****");
			textMask.setPlaceholderCharacter('_');
			textMask.setValidCharacters("0123456789_");

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return textMask;
	}

	/**
	 * This method sets a {@link MaskFormatter} with a the number of
	 * character of a Canadian postal code. Note that the mask
	 * is accepting letters and number in all position.  This
	 * is only used to limit the number of digits but not to
	 * validate that the postal code is valid.  It is useful
	 * for searching
	 * 
	 * @return the new MaskFormatter or null if the creation failed
	 */
	public static MaskFormatter createSearchFieldPostalCodeMask() {
		MaskFormatter textMask = null;
		try {
			textMask = new MaskFormatter("*** ***");
			textMask.setPlaceholderCharacter('_');
			textMask.setValidCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return textMask;
	}
	
	/**
	 * Creates a time {@link MaskFormatter} with the "__:__" format.
	 * Uses {@link TimeUtil.#parseTime(Object, Time)} to complete partial time entries.
	 * getValue() and setValue() work with Time objects.
	 * @param allowNull Whether null should be allowed.
	 * @return
	 * @author plefebvre
	 */
	public static MaskFormatter createTimeMaskFormatter(final boolean allowNull) {
		try {
			MaskFormatter mask = new MaskFormatter("##:##") {
				private static final long serialVersionUID = -6548445178875102414L;
				
				@Override
				public Object stringToValue(String s) throws ParseException {
					Time time = TimeUtil.parseTime(s.replace(String.valueOf(getPlaceholderCharacter()), ""), null);
					if (time == null) {
						if (allowNull && !s.matches(".*\\d.*")) {
							// This is a null entry (no digits), we allow it
							return null;
						} else {
							// This is an invalid entry, we throw a ParseException to revert
							throw new ParseException("Invalid time: " + s, 0);
						}
					} else {
						// This is a valid entry
						return time;
					}
				};
				
				@Override
				public String valueToString(Object obj) throws ParseException {
					if (obj == null) {
						return super.valueToString(obj);
					} else if (obj instanceof Time) {
						return obj.toString().substring(0, getMask().length());
					} else {
						throw new ParseException("Invalid time: " + obj, 0);
					}
				}
				
				@Override
				public void setPlaceholder(String s) {
					throw new UnsupportedOperationException("Only setPlaceHolderCharacter() is allowed on this time mask formatter.");
				}
			};
			mask.setPlaceholderCharacter('_');
			return mask;
			
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private MaskFormatterFactory() {
	}
}
