package com.philippelangevin.sdk.uiUtil.formatter;

import java.text.ParseException;

import javax.swing.JFormattedTextField.AbstractFormatter;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.philippelangevin.sdk.util.StringUtil;

public class LocalDateFormatter extends AbstractFormatter {
	
	private static final long serialVersionUID = 2855361664015020856L;
	
	static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.shortDate();

	@Override
	public Object stringToValue(String string) throws ParseException {
		if (StringUtil.isEmpty(string)) {
			return null;
		}
		try {
			return DATE_FORMATTER.parseDateTime(string).toLocalDate();
		} catch (IllegalArgumentException e) {
			throw new ParseException(e.getLocalizedMessage(), -1);
		}
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if (value == null) {
			return null;
		}
		if (value instanceof LocalDate) {
			return DATE_FORMATTER.print((LocalDate)value);
		}
		throw new ParseException("Can only parse "+LocalDate.class.getCanonicalName(), -1);
	}
}
