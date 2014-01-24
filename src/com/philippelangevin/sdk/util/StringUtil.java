package com.philippelangevin.sdk.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.text.Collator;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdesktop.swingx.renderer.StringValue;

/**
  * <p> Title: {@link StringUtil} <p>
  * <p> Description: This class is used to display strings in a prettier way, and
  * 				 holds other string util methods. </p>
  * <p> Company : C-Tec <p>
  * 
  * @author plefebvre
  * Copyright: (c) 2009, C-Tec Inc. - All rights reserved
  */

 /*
  * History
  * ------------------------------------------------
  * Date			Name		BT		Description
  * 2009-07-07		plefebvre
  * 2010-02-08		plefebvre			Moved from A-Reporter to C-Tec SDK
  * 2010-10-27		pcharette			equals
  */

public class StringUtil {
	private static final List<Character> SMALL_ROMAN_NUMERALS = Arrays.asList('I', 'V', 'X');
	private static final Collator COLLATOR = Collator.getInstance();
	static {
		//we want é, è, ê and e to be equals
		COLLATOR.setStrength(Collator.PRIMARY);
	}
	
	public static final Comparator<String> COMPARATOR = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			return StringUtil.compare(o1, o2);
		}
	};
	
	private static boolean isSmallRomanNumeral(String text) {
		for (int i = 0; i < text.length(); i++) {
			if (!SMALL_ROMAN_NUMERALS.contains(text.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method attempts to beautify a String by replacing underscores with
	 * spaces, and by capitalizing all words.
	 * 
	 * @param text
	 * @return
	 */
	public static String beautify(Object o) {
		if (o == null) {
			return "";
		}
		String text = o.toString();
		
		/*
		 * We get rid of any underscores
		 */
		text = text.replaceAll("_", " ");
		
		/*
		 * We match any alphabetic character
		 */
		Pattern wordPattern = Pattern.compile("[0-9|\\p{javaLowerCase}|\\p{javaUpperCase}]+");
		Matcher matcher = wordPattern.matcher(text);
		
		/*
		 * We use a string builder with the whole text to ensure to keep
		 * all the other non-alphabetic characters.
		 */
		StringBuilder sb = new StringBuilder(text);
		while (matcher.find()) {
			String line = matcher.group();
			if (!line.isEmpty()) {
				/*
				 * We capitalize the first letter and the rest is lowercase.
				 * We try to leave the roman numerals in capitalized letters.
				 */
				if (!isSmallRomanNumeral(sb.substring(matcher.start(), matcher.end()))) {
					sb.replace(matcher.start(), matcher.start() + 1, line.substring(0, 1).toUpperCase());
					sb.replace(matcher.start() + 1, matcher.end(), line.substring(1).toLowerCase());
				}
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Convenience method to beautify a StringBuilder (by reference).
	 * 
	 * @param sb
	 */
	public static void beautify(StringBuilder sb) {
		String text = beautify(sb.toString());
		sb.delete(0, sb.length());
		sb.append(text);
	}
	
	// *****************************
	// Compute Levenshtein distance
	// *****************************
	// Prit sur http://www.merriampark.com/ld.htm
	/**
	 * Levenshtein distance (LD) is a measure of the similarity between two strings, which we will
	 * refer to as the source string (s) and the target string (t). The distance is the number of
	 * deletions, insertions, or substitutions required to transform s into t. For example, <br>
	 * <ul>
	 * <li>If s is "test" and t is "test", then LD(s,t) = 0, because no transformations are needed.
	 * The strings are already identical.</li>
	 * <li>If s is "test" and t is "tent", then LD(s,t) = 1, because one substitution (change "s" to
	 * "n") is sufficient to transform s into t.</li>
	 * </ul>
	 * <br>
	 * The greater the Levenshtein distance, the more different the strings are. <br>
	 * Levenshtein distance is named after the Russian scientist Vladimir Levenshtein, who devised
	 * the algorithm in 1965. If you can't spell or pronounce Levenshtein, the metric is also
	 * sometimes called edit distance. <br>
	 * The Levenshtein distance algorithm has been used in: <br>
	 * <ul>
	 * <li>Spell checking</li>
	 * <li>Speech recognition</li>
	 * <li>DNA analysis</li>
	 * <li>Plagiarism detection</li>
	 * </ul>
	 * 
	 * @param chaine1
	 * @param chaine2
	 * @return Le nb de différence entre les 2 chaines (0 pour des chaines parfaitement identiques)
	 */
	public static int getLevenshteinDistance(String chaine1, String chaine2) {
		// ****************************
		// Get minimum of three values
		// ****************************
		int matrice[][]; // matrix
		int longeur1; // length of chaine1
		int longeur2; // length of chaine2
		char s_i; // ith character of chaine1
		char t_j; // jth character of chaine2
		int cost; // cost
		
		// Step 1
		longeur1 = chaine1.length();
		longeur2 = chaine2.length();
		if (longeur1 == 0) {
			return longeur2;
		}
		if (longeur2 == 0) {
			return longeur1;
		}
		matrice = new int[longeur1 + 1][longeur2 + 1];
		
		// Step 2
		for (int position1 = 0; position1 <= longeur1; position1++) {
			matrice[position1][0] = position1;
		}
		
		for (int position2 = 0; position2 <= longeur2; position2++) {
			matrice[0][position2] = position2;
		}
		
		// Step 3
		for (int position1 = 1; position1 <= longeur1; position1++) {
			s_i = chaine1.charAt(position1 - 1);
			
			// Step 4
			for (int position2 = 1; position2 <= longeur2; position2++) {
				t_j = chaine2.charAt(position2 - 1);
				
				// Step 5
				if (s_i == t_j) {
					cost = 0;
				} else {
					cost = 1;
				}
				
				// Step 6
				matrice[position1][position2] = Minimum(matrice[position1 - 1][position2] + 1, matrice[position1][position2 - 1] + 1, matrice[position1 - 1][position2 - 1] + cost);
			}
		}
		// Step 7
		return matrice[longeur1][longeur2];
	}
	
	private static int Minimum(int a, int b, int c) {
		int min;
		min = a;
		if (b < min) {
			min = b;
		}
		if (c < min) {
			min = c;
		}
		return min;
	}
	
	public static int getLevenshteinDistanceIgnoreAccentsAndCase(String chaine1, String chaine2) {
		return getLevenshteinDistance(unAccent(chaine1.toLowerCase()), unAccent(chaine2.toLowerCase()));
	}
	
	/**
	 * Removes the accents from a String.
	 * Example: StringUtil.unAccent("HÉhé Çë") will return "Hehe Ce".
	 * 
	 * @param o
	 * @return
	 */
	public static String unAccent(Object o) {
		if (o == null) {
			return "";
		} else {
			return Normalizer.normalize(String.valueOf(o), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}", "");
		}
	}
	
	/**
	 * This replaces String.valueOf(o), with these two bug fixes:
	 *  - StringUtil.valueOf(null) returns "null", String.valueOf(null) crashes.
	 *  - If toString() returns null, this method returns "null" instead of null.
	 * @param o
	 * @return
	 */
	public static String valueOf(Object o) {
		return valueOf(o, "null");
	}
	
	/**
	 * This replaces String.valueOf(o), with a nullReplacement and these two bug fixes:
	 *  - StringUtil.valueOf(null) returns nullReplacement, String.valueOf(null) crashes.
	 *  - If toString() returns null, this method returns nullReplacement instead of null.
	 * @param o
	 * @param nullReplacement
	 * @return
	 */
	public static String valueOf(Object o, String nullReplacement) {
		if (o == null) {
			return nullReplacement;
		} else {
			String s = o.toString();
			if (s == null) {
				return nullReplacement;
			} else {
				return s;
			}
		}
	}
	
	/**
	 * Check for equality between 2 strings. A <code>null</code> string is equals to an empty string.
	 * To consider a <code>null</code> string not equals to an empty string, use {@link ctec.sdk.dataStructure.ObjectUtil#equals(Object, Object)}
	 * instead.
	 * @return <code>true</code> if the 2 string are equals.
	 */
	public static boolean equals(String s1, String s2) {
		return (s1 == null || s1.isEmpty()) ? (s2 == null || s2.isEmpty()) : s1.equals(s2);
	}
	
	/**
	 * Checks whether a String is empty, null is considered empty.
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}
	
	/**
	 * Returns the t.printStackTrace() content as a String.
	 * @param throwable An exception or error to print
	 * @return
	 */
	public static String printStackTraceToString(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		return sw.toString();
	}
	
	/**
	 * Returns a null-supported string comparison using Collator.
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int compare(String s1, String s2) {
		
		if (s1 == null) {
			return s2 == null? 0: 1;
		} else if (s2 == null) {
			return -1;
		} else {
			return COLLATOR.compare(s1, s2);
		}
	}

	/**
	 * toString that handle arrays.
	 * @param o The object to get the string representation of.
	 * @return The string representation of the object.
	 */
	public static String toString(Object o) {
		//same as String.valueOf(Object)
		if (o == null) {
			return null;
		}
		if (o.getClass().isArray()) {
			int iMax = Array.getLength(o) - 1;
			if (iMax == -1) {
				return "[]";
			}
	        StringBuilder b = new StringBuilder();
	        b.append('[');
	        for (int i = 0; ; i++) {
	            b.append(Array.get(o, i));
			    if (i == iMax) {
					return b.append(']').toString();
				}
	            b.append(", ");
	        }
		}
		return String.valueOf(o);
	}

	/**
	 * Returns a String listing the element of it separated by the separator. The separator is appended between
	 * the elements, there is no separator before the first or after the last element. This method use {@link #toString(Object)}
	 * to convert the elements of the list to strings.
	 * @param it The list of element to list.
	 * @param separator The separator to insert between the elements.
	 * @return The string listing the element of the list.
	 */
	public static String toString(Iterable<?> it, String separator) {
		StringBuilder sb = new StringBuilder();
		for (Object o : it) {
			if (sb.length() != 0) {
				sb.append(separator);
			}
			sb.append(toString(o));
		}
		return sb.toString();
	}

	/**
	 * Returns a String listing the element of it separated by the separator. The separator is appended between
	 * the elements, there is no separator before the first or after the last element. This method use the provided
	 * {@link StringValue} to convert the elements of the list to strings.
	 * @param it The list of element to list.
	 * @param separator The separator to insert between the elements.
	 * @param sv The converter to use to convert the element of the list to strings.
	 * @return The string listing the element of the list.
	 */
	public static String toString(Iterable<?> it, String separator, StringValue sv) {
		StringBuilder sb = new StringBuilder();
		for (Object o : it) {
			if (sb.length() != 0) {
				sb.append(separator);
			}
			sb.append(sv.getString(o));
		}
		return sb.toString();
	}
}
