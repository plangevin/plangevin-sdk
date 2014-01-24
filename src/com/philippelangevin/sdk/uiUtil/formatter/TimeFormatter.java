package com.philippelangevin.sdk.uiUtil.formatter;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField.AbstractFormatter;

import org.joda.time.Duration;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalTime;

import com.philippelangevin.sdk.util.StringUtil;

/**
 * Format to and from a {@link LocalTime}. The formats handles by this class are HH:mm, hours in decimal (, and .)
 * and digits has 1000 => 10:00, 500 => 5:00, 20 => 20:00 and 9 => 9:00.
 * @author pcharette
 *
 */
public class TimeFormatter extends AbstractFormatter {
	
	private static final long serialVersionUID = 9044199483908026891L;


	//1 => 1:00
	//10 => 10:00
	//100 => 1:00
	//1000 => 10:00
	//01 => 1:00
	//001 => 0:01
	//0001 => 0:01
	//1: => 1:00
	//01: => 1:00
	private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}?)([:,\\.])?(\\d{0,2})");

	private static int parseInt(String str) throws NumberFormatException {
		if (StringUtil.isEmpty(str)) {
			return 0;
		}
		return Integer.parseInt(str);
	}
	
	private boolean decimal;
	
	/**
	 * Format {@link LocalTime} to string in format HH:mm.
	 */
	public TimeFormatter() {
		this(false);
	}
	
	/**
	 * Format {@link LocalTime} to string. Use the decimal property to change the format from
	 * hours in decimal to HH:mm.
	 * @param decimal <code>true</code> to convert the {@link LocalTime} to decimal format.
	 */
	public TimeFormatter(boolean decimal) {
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
			Matcher matcher = TIME_PATTERN.matcher(arg0);
			if (matcher.matches()) {
				String h = matcher.group(1);
				String dec = matcher.group(2) != null ? matcher.group(2).replaceAll(",", ".") : null;
				String m = matcher.group(3);
				int im = parseInt(m);
				//when we have only 2 digits, we want them both on the hour field
				if (h.length() == 1 && m.length() == 1 && arg0.length() == 2) {
					h += m;
					im = 0;
				} else if (".".equals(dec) && im != 0) {
					if (m.length() == 1)
						im *= 10;
					im = (int) Math.round((im/100.0)*60);
				}
				return new LocalTime(parseInt(h), im);
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
		if (arg0 instanceof LocalTime) {
			LocalTime d = (LocalTime)arg0;
			if (decimal) {
				return String.format("%.02f", d.getHourOfDay()+(d.getMinuteOfHour()/60.0));
			} else {
				return String.format("%d:%02d", d.getHourOfDay(), d.getMinuteOfHour());
			}
		}
		throw new ParseException("Can only parse "+Duration.class.getCanonicalName(), -1);
	}
}
