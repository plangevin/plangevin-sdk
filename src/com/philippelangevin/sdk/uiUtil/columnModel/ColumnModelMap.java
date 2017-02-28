/**
 * 
 */
package com.philippelangevin.sdk.uiUtil.columnModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pcharette
 */
/*
 * History
 * ------------------------------------------------
 * Date			Name		BT		Description
 * 2011-01-12	pcharette
 */
public class ColumnModelMap extends HashMap<ColumnModelIF<?>, Object>{
	
	private static final long serialVersionUID = 4239760904102778483L;

	public ColumnModelMap() {
		super();
	}

	public ColumnModelMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public ColumnModelMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ColumnModelMap(Map<? extends ColumnModelIF<?>, ? extends Object> m) {
		super(m);
	}

	/**
	 * @deprecated use {@link #putSafe(ColumnModelIF, Object)} instead
	 * @see ctec.sdk.uiUtil.columnModel.ColumnModelMap#put(ctec.sdk.uiUtil.columnModel.ColumnModelIF, java.lang.Object)
	 */
	@Override
	@Deprecated
	public Object put(ColumnModelIF<?> key, Object value) {
		return super.put(key, key.getColumnClass().cast(value));
	}

	@SuppressWarnings("unchecked")
	public <T> T putSafe(ColumnModelIF<T> key, T value) {
		return (T) super.put(key, value);
	}
	
	/**
	 * @deprecated use {@link #get(ColumnModelIF)} instead
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	@Override
	@Deprecated
	public Object get(Object key) {
		return super.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(ColumnModelIF<T> key) {
		return (T) super.get(key);
	}
	
	/**
	 * @deprecated use {@link #containsKey(ColumnModelIF)} instead
	 * @see java.util.HashMap#containsKey(java.lang.Object)
	 */
	@Override
	@Deprecated
	public boolean containsKey(Object key) {
		return super.containsKey(key);
	}
	
	public boolean containsKey(ColumnModelIF<?> key) {
		return super.containsKey(key);
	}

	public Object[] toArray() {
		Object[] values = new Object[size()];
		for (Entry<ColumnModelIF<?>, Object> e : entrySet()) {
			if (e.getKey().getColNumber() >= values.length) {
				Object[] values2 = new Object[e.getKey().getColNumber()+1];
				System.arraycopy(values, 0, values2, 0, values.length);
				values = values2;
			}
			values[e.getKey().getColNumber()] = e.getValue();
		}
		return values;
	}
}
