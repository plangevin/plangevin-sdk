package com.philippelangevin.sdk.util;

import java.beans.PropertyChangeEvent;

/**
 * An event change for an Observable map. There is 3 types of change, add, remove and change. All 3 event types have
 * the {@link ObservableMap} source, the type, the property (also the type) and the key. The add also has the new
 * value, the remove has the old value and the change has the new and possibly the old. If the map observes its content,
 * the property change throws by that content prevent it from having the old value. 
 * 
 * @author pcharette
 */
public class ObservableMapChangeEvent extends PropertyChangeEvent {

	private static final long serialVersionUID = -6741197957480048826L;

	public static enum Type {
		CHANGE_ITEM, ADD_ITEM, REMOVE_ITEM
	};

	public static <K, V> ObservableMapChangeEvent createAdd(ObservableMap<K, V> source, K key, V newValue) {
		return new ObservableMapChangeEvent(source, Type.ADD_ITEM, key, null, newValue);
	}

	public static <K, V> ObservableMapChangeEvent createRemove(ObservableMap<K, V> source, K key, V oldValue) {
		return new ObservableMapChangeEvent(source, Type.REMOVE_ITEM, key, oldValue, null);
	}

	public static <K, V> ObservableMapChangeEvent createChange(ObservableMap<K, V> source, K key, V oldValue, V newValue) {
		return new ObservableMapChangeEvent(source, Type.CHANGE_ITEM, key, oldValue, newValue);
	}

	protected final Type type;
	protected final Object key;

	private <K, V> ObservableMapChangeEvent(ObservableMap<K, V> source, Type type, K key, V oldValue, V newValue) {
		super(source, type.toString(), oldValue, newValue);
		this.type = type;
		this.key = key;
	}

	public Object getKey() {
		return key;
	}

	public Type getType() {
		return type;
	}
}
