/**
 * 
 */
package com.philippelangevin.sdk.database.transferableObject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.philippelangevin.sdk.dataStructure.HashCodeUtil;
import com.philippelangevin.sdk.dataStructure.ObjectUtil;
import com.philippelangevin.sdk.dataStructure.ObjectUtil.NULL_COMPARATOR_ENUM;

/**
 * @author vgaudreault
 *
 */
  /*
   * History
   * ------------------------------------------------
   * Date			Name		BT		Description
   * **********		vgaudreault			initial Revision
   * 2010-01-14		sroger				modify for Zulook design
   */

public abstract class TransferableObject implements Serializable, Cloneable, Comparable<TransferableObject>{
	private static final long serialVersionUID = 1L;
	
	public static boolean EQUAL_IS_DEEP = false;
	
	static {
		// Introspection of TOUtil to make sure it is always loaded (useful for debug)
		try {
			Class.forName(TOUtil.class.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * See getUID() for information about the uid field.
	 */
	private static AtomicInteger uidCounter = new AtomicInteger();
	private Integer uid = uidCounter.incrementAndGet();
	private PropertyChangeSupport propertyChangeSupport;
	protected Map<ColumnInfo<?>,Object> columnsDataMap;
	protected Map<AdditionalFieldInfo<?>,Object> additionnalFieldsDataMap;
	private Map<TransferableObjectInfo<?>, String> dbFieldToProperty;
	
	protected TransferableObject() {
		columnsDataMap = new TreeMap<ColumnInfo<? extends Object>, Object>();
		additionnalFieldsDataMap = new TreeMap<AdditionalFieldInfo<? extends Object>, Object>();
		dbFieldToProperty = new HashMap<TransferableObjectInfo<?>, String>();
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	protected TransferableObject(TransferableObject to) {
		this();
		synch(to);
	}
	
	/**
	 * Copies the data of the provided TO in this instance.
	 * Both TOs must be of the same class.
	 * @param to
	 */
	public void synch(TransferableObject to){
		if (this.getClass() != to.getClass()){
			throw new ClassCastException("Mismatch between " + this.getClass().toString() + " and " + to.getClass().toString() + "!");
		}
		
		//Copy everything
		for(ColumnInfo<?> ci : to.columnsDataMap.keySet()){
			Object oldValue = columnsDataMap.get(ci);
			Object newValue = ci.getMetaData().copy(to.columnsDataMap.get(ci));
			columnsDataMap.put(ci, newValue);
			firePropertyChange(ci, oldValue, newValue);
		}
		for(AdditionalFieldInfo<?> afi : to.additionnalFieldsDataMap.keySet()){
			Object oldValue = additionnalFieldsDataMap.get(afi);
			Object newValue = afi.getMetaData().copy(to.additionnalFieldsDataMap.get(afi));
			additionnalFieldsDataMap.put(afi, newValue);
			firePropertyChange(afi, oldValue, newValue);
		}
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void addPropertyChangeListener(TransferableObjectInfo<?> column, PropertyChangeListener listener) {
		addPropertyChangeListener(getPropertyName(column), listener);
	}

	public void removePropertyChangeListener(TransferableObjectInfo<?> column, PropertyChangeListener listener) {
		removePropertyChangeListener(getPropertyName(column), listener);
	}

	protected void firePropertyChange(TransferableObjectInfo<?> column, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(getPropertyName(column), oldValue, newValue);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends TransferableObject> T copy(T orig){
		TransferableObject copy = null;
		try {
			copy = orig.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		
		//Copy everything
		for(ColumnInfo<? extends Object> ci : orig.columnsDataMap.keySet()){
			copy.columnsDataMap.put(ci, ci.getMetaData().copy(orig.columnsDataMap.get(ci)));
		}
		for(AdditionalFieldInfo<? extends Object> afi : orig.additionnalFieldsDataMap.keySet()){
			copy.additionnalFieldsDataMap.put(afi, afi.getMetaData().copy(orig.additionnalFieldsDataMap.get(afi)));
		}
		return (T)copy;
	}
	
	public String getPropertyName(TransferableObjectInfo<?> column) {
		if (!dbFieldToProperty.containsKey(column)) {
			dbFieldToProperty.put(column, TOUtil.getPropertyName(column));
		}
		return dbFieldToProperty.get(column);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(TransferableObjectInfo<T> toInfo) {
		if (toInfo instanceof ColumnInfo) {
			return (T) columnsDataMap.get(toInfo);
		} else if (toInfo instanceof AdditionalFieldInfo) {
			return (T) additionnalFieldsDataMap.get(toInfo);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * A generic setter for one field in a TransferableObject
	 * @param <T> Type associated with the field that's being edited
	 * @param toInfo Identifier for the field to edit
	 * @param value Value to put in the field
	 * @return true if the value was actually changed; false if the old and new value were equal.
	 */
	public <T> boolean set(TransferableObjectInfo<T> toInfo, T value) {
		T old = get(toInfo);
		if (!ObjectUtil.equals(value, old)) {
			value = toInfo.getMetaData().parse(value);
			if (toInfo instanceof ColumnInfo<?>) {
				columnsDataMap.put((ColumnInfo<T>) toInfo, value);
			} else if (toInfo instanceof AdditionalFieldInfo<?>) {
				additionnalFieldsDataMap.put((AdditionalFieldInfo<T>) toInfo, value);
			} else {
				throw new IllegalArgumentException();
			}
			firePropertyChange(toInfo, old, value);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This method is useful to know if a field has been set... there is a difference
	 * between a value that has never been set, and a value that has been set to null.
	 * @param toInfo
	 * @return
	 */
	public boolean containsValue(TransferableObjectInfo<?> toInfo) {
		if (toInfo instanceof ColumnInfo<?>) {
			return columnsDataMap.containsKey(toInfo);
		} else if (toInfo instanceof AdditionalFieldInfo<?>) {
			return additionnalFieldsDataMap.containsKey(toInfo);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Removes the value for a specified column/field (different than setting null).
	 * @param toInfo
	 */
	public <T> void removeValue(TransferableObjectInfo<T> toInfo) {
		Object oldValue;
		if (toInfo instanceof ColumnInfo<?>) {
			oldValue = columnsDataMap.remove(toInfo);
		} else if (toInfo instanceof AdditionalFieldInfo<?>) {
			oldValue = additionnalFieldsDataMap.remove(toInfo);
		} else {
			throw new IllegalArgumentException();
		}
		firePropertyChange(toInfo, oldValue, null);
	}
	
	public abstract TransferableObjectStructureIF getTOStructure();
	
	/**
	 * Finds the string-represented column and returns its value.
	 * This method is less efficient than get(TransferableObjectInfo).
	 * @param toInfo
	 * @return
	 */
	public Object get(String toInfo){
		if (toInfo == null) {
			return null;
		}
		
		TransferableObjectStructureIF structure = getTOStructure();
		
		ColumnInfo<?>[] columns = structure.getColumns();
		for (ColumnInfo<?> columnInfo: columns) {
			if (String.valueOf(columnInfo).equalsIgnoreCase(toInfo)) {
				return get(columnInfo);
			}
		}
		
		AdditionalFieldInfo<?>[] fields = structure.getAdditionalFields();
		for (AdditionalFieldInfo<?> field: fields) {
			if (String.valueOf(field).equalsIgnoreCase(toInfo)) {
				return get(field);
			}
		}
		
		return null;
	}
	
	/**
	 * Calculates the hashCode based on the primary key.
	 */
	@Override
	public int hashCode() {
		ColumnInfo<?> cols[] = getTOStructure().getColumns();
		int primKeySize = getTOStructure().getPrimaryKeySize();
		int seed = HashCodeUtil.SEED;
		for (int i = 0; i < primKeySize; ++i) {
			seed = HashCodeUtil.hash(seed, get(cols[i]));
		}
		return seed;
	}
	
	/**
	 * Compare two TO with their primary key
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo( TransferableObject arg0 ) {
		if( null == arg0 ) {
			return -1;
		} else if( !getClass().equals( arg0.getClass() ) ) {
			throw new ClassCastException();
		}
		ColumnInfo<?>[] columns = getTOStructure().getColumns();
		try {
			for( int i = 0; i < getTOStructure().getPrimaryKeySize(); i++ ) {
				int comp = ObjectUtil.compare(get(columns[i]), arg0.get(columns[i]), NULL_COMPARATOR_ENUM.NULL_LAST);
				if (comp != 0) {
					return comp;
				}
			}
			return 0;
		} catch( Throwable e ) {
			//XXX why???
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Verifies the equality of two TOs.
	 * As of 2011-04-13 when TransferableObject.EQUAL_IS_DEEP is true, deepEquals() will be invoked;
	 * otherwise shallowEquals() will be invoked. Ideally we should always set this boolean to true
	 * upon starting new applications, but the default is false to allow backward-compatibility.
	 * @param o
	 * @return
	 */
	@Override
	public boolean equals(Object o){
		if (EQUAL_IS_DEEP) {
			return deepEquals(o);
		} else {
			return shallowEquals(o);
		}
	}
	
	/**
	 * Verifies the equality of the primary keys of two TOs.
	 * @param o
	 * @return
	 */
	public boolean shallowEquals(Object o) {
		if (o == null || !(o instanceof TransferableObject) || !getClass().equals(o.getClass())) {
			return false;
		} else if (this == o) {
			return true;
		}
		
		TransferableObject to = (TransferableObject) o;
		
		ColumnInfo<?>[] columns = getTOStructure().getColumns();
		for (int i = 0; i < to.getTOStructure().getPrimaryKeySize(); i++) {
			Object value1 = get(columns[i]);
			Object value2 = to.get(columns[i]);
			
			if (value1 == null || !value1.equals(value2)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Verifies the equality of the columns of two TOs (additional fields are ignored).
	 * @param o
	 * @return
	 */
	public boolean deepEquals(Object o){
		if (o == null || !(o instanceof TransferableObject) || !getClass().equals(o.getClass())) {
			return false;
		} else if (this == o) {
			return true;
		}
		
		TransferableObject to = (TransferableObject) o;
		
		ColumnInfo<?>[] columns = getTOStructure().getColumns();
		int nbPrimaryKeys = to.getTOStructure().getPrimaryKeySize();
		for (int i = 0; i < columns.length; i++) {
			Object value1 = get(columns[i]);
			Object value2 = to.get(columns[i]);
			
			//handle primary key values (all simple base types)
			if (i < nbPrimaryKeys) {
				if (value1 == null || !value1.equals(value2)) {
					return false;
				}
			}
			//handle null values
			else if (value1 == null || value2 == null){
				//an auto-number with a null value is necessarily not equal
				if (columns[i].getMetaData().isAutoNumber() || value1 != null || value2 != null) {
					return false;
				}
			}
			//handle array
			else if (value1.getClass().isArray()){
				if (!Arrays.deepEquals((Object[])value1, (Object[])value2)) {
					return false;
				}
			}
			//handle regular base type values
			else if (!value1.equals(value2)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @deprecated Use copy constructor instead of clone
	 */
	@Override
	@Deprecated
	public Object clone() {
		try {
			TransferableObject to = (TransferableObject) super.clone();
			to.uid = uidCounter.incrementAndGet();
			return to;
			
		} catch( CloneNotSupportedException e ) {
			e.printStackTrace();
			throw new AssertionError();
		}
	}
	
	/**
	 * Returns the unique ID value associated with this instance.
	 * The UID is never modified, its purpose is to help store TOs that contain
	 * auto-incremental keys into maps without having to modify the references.
	 * 
	 * If you copy or clone a TO, the new instance will have a different uid than
	 * the original TO since it will be a different instance.
	 * 
	 * Please note that this uid is not used in equals() nor in deepEquals().
	 * @return The unique ID associated with this TO.
	 */
	public final Integer getUID() {
		return uid;
	}
	
	/**
	 * Returns a string representing the primary key values separated by ':'.
	 * @return
	 */
	public String getPrimaryKeyValues() {
		return TOUtil.getPrimaryKeyValues(this);
	}
	
	/**
	 * Returns a string representing the primary key values separated by separator.
	 * @param separator
	 * @return
	 */
	public String getPrimaryKeyValues(String separator) {
		return TOUtil.getPrimaryKeyValues(this, separator);
	}
	
	@Override
	public String toString() {
		return TOUtil.dump(this);
	};
	
	/**
	 * Returns a debug-friendly version of this TO (all data on several lines).
	 * @return
	 */
	public String toStringDebug() {
		return TOUtil.dump(this);
	}
	
	/**
	 * Returns a compact representation of this TO.
	 * Circular references with sub-TOs and sub-collections should be avoided.
	 * This is a convenient method invoking TOPrinter.toStringCompact().
	 * @return
	 */
	public String toStringCompact() {
		return TOUtil.toStringCompact(this, true, true);
	}
	
	/**
	 * Returns a compact representation of this TO.
	 * Circular references with sub-TOs and sub-collections should be avoided.
	 * This is a convenient method invoking TOPrinter.toStringCompact(printTOContent, printCollectionContent).
	 * @param printTOContent Whether sub-TOs are printed (otherwise it's just the class name).
	 * @param printCollectionContent Whether collections are recursively printed (otherwise just the size).
	 * @return
	 */
	public String toStringCompact(boolean printTOContent, boolean printCollectionContent) {
		return TOUtil.toStringCompact(this, printTOContent, printCollectionContent);
	}

	public ColumnInfo<?>[] getPrimaryColumns() {
		ColumnInfo<?>[] col = new ColumnInfo<?>[getTOStructure().getPrimaryKeySize()];
		System.arraycopy(getTOStructure().getColumns(), 0, col, 0, col.length);
		return col;
	}

	public ColumnInfo<?>[] getNonPrimaryColumns() {
		ColumnInfo<?>[] col = new ColumnInfo<?>[getTOStructure().getColumns().length - getTOStructure().getPrimaryKeySize()];
		System.arraycopy(getTOStructure().getColumns(), getTOStructure().getPrimaryKeySize(), col, 0, col.length);
		return col;
	}
}
