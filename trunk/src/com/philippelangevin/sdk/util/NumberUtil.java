package com.philippelangevin.sdk.util;

/**
 * <p> Title: {@link NumberUtil} <p>
 * <p> Description: Contains various number parsing methods. </p>
 * <p> Company : C-Tec <p>
 *
 * @author plefebvre
 * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
 */

/*
 * History
 * ------------------------------------------------
 * Date			Name		BT		Description
 * 2010-11-19	plefebvre
 */
public class NumberUtil {
	/**
	 * Attempts to parse an Integer, returns null if it fails.
	 * @param o
	 * @return
	 */
	public static Integer parseIntSilent(Object o) {
		return parseIntSilent(o, null);
	}
	
	/**
	 * Attempts to parse an Integer, returns nullReplacement if it fails.
	 * @param o
	 * @param nullReplacement
	 * @return
	 */
	public static Integer parseIntSilent(Object o, Integer nullReplacement) {
		if (o == null) {
			return nullReplacement;
		}
		
		try {
			return Integer.parseInt(o.toString());
		} catch (NumberFormatException e) {
			return nullReplacement;
		}
	}
	
	/**
	 * Attempts to parse an Integer from the numbers provided in a string,
	 * returns null if it fails. For example "-1ag3.6g7h#" will return 1367.
	 * Note that the Integer returned will never be negative.
	 * @param o
	 * @return
	 */
	public static Integer parseIntNumbersOnlySilent(Object o) {
		return parseIntNumbersOnlySilent(o, null);
	}
	
	/**
	 * Attempts to parse an Integer from the numbers provided in a string,
	 * returns nullReplacement if it fails. For example "-1ag3.6g7h#" will return 1367.
	 * Note that the Integer returned will never be negative.
	 * @param o
	 * @param nullReplacement
	 * @return
	 */
	public static Integer parseIntNumbersOnlySilent(Object o, Integer nullReplacement) {
		if (o == null) {
			return nullReplacement;
		}
		
		try {
			return Integer.parseInt(o.toString().replaceAll("\\D", ""));
		} catch (NumberFormatException e) {
			return nullReplacement;
		}
		
	}
	
	/**
	 * Attempts to parse a Boolean to its Integer representation
	 * @param o
	 * @return 1 for true, 0 for false or null if o is not a boolean or null
	 */
	public static Integer parseBoolean(Boolean o){
		if(o != null){
			return o.booleanValue() ? 1 : 0;
		}
		return null;
	}
	
	/**
	 * Returns true if both numbers have the same, or close enough, values.
	 * Values are considered the same if their first 5 digits are identical.
	 * Null values are supported.
	 * @param n1
	 * @param n2
	 * @return
	 */
	public static boolean valuesEqual(Number n1, Number n2) {
		return n1 == null ? n2 == null: n2 == null? false: Math.abs(n1.doubleValue() - n2.doubleValue()) < 0.000001;
	}
	
	/**
	 * Returns whether the number provided is a valid integer.
	 * @param s
	 * @return
	 */
	public static boolean isInteger(String s) {
		if (s == null) {
			return false;
		} else {
			return s.matches("\\d+");
		}
	}
	
}
