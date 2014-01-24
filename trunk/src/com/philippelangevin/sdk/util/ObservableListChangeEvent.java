/**
 * 
 */
package com.philippelangevin.sdk.util;

import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.event.ListDataEvent;

/**
 * An event meaning that an {@link ObservableList} has changed. The list can change is something is added. removed, sets or if
 * the list observed its content and an element throw a {@link PropertyChangeEvent}.
 * @author pcharette
 *
 */
public class ObservableListChangeEvent extends ListDataEvent {

	private static final long serialVersionUID = 6351985832074219840L;

	protected final List<?> data;

	/**
	 * Create the event from an {@link ObservableList} with a type ({@link ListDataEvent#INTERVAL_ADDED}, {@link ListDataEvent#INTERVAL_REMOVED} or
	 * {@link ListDataEvent#CONTENTS_CHANGED}), the indexes where the event occurs and the data that created the change.
	 * @param source The source list.
	 * @param type The type of event, one of {@link ListDataEvent#INTERVAL_ADDED}, {@link ListDataEvent#INTERVAL_REMOVED} or {@link ListDataEvent#CONTENTS_CHANGED}.
	 * @param index0 The start index of the change in the list.
	 * @param index1 The end index of the change in the list.
	 * @param data The changed data (or new, or deleted or <code>null</code> if the change is not trackable).
	 */
	public <V> ObservableListChangeEvent(ObservableList<V> source, int type, int index0, int index1, List<V> data) {
		super(source, type, index0, index1);
		this.data = data;
	}

	@Override
	public ObservableList<?> getSource() {
		return (ObservableList<?>) super.getSource();
	}

	/**
	 * The data that cause the change. This value is <code>null</code> if the change was not trackable (a property change on an observed element).
	 * @return The data.
	 */
	public List<?> getDataList() {
		return data;
	}

	/**
	 * Returns the data that cause the event. This method is safe to call if the event type is {@link ListDataEvent#CONTENTS_CHANGED}. It will
	 * throw an {@link IllegalStateException} if the data is a list with a size higher than 1.
	 * @return The data that cause the change if it involve only 1 element or if the change was not trackable.
	 * @throws IllegalStateException If the change involve more than 1 element (use {@link #getDataList()} instead).
	 */
	public Object getData() throws IllegalStateException {
		if (data == null || data.isEmpty()) {
			return null;
		}
		if (data.size() == 1) {
			return data.get(0);
		}
		throw new IllegalStateException("the data is a list");
	}
}
