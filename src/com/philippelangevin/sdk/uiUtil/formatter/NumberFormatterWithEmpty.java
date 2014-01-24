/**
 * 
 */
package com.philippelangevin.sdk.uiUtil.formatter;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

/**
 * @author pcharette
 * @date 2010-11-22
 */
public class NumberFormatterWithEmpty extends NumberFormatter {

	private static final long serialVersionUID = 1L;
	private NumberFormatter delegate;
	protected final Number emptyValue;
	
	public NumberFormatterWithEmpty() {
		this((Number)null);
	}
	
	public NumberFormatterWithEmpty(NumberFormat format) {
		this(null, format);
	}
	
	public NumberFormatterWithEmpty(Number emptyValue) {
		this(emptyValue, new NumberFormatter());
	}
	
	public NumberFormatterWithEmpty(Number emptyValue, NumberFormat format) {
		this(emptyValue, new NumberFormatter(format));
	}
	
	public NumberFormatterWithEmpty(NumberFormatter delegate) {
		this(null, delegate);
	}
	
	public NumberFormatterWithEmpty(Number emptyValue, NumberFormatter delegate) {
		this.delegate = delegate;
		this.emptyValue = emptyValue;
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	@Override
	public void install(JFormattedTextField ftf) {
		delegate.install(ftf);
	}

	@Override
	public void uninstall() {
		delegate.uninstall();
	}

	@Override
	public Object stringToValue(String text) throws ParseException {
		if (text == null || text.isEmpty()) {
			return emptyValue;
		}
		return delegate.stringToValue(text);
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		return delegate.valueToString(value);
	}
	
	public Number getEmptyValue() {
		return emptyValue;
	}
}
