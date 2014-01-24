package com.philippelangevin.sdk.uiUtil.formatter;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField.AbstractFormatter;

import org.joda.time.Duration;
import org.joda.time.IllegalFieldValueException;

import com.philippelangevin.sdk.util.StringUtil;

/**
 * Format to and from a {@link Duration}. The formats handles by this class are HHhmm, hours in decimal (, and .)
 * and digits has 1000 => 10h00, 500 => 5h00, 20 => 20h00 and 9 => 9h00.
 * @author pcharette
 */
public class DurationFormatter extends AbstractFormatter {

	private static final long serialVersionUID = -2056037935451653690L;

	//1 => 1h00
	//10 => 10h00
	//100 => 1h00
	//1000 => 10h00
	//01 => 1h00
	//001 => 0h01
	//0001 => 0h01
	//1.5 => 1h30
	//1,5 => 1h30
	//,5 => 0h30
	//h30 => 0h30
	private static final Pattern DURATION_PATTERN_H = Pattern.compile("(\\d*)h(\\d{0,2})");
	private static final Pattern DURATION_PATTERN_DEC = Pattern.compile("(\\d*[,\\.]\\d*)");
	private static final Pattern DURATION_PATTERN_NUMS = Pattern.compile("(\\d+)");
	private static final Pattern DURATION_PATTERN = Pattern.compile("(?:" + DURATION_PATTERN_H.pattern() + "|" + DURATION_PATTERN_DEC + "|" + DURATION_PATTERN_NUMS + ")");
	private static final String DOUBLE_SEPARATOR = ".";
	private static double MINS_IN_MS = 1000.0 * 60;
	private static double HOURS_IN_MS = MINS_IN_MS * 60;

	private static long parseLong(String str) throws NumberFormatException {
		if (StringUtil.isEmpty(str)) {
			return 0l;
		}
		return Integer.parseInt(str);
	}

	private static double parseDouble(String str) throws NumberFormatException {
		if (StringUtil.isEmpty(str)) {
			return 0d;
		}
		if (str.startsWith(DOUBLE_SEPARATOR)) {
			str = "0" + str;
		}
		if (str.endsWith(DOUBLE_SEPARATOR)) {
			str += "0";
		}
		return Double.parseDouble(str);
	}

	private boolean decimal;

	/**
	 * Format {@link Duration} to string in format HH:mm.
	 */
	public DurationFormatter() {
		this(false);
	}

	/**
	 * Format {@link Duration} to string. Use the decimal property to change the format from
	 * hours in decimal to HHhmm.
	 * @param decimal <code>true</code> to convert the {@link Duration} to decimal format.
	 */
	public DurationFormatter(boolean decimal) {
		this.decimal = decimal;
	}

	public boolean getDecimal() {
		return decimal;
	}

	public void setDecimal(boolean decimal) {
		this.decimal = decimal;
	}

	@Override
	public Object stringToValue(String arg0) throws ParseException {
		if (StringUtil.isEmpty(arg0)) {
			return null;
		}
		try {
			if (DURATION_PATTERN.matcher(arg0).matches()) {
				Matcher matcher = DURATION_PATTERN_H.matcher(arg0);
				if (matcher.matches()) {
					return new Duration(Math.round(parseLong(matcher.group(1)) * HOURS_IN_MS + parseLong(matcher.group(2)) * MINS_IN_MS));
				} else {
					matcher.usePattern(DURATION_PATTERN_DEC);
					if (matcher.matches()) {
						return new Duration(Math.round(parseDouble(matcher.group(1).replaceAll("[,\\.]", DOUBLE_SEPARATOR)) * HOURS_IN_MS));
					} else {
						matcher.usePattern(DURATION_PATTERN_NUMS);
						if (matcher.matches()) {
							String h, m, dur = matcher.group(1);
							if (dur.length() > 2) {
								h = dur.substring(0, dur.length() - 2);
								m = dur.substring(dur.length() - 2);
							} else {
								h = dur;
								m = "";
							}
							return new Duration(Math.round(parseLong(h) * HOURS_IN_MS + parseLong(m) * MINS_IN_MS));
						} else {
							throw new IllegalStateException("Pattern matches but no format found");
						}
					}
				}
			}
			throw new ParseException("Malformed duration", -1);
		} catch (NumberFormatException e) {
			throw new ParseException(e.getLocalizedMessage(), -1);
		} catch (IllegalFieldValueException e) {
			throw new ParseException(e.getLocalizedMessage(), -1);
		}
	}

	@Override
	public String valueToString(Object arg0) throws ParseException {
		if (arg0 == null)
			return null;
		if (arg0 instanceof Duration) {
			Duration d = (Duration) arg0;
			if (decimal) {
				return String.format("%.02f", d.getMillis() / HOURS_IN_MS);
			} else {
				return String.format("%dh%02d", (int) (d.getMillis() / HOURS_IN_MS), Math.round((d.getMillis() / MINS_IN_MS) % 60));
			}
		}
		throw new ParseException("Can only parse " + Duration.class.getCanonicalName(), -1);
	}
}
