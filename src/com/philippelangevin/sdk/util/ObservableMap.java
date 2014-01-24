/**
 * 
 */
package com.philippelangevin.sdk.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import com.philippelangevin.sdk.dataStructure.ObjectUtil;

/**
 * An observable map that trigger events on modification. It can also listen on its content for
 * property change and fire a modification event if it occurs.
 * 
 * @author pcharette
 * @date 2011-04-06
 */
public class ObservableMap<K, V> extends HashMap<K, V> implements PropertyChangeListener {

	private static final long serialVersionUID = -6181495116225874219L;

	private EventListenerList mapListeners = new EventListenerList();
	private boolean listeningContent;

	/**
	 * Create a new empty map. It will listen on its content if listeningContent is <code>true</code>.
	 */
	public ObservableMap(boolean listeningContent) {
		super();
		this.listeningContent = listeningContent;
	}

	/**
	 * Create a new empty map that is listening on its content with an initial capacity and a load factor.
	 * @param initialCapacity The initial capacity of the map.
	 * @param loadFactor The load factor to use to increment the map size.
	 */
	public ObservableMap(boolean listeningContent, int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		this.listeningContent = listeningContent;
	}

	/**
	 * Create a new empty map that is listening on its content with an initial capacity.
	 * @param initialCapacity The initial capacity of the map.
	 */
	public ObservableMap(boolean listeningContent, int initialCapacity) {
		super(initialCapacity);
		this.listeningContent = listeningContent;
	}

	/**
	 * Create a new map that is listening on its content with 
	 * @param m
	 */
	public ObservableMap(boolean listeningContent, Map<? extends K, ? extends V> m) {
		this(listeningContent, Math.round(m.size() / 0.75f));
		putAll(m);
	}

	/**
	 * Create a new map that is listening on its content with 
	 * @param m
	 */
	public ObservableMap(ObservableMap<? extends K, ? extends V> m) {
		this(m.isListeningContent(), Math.round(m.size() / 0.75f));
		putAll(m);
	}

	@Override
	public void clear() {
		// Remove all elements via iterator to trigger notification
		Iterator<K> iterator = keySet().iterator();

		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
	}

	@Override
	public V put(K key, V value) {
		V lastValue = null;
		if (value != null && listeningContent) {
			ObjectUtil.addPropertyChangeListener(value, this);
		}
		ObservableMapChangeEvent e;
		if (containsKey(key)) {
			e = ObservableMapChangeEvent.createChange(this, key, lastValue, value);
		} else {
			e = ObservableMapChangeEvent.createAdd(this, key, value);
		}
		lastValue = super.put(key, value);
		fireMapChanged(e);
		if (lastValue != null) {
			ObjectUtil.removePropertyChangeListener(lastValue, this);
		}
		return lastValue;
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new EntrySet(super.entrySet());
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (K key : m.keySet()) {
			put(key, m.get(key));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		if (containsKey(key)) {
			V value = super.remove(key);
			if (listeningContent && value != null) {
				ObjectUtil.removePropertyChangeListener(value, this);
			}
			fireMapChanged(ObservableMapChangeEvent.createRemove(this, (K) key, value));
			return value;
		}

		return null;
	}

	public void addObservableMapListener(ObservableMapListener listener) {
		mapListeners.add(ObservableMapListener.class, listener);
	}

	public void removeObservableMapListener(ObservableMapListener listener) {
		mapListeners.remove(ObservableMapListener.class, listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		mapListeners.add(PropertyChangeListener.class, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		mapListeners.remove(PropertyChangeListener.class, listener);
	}

	private class EntryIterator implements Iterator<Map.Entry<K, V>> {
		private Iterator<Map.Entry<K, V>> realIterator;
		private Map.Entry<K, V> last;

		EntryIterator() {
			realIterator = ObservableMap.super.entrySet().iterator();
		}

		@Override
		public boolean hasNext() {
			return realIterator.hasNext();
		}

		@Override
		public Map.Entry<K, V> next() {
			last = realIterator.next();

			return last;
		}

		@Override
		public void remove() {
			if (last == null) {
				throw new IllegalStateException();
			}

			Object toRemove = last.getKey();
			last = null;
			ObservableMap.this.remove(toRemove);
		}
	}

	private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
		private Set<Map.Entry<K, V>> set;

		public EntrySet(Set<Map.Entry<K, V>> set) {
			this.set = set;
		}

		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		@Override
		public boolean contains(Object o) {
			return set.contains(o);
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean remove(Object o) {
			if (o instanceof Map.Entry) {
				boolean rem = set.remove(o);
				if (rem) {
					Map.Entry<K, V> e = (Map.Entry<K, V>) o;
					fireMapChanged(ObservableMapChangeEvent.createRemove(ObservableMap.this, e.getKey(), e.getValue()));
				}
				return rem;
			}
			return false;
		}

		@Override
		public int size() {
			return set.size();
		}

		@Override
		public void clear() {
			set.clear();
		}
	}

	public boolean isListeningContent() {
		return listeningContent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		return new ObservableMap<K, V>(isListeningContent(), (HashMap<K, V>) super.clone());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (containsValue(evt.getSource())) {
			fireMapChanged(ObservableMapChangeEvent.createChange(this, getKeyForValue(evt.getSource()), null, (V) evt.getSource()));
		} else {
			if (listeningContent) {
				ObjectUtil.removePropertyChangeListener(evt.getSource(), this);
			}
		}
	}

	private K getKeyForValue(Object value) {
		for (Map.Entry<K, V> e : super.entrySet()) {
			if (value != null && value.equals(e.getValue())) {
				return e.getKey();
			}
		}
		return null;
	}

	protected void fireMapChanged(ObservableMapChangeEvent event) {
		for (Object l : mapListeners.getListenerList()) {
			if (l instanceof ObservableMapListener) {
				((ObservableMapListener) l).mapChanged(event);
			} else if (l instanceof PropertyChangeListener) {
				((PropertyChangeListener) l).propertyChange(event);
			}
		}
	}
}
