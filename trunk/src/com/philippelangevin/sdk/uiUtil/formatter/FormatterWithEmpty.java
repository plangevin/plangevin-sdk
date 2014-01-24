/**
 * 
 */
package com.philippelangevin.sdk.uiUtil.formatter;

import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 * Wraps a formatter to handle the empty string and returns null.
 * @author pcharette
 * @date 2010-12-10
 */
public class FormatterWithEmpty extends AbstractFormatter {
	
	private static final long serialVersionUID = 1L;
	private AbstractFormatter delegate;
	protected final Object emptyValue;
	
	public FormatterWithEmpty(AbstractFormatter delegate) {
		this(null, delegate);
	}
	
	public FormatterWithEmpty(Object emptyValue, AbstractFormatter delegate) {
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
	
	public Object getEmptyValue() {
		return emptyValue;
	}
}
