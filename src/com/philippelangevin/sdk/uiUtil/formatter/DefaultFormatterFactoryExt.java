/**
 * 
 */
package com.philippelangevin.sdk.uiUtil.formatter;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DefaultFormatterFactory;

/**
 * The only difference with the default implementation is the check for isEditable() in {@link #getFormatter(JFormattedTextField)}.
 * @author pcharette
 * @date 2010-12-05
 */
public class DefaultFormatterFactoryExt extends DefaultFormatterFactory {

	private static final long serialVersionUID = 1L;

	public DefaultFormatterFactoryExt() {
		super();
	}

	public DefaultFormatterFactoryExt(AbstractFormatter defaultFormat, AbstractFormatter displayFormat, AbstractFormatter editFormat, AbstractFormatter nullFormat) {
		super(defaultFormat, displayFormat, editFormat, nullFormat);
	}

	public DefaultFormatterFactoryExt(AbstractFormatter defaultFormat, AbstractFormatter displayFormat, AbstractFormatter editFormat) {
		super(defaultFormat, displayFormat, editFormat);
	}

	public DefaultFormatterFactoryExt(AbstractFormatter defaultFormat, AbstractFormatter displayFormat) {
		super(defaultFormat, displayFormat);
	}

	public DefaultFormatterFactoryExt(AbstractFormatter defaultFormat) {
		super(defaultFormat);
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.DefaultFormatterFactory#getFormatter(javax.swing.JFormattedTextField)
	 */
	@Override
	public AbstractFormatter getFormatter(JFormattedTextField source) {
		JFormattedTextField.AbstractFormatter format = null;

        if (source == null) {
            return null;
        }
        Object value = source.getValue();

        if (value == null) {
            format = getNullFormatter();
        }
        if (format == null) {
        	//the only difference with the default implementation is the check for isEditable()
            if (source.hasFocus() && source.isEditable()) {
                format = getEditFormatter();
            }
            else {
                format = getDisplayFormatter();
            }
            if (format == null) {
                format = getDefaultFormatter();
            }
        }
        return format;
	}
}
