/**
 * 
 */
package com.philippelangevin.sdk.util;

import java.beans.PropertyChangeListener;

/**
 * A listener for an {@link ObservableMap}.
 * 
 * @author pcharette
 *
 */
public interface ObservableMapListener extends PropertyChangeListener {

	/**
	 * Notification that a map has been changed. 
	 * @param event The change event.
	 */
	void mapChanged(ObservableMapChangeEvent event);
}
