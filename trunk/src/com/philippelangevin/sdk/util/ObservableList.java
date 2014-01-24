package com.philippelangevin.sdk.util;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.philippelangevin.sdk.dataStructure.ObjectUtil;

/**
 * An observable list that implements {@link ListModel} and fires modifications when its structure change. This
 * list can also listen on its data and fire content change the data is changed.
 * 
 * @author pcharette
 * @date 2011-04-06
 */
public class ObservableList<E> extends ArrayList<E> implements ListModel, PropertyChangeListener {

	private static final long serialVersionUID = 5929114207290192466L;

	public static final String CHANGE_ITEM_PROPERTY = "changeItem";
	public static final String ADD_ITEM_PROPERTY = "newItem";
	public static final String DELETE_ITEM_PROPERTY = "deleteItem";

	private EventListenerList listDataListeners = new EventListenerList();
	private boolean listeningContent;

	/**
	 * Creates an empty list. The list will listen to its content if listeningContent is <code>true</code>.
	 * @param listeningContent If <code>true</code>, the list will listen on its content.
	 */
	public ObservableList(boolean listeningContent) {
		super();
		this.listeningContent = listeningContent;
	}

	/**
	 * Creates an empty list. The list will listen to its content if listeningContent is <code>true</code>.
	 * Also adds an observable list listener.
	 * @param listeningContent If <code>true</code>, the list will listen on its content.
	 * @param observableListListener
	 */
	public ObservableList(boolean listeningContent, ObservableListListener observableListListener) {
		this(listeningContent);
		addObservableListListener(observableListListener);
	}

	/**
	 * Creates a list with the data of c that listens to its content, and adds an observable list listener.
	 * @param listeningContent
	 * @param c Initial data.
	 */
	public ObservableList(boolean listeningContent, Collection<? extends E> c) {
		super(c);
		this.listeningContent = listeningContent;
	}

	/**
	 * Creates a list with the data of c that listens to its content if listeningContent is
	 * <code>true</code>, and adds an observable list listener.
	 * @param listeningContent
	 * @param c
	 * @param observableListListener
	 */
	public ObservableList(boolean listeningContent, Collection<? extends E> c, ObservableListListener observableListListener) {
		super(c);
		this.listeningContent = listeningContent;
		addObservableListListener(observableListListener);
	}

	/**
	 * Create a list with an initial capacity that listen on its content.
	 * @param listeningContent
	 * @param initialCapacity The initial capacity of the list.
	 */
	public ObservableList(boolean listeningContent, int initialCapacity) {
		super(initialCapacity);
		this.listeningContent = listeningContent;
	}

	@Override
	public int getSize() {
		return size();
	}

	@Override
	public E getElementAt(int index) {
		return get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listDataListeners.add(ListDataListener.class, l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listDataListeners.remove(ListDataListener.class, l);
	}

	public void addObservableListListener(ObservableListListener l) {
		listDataListeners.add(ObservableListListener.class, l);
	}

	public void removeObservableListListener(ObservableListListener l) {
		listDataListeners.remove(ObservableListListener.class, l);
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		listDataListeners.add(PropertyChangeListener.class, l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listDataListeners.remove(PropertyChangeListener.class, l);
	}

	public boolean isListeningContent() {
		return listeningContent;
	}

	@Override
	public void add(int index, E element) {
		super.add(index, element);
		if (listeningContent) {
			ObjectUtil.addPropertyChangeListener(element, this);
		}
		fireIntervalAdded(index, index, Collections.singletonList(element));
	}

	@Override
	public boolean add(E e) {
		add(size(), e);
		return true;
	}

	@Override
	public E remove(int index) {
		E old = super.remove(index);
		if (listeningContent) {
			ObjectUtil.removePropertyChangeListener(old, this);
		}
		fireIntervalRemoved(index, old);
		return old;
	}

	@Override
	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index != -1) {
			E old = super.remove(index);
			if (listeningContent) {
				ObjectUtil.removePropertyChangeListener(old, this);
			}
			fireIntervalRemoved(index, old);
			return true;
		}
		return false;
	}

	@Override
	public E set(int index, E element) {
		E old = super.set(index, element);
		fireContentsChanged(index, Collections.singletonList(old));
		return old;
	}

	@Override
	public void clear() {
		int size = size();
		if (size > 0) {
			if (listeningContent) {
				for (E e : this) {
					ObjectUtil.removePropertyChangeListener(e, this);
				}
			}
			List<E> oldList = Collections.unmodifiableList(new ArrayList<E>(this));
			super.clear();
			fireIntervalRemoved(0, size - 1, oldList);
		}
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return addAll(size(), c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		if (super.addAll(index, c)) {
			if (listeningContent) {
				for (E e : c) {
					ObjectUtil.addPropertyChangeListener(e, this);
				}
			}
			fireIntervalAdded(index, index + c.size() - 1, Collections.unmodifiableList(new ArrayList<E>(c)));
			return true;
		}
		return false;
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		if (listeningContent) {
			for (int i = fromIndex; i <= toIndex; ++i) {
				ObjectUtil.removePropertyChangeListener(get(i), this);
			}
		}
		List<E> removedList = new ArrayList<E>(subList(fromIndex, toIndex));
		super.removeRange(fromIndex, toIndex);
		fireIntervalRemoved(fromIndex, toIndex, Collections.unmodifiableList(removedList));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		return new ObservableList<E>(listeningContent, (Collection<E>) super.clone());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		int index = indexOf(evt.getSource());
		if (index == -1) {
			if (listeningContent) {
				ObjectUtil.removePropertyChangeListener(evt.getSource(), this);
			}
		} else {
			fireContentsChanged(index, null);
		}
	}

	protected void fireIntervalAdded(int index0, int index1, List<E> addedList) {
		assert (index1 - index0 == addedList.size() - 1);
		ObservableListChangeEvent lde = null;
		IndexedPropertyChangeEvent pce = null;
		for (Object l : listDataListeners.getListenerList()) {
			if (l instanceof ObservableListListener) {
				if (lde == null) {
					lde = new ObservableListChangeEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1, addedList);
				}
				((ObservableListListener) l).listChanged(lde);
			} else if (l instanceof ListDataListener) {
				if (lde == null) {
					lde = new ObservableListChangeEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1, addedList);
				}
				((ListDataListener) l).intervalAdded(lde);
			} else if (l instanceof PropertyChangeListener) {
				if (pce == null) {
					pce = new IndexedPropertyChangeEvent(this, ADD_ITEM_PROPERTY, null, null, index0);
				}
				((PropertyChangeListener) l).propertyChange(pce);
			}
		}
	}

	protected void fireIntervalRemoved(int index0, int index1, List<E> removedList) {
		assert (index1 - index0 == removedList.size() - 1);
		ObservableListChangeEvent lde = null;
		IndexedPropertyChangeEvent pce = null;
		for (Object l : listDataListeners.getListenerList()) {
			if (l instanceof ObservableListListener) {
				if (lde == null) {
					lde = new ObservableListChangeEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1, removedList);
				}
				((ObservableListListener) l).listChanged(lde);
			} else if (l instanceof ListDataListener) {
				if (lde == null) {
					lde = new ObservableListChangeEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1, removedList);
				}
				((ListDataListener) l).intervalRemoved(lde);
			} else if (l instanceof PropertyChangeListener) {
				if (pce == null) {
					pce = new IndexedPropertyChangeEvent(this, DELETE_ITEM_PROPERTY, null, null, index0);
				}
				((PropertyChangeListener) l).propertyChange(pce);
			}
		}
	}

	protected void fireIntervalRemoved(int index, E old) {
		fireIntervalRemoved(index, index, Collections.singletonList(old));
	}

	protected void fireContentsChanged(int index, List<E> oldData) {
		assert (oldData == null || oldData.size() == 1);
		ObservableListChangeEvent lde = null;
		IndexedPropertyChangeEvent pce = null;
		for (Object l : listDataListeners.getListenerList()) {
			if (l instanceof ListDataListener) {
				if (lde == null) {
					lde = new ObservableListChangeEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index, oldData);
				}
				((ListDataListener) l).contentsChanged(lde);
			} else if (l instanceof ObservableListListener) {
				if (lde == null) {
					lde = new ObservableListChangeEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index, oldData);
				}
				((ObservableListListener) l).listChanged(lde);
			} else if (l instanceof PropertyChangeListener) {
				if (pce == null) {
					pce = new IndexedPropertyChangeEvent(this, CHANGE_ITEM_PROPERTY, null, null, index);
				}
				((PropertyChangeListener) l).propertyChange(pce);
			}
		}
	}
}
