/**
 * 
 */
package com.philippelangevin.sdk.util;

import java.util.EventListener;

/**
 * Listen on an {@link ObservableList}.
 * @author pcharette
 *
 */
public interface ObservableListListener extends EventListener {

	/**
	 * A list was changed. The event contains the source list, the type of change, the indexes where the event occurs and the data involve in the
	 * change.
	 * @param event The event that cause the change.
	 */
	void listChanged(ObservableListChangeEvent event);
}
