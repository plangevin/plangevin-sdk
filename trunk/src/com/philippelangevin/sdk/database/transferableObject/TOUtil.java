package com.philippelangevin.sdk.database.transferableObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.philippelangevin.sdk.dataStructure.CollectionUtil;
import com.philippelangevin.sdk.dataStructure.ObjectUtil;
import com.philippelangevin.sdk.dataStructure.ObjectUtil.NULL_COMPARATOR_ENUM;
import com.philippelangevin.sdk.dataStructure.Tuple.Pair;
import com.philippelangevin.sdk.util.StringUtil;

/**
 * <p> Title: {@link TOUtil} <p>
 * <p> Description: Regroups the various TO printing methods. </p>
 * <p> Company : C-Tec <p>
 *
 * @author plefebvre
 * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
 */

/*
 * History
 * ------------------------------------------------
 * Date			Name		BT		Description
 * 2010-09-24	plefebvre
 */
public class TOUtil {
	
	/**
	 * Returns all non-null TO data (one per line).
	 * @param to The TO(s) to print.
	 * @return
	 */
	public static String dump(TransferableObject... tos) {
		StringBuilder strBld = new StringBuilder( 250 );
		for (TransferableObject to: tos) {
			strBld.append( to.getClass().getCanonicalName() + " (UID=" +to.getUID()+")"+ " dump:\n" );
			strBld.append( "Column fields info ---------------\n" );
			ColumnInfo<?>[] columns = to.getTOStructure().getColumns();
			for( ColumnInfo<?> column: columns ) {
				Object data = to.get( column);
				if( null != data ) {
					strBld.append( String.format( "%30s: %s\n", column.toString(), StringUtil.toString(data) ) );
				}
			}
			strBld.append( "Additional fields info ---------------\n" );
			AdditionalFieldInfo<?>[] additionalDataFields = to.getTOStructure().getAdditionalFields();
			for( AdditionalFieldInfo<?> column: additionalDataFields ) {
				Object data = to.get( column);
				if( null != data ) {
					strBld.append( String.format( "%30s: %s\n", column.toString(), StringUtil.toString(data) ) );
				}
			}
		}
		return strBld.toString();
	}
	
	/**
	 * Returns a string representing the primary key values separated by ':'.
	 * @param to
	 * @return
	 */
	public static String getPrimaryKeyValues(TransferableObject to) {
		return getPrimaryKeyValues(to, ":");
	}
	
	/**
	 * Returns a string representing the primary key values separated by separator.
	 * @param to
	 * @param separator
	 * @return
	 */
	public static String getPrimaryKeyValues(TransferableObject to, String separator) {
		int primaryKeySize = to.getTOStructure().getPrimaryKeySize();
		StringBuilder sb = new StringBuilder(primaryKeySize * 10);
		
		ColumnInfo<?>[] columns = to.getTOStructure().getColumns();
		for (int i = 0; i < primaryKeySize; i++) {
			if (sb.length() != 0) {
				sb.append( separator );
			}
			
			sb.append(String.valueOf(to.get(columns[i])));
		}
		
		return sb.toString();
	}
	
	/**
	 * Returns a compact representation of this TO.
	 * Circular references with sub-TOs and sub-collections should be avoided.
	 * This is a convenient method invoking toStringCompact(true, true).
	 * @param to The TO to print.
	 * @return
	 */
	public static String toStringCompact(TransferableObject to) {
		return toStringCompact(to, true, true);
	}
	
	/**
	 * Returns a compact representation of this TO.
	 * Circular references with sub-TOs and sub-collections should be avoided.
	 * @param to The TO to print.
	 * @param printTOContent Whether sub-TOs are printed (otherwise it's just the class name).
	 * @param printCollectionContent Whether collections are recursively printed (otherwise just the size).
	 * @return
	 */
	public static String toStringCompact(TransferableObject to, boolean printTOContent, boolean printCollectionContent) {
		// We prepare the columns
		List<TransferableObjectInfo<?>> columns = new ArrayList<TransferableObjectInfo<?>>();
		columns.addAll(Arrays.asList(to.getTOStructure().getColumns()));
		columns.addAll(Arrays.asList(to.getTOStructure().getAdditionalFields()));
		
		StringBuilder sb = new StringBuilder(20 * columns.size());
		
		sb.append(to.getClass().getSimpleName());
		sb.append(" (uid=");
		sb.append(to.getUID());
		sb.append(")={");
		
		for(TransferableObjectInfo<?> column: columns) {
			Object data = to.get(column);
			if (null != data) {
				sb.append(column);
				sb.append("=\"");
				// We don't want to input stuff on several lines
				sb.append(getRecCompactDataString(data, printTOContent, printCollectionContent).replaceAll("\\r|\\n", ""));
				sb.append("\" ");
			}
		}
		
		return sb.toString().trim() + "}";
	}
	
	/**
	 * Prints the content representation of a specific TO data field.
	 * This method is called recursively with TOs and collections when requested.
	 * Circular references with sub-TOs and sub-collections should be avoided.
	 * @param data The TO field data to print.
	 * @param printTOContent Whether sub-TOs are printed (otherwise it's just the class name).
	 * @param printCollectionContent Whether collections are recursively printed (otherwise just the size).
	 * @return
	 */
	private static String getRecCompactDataString(Object data, boolean printTOContent, boolean printCollectionContent) {
		if (data == null) {
			// This is null
			return "null";
			
		} else if (data instanceof TransferableObject) {
			// This is a TO
			if (printTOContent) {
				// We recursively print this TO
				return TOUtil.toStringCompact((TransferableObject)data, printTOContent, printCollectionContent);
			} else {
				// We don't recursively print the TO, only the TO name
				return "(" + data.getClass().getSimpleName() + ")";
			}
			
		} else if (data.getClass().getComponentType() == null && !(data instanceof Iterable<?>)) {
			// This is not an array nor a collection
			return String.valueOf(data);
			
		} else {
			// This is either an array or a collection
			Iterable<?> iterable;
			if (data.getClass().getComponentType() != null) {
				iterable = Arrays.asList((Object[])data);
			} else {
				iterable = (Iterable<?>)data;
			}
			
			if (!printCollectionContent) {
				// We don't print the collection content, only the size
				Iterator<?> iterator = iterable.iterator();
				int counter = 0;
				while (iterator.hasNext()) {
					iterator.next();
					counter++;
				}
				return "[size=" + counter + "]";
				
			} else {
				// We print the collection content
				StringBuilder sb = new StringBuilder(10);
				sb.append('[');
				
				Iterator<?> iterator = iterable.iterator();
				while (iterator.hasNext()) {
					sb.append(getRecCompactDataString(iterator.next(), printTOContent, printCollectionContent));
					if (iterator.hasNext()) {
						sb.append(", ");
					}
				}
				
				sb.append(']');
				return sb.toString();
			}
		}
	}
	
	/**
	 * Invokes getCompactStringRepresentation for several TOs.
	 * @param multiLines Whether each TO should be printed on a different line.
	 * @param tos The TOs to print.
	 * @return
	 */
	public static String toStringCompact(boolean multiLines, TransferableObject... tos) {
		return toStringCompact(multiLines, true, true, tos);
	}
	
	/**
	 * Invokes getCompactStringRepresentation for several TOs.
	 * @param multiLines Whether each TO should be printed on a different line.
	 * @param printTOContent Whether sub-TOs are printed (otherwise it's just the class name).
	 * @param printCollectionContent Whether collections are recursively printed (otherwise just the size).
	 * @param tos The TOs to print.
	 * @return
	 */
	public static String toStringCompact(boolean multiLines, boolean printTOContent, boolean printCollectionContent, TransferableObject... tos) {
		return toStringCompact(Arrays.asList(tos), multiLines, printTOContent, printCollectionContent);
	}
	
	/**
	 * Invokes getCompactStringRepresentation for several TOs, one line per TO.
	 * @param tos The TOs to print.
	 * @return
	 */
	public static String toStringCompact(Iterable<? extends TransferableObject> tos) {
		return toStringCompact(tos, true);
	}
	
	/**
	 * Invokes getCompactStringRepresentation for several TOs.
	 * @param tos The TOs to print.
	 * @param multiLines Whether each TO should be printed on a different line.
	 * @return
	 */
	public static String toStringCompact(Iterable<? extends TransferableObject> tos, boolean multiLines) {
		return toStringCompact(tos, multiLines, true, true);
	}
	
	/**
	 * Invokes getCompactStringRepresentation for several TOs.
	 * @param tos The TOs to print.
	 * @param multiLines Whether each TO should be printed on a different line.
	 * @param printTOContent Whether sub-TOs are printed (otherwise it's just the class name).
	 * @param printCollectionContent Whether collections are recursively printed (otherwise just the size).
	 * @return
	 */
	public static String toStringCompact(Iterable<? extends TransferableObject> tos, boolean multiLines, boolean printTOContent, boolean printCollectionContent) {
		if (tos == null) {
			return "null";
		} else {
			// We print the collection content
			StringBuilder sb = new StringBuilder(100);
			sb.append('[');
			
			Iterator<? extends TransferableObject> iterator = tos.iterator();
			while (iterator.hasNext()) {
				TransferableObject to = iterator.next();
				if (to == null) {
					sb.append("null");
				} else {
					sb.append(TOUtil.toStringCompact(to, printTOContent, printCollectionContent));
				}
				
				if (iterator.hasNext()) {
					if (multiLines) {
						sb.append(",\n");
					} else {
						sb.append(", ");
					}
				}
			}
			
			sb.append(']');
			return sb.toString();
		}
	}
	
	/**
	 * Checks the equality of the lists by calling deepEquals() on TO
	 * instead of equals.
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static <T extends TransferableObject> boolean collectionDeepEquals(Collection<? extends T> c1, Collection<? extends T> c2){
		
		//If both are null, they're equal
		if(c1 == null && c2 == null){
			return true;
		}
		
		//If only one is null, they're not equal
		if(c1 == null || c2 == null){
			return false;
		}
		
		//Now for the main course...
		if(c1.size() != c2.size()){
			return false;
		}
		
		Iterator<? extends TransferableObject> it1 = c1.iterator();
		Iterator<? extends TransferableObject> it2 = c2.iterator();
		while (it1.hasNext()) {
			if (!it1.next().deepEquals(it2.next())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Upserts a TO into a collection.
	 * <p>Looks for a TO inside the collection, which matches the provided TO using shallowEquals,
	 * then updates it if both TOs don't deepEqual. If no equals match is found in the collection, adds the TO.
	 * <p>In case of an update, the already existing TO instance will be kept and its data will be updated -
	 * it will not be replaced by the parameter. This all means that the reference of the provided TO
	 * will only wind up in the collection if it gets inserted into it.
	 * @param collection
	 * @param to
	 * @return True if the TO was added or updated, false if a deepEquals match was found and no change occurred.
	 * @throws NullPointerException if either parameter is null.
	 */
	public static <T extends TransferableObject, S extends T> boolean collectionUpsert(Collection<T> collection, S to)
	throws NullPointerException{
		if(to == null || collection == null){
			throw new NullPointerException();
		}
		
		for(T currentTO : collection){
			//If we have a TO whose PK matches the parameter's PK
			if (to.shallowEquals(currentTO)){
				
				//If there's any difference between the TOs
				if (!to.deepEquals(currentTO)){
					
					//Update the TO
					currentTO.synch(to);
					return true;
				}//If the TOs are identical
				else {
					//No update is being done
					return false;
				}
			}
		}
		
		//If we didn't find a shallow match for the TO, add it
		collection.add(to);
		return true;
	}
	
	/**
	 * Checks for equality of 2 TOs by calling deepEquals, and handles null properly (nulls are equal)
	 * @param to1
	 * @param to2
	 * @return
	 */
	public static <T extends TransferableObject> boolean TODeepEquals(T to1, T to2){
		return to1 == null ? to2 == null : to1.deepEquals(to2);
	}
	
	/**
	 * Creates a shallow (key only) comparator based on the provided to's type.
	 * @param to
	 * @return
	 */
	public static <T extends TransferableObject> Comparator<T> createShallowTOComparator(T to,
			NULL_COMPARATOR_ENUM nullComparatorEnum) {
		
		//Load TO's structure
		TransferableObjectInfo<?>[] colObjList = to.getTOStructure().getColumns();
		
		//Start a list of fields which will receive all key fields
		TransferableObjectInfo<?>[] keyList = new TransferableObjectInfo<?>[to.getTOStructure().getPrimaryKeySize()];
		
		for(int i = 0; i < to.getTOStructure().getPrimaryKeySize() ; i++){
			keyList[i] = colObjList[i];
		}
		
		//Use the list to create a comparator
		return createTOComparator(nullComparatorEnum, keyList);
	}
	
	/**
	 * Creates a TO comparator based on the specified fields, in the order supplied.
	 * This means if the values of two TOs are the same for the first field, the second
	 * field will be used for the comparison.
	 * 
	 * This method, following the standards requested by the Comparable interface, will
	 * throw a NullPointerException if compares a null element. For a different behavior
	 * take a look at {@link #createTOComparator(NULL_COMPARATOR_ENUM, TransferableObjectInfo...)}.
	 * @param <T>
	 * @param fields
	 * @return
	 */
	public static <T extends TransferableObject> Comparator<T> createTOComparator(final TransferableObjectInfo<?>... fields) {
		return createTOComparator(NULL_COMPARATOR_ENUM.NULL_EXCEPTION, fields);
	}
	
	/**
	 * Creates a TO comparator based on the specified fields, in the order supplied.
	 * This means if the values of two TOs are the same for the first field, the second
	 * field will be used for the comparison.
	 * The behavior of null elements will change depending of comparatorEnum.
	 * @param <T>
	 * @param comparatorEnum
	 * @param fields
	 * @return
	 */
	public static <T extends TransferableObject> Comparator<T> createTOComparator(final NULL_COMPARATOR_ENUM comparatorEnum, final TransferableObjectInfo<?>... fields) {
		if (comparatorEnum == null || fields == null || fields.length == 0) {
			throw new NullPointerException();
		}
		
		return new Comparator<T>() {
			@Override
			public int compare(T to1, T to2) {
				
				for (TransferableObjectInfo<?> field: fields) {
					Comparable<?> o1 = (Comparable<?>) to1.get(field);
					Comparable<?> o2 = (Comparable<?>) to2.get(field);
					
					int c = ObjectUtil.compare(o1, o2, comparatorEnum);
					if (c != 0) {
						return c;
					}
				}
				
				return 0;
			}
		};
	}
	
	/**
	 * Creates a TO based on the provided class and primary key values.
	 * @param clazz
	 * @param keys
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends TransferableObject> T createInstance(Class<T> clazz, Object... keys) {
		T to;
		try {
			to = clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
		ColumnInfo[] columns = to.getTOStructure().getColumns();
		int primaryKeySize = to.getTOStructure().getPrimaryKeySize();
		
		if (keys == null || keys.length != primaryKeySize) {
			throw new IllegalStateException();
		}
		
		for (int i = 0; i < primaryKeySize; i++) {
			to.set(columns[i], keys[i]);
		}
		
		return to;
	}
	
	/**
	 * Creates a TO based on the provided class and containing the specified values.
	 * @param clazz
	 * @param columnValues
	 * @return
	 */
	public static <T extends TransferableObject, V> T createInstance(Class<T> clazz, TOColumnPair<V>... columnPairs) {
		T to;
		try {
			to = clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
		for (Pair<TransferableObjectInfo<V>, V> columnPair: columnPairs) {
			to.set(columnPair.getFirst(), columnPair.getSecond());
		}
		
		return to;
	}

	public static String getPropertyName(TransferableObjectInfo<?> column) {
		StringBuilder sb = new StringBuilder();
		for (String part : column.toString().split("_")) {
			part = part.toLowerCase();
			if (part.equals("id")) {
				sb.append("ID");
			} else if (sb.length() > 0) {
				sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
			} else {
				sb.append(part);
			}
		}
		return sb.toString ();
	}
	
	/**
	 * Returns the TO structure base on a TO class.
	 * @param clazz
	 * @return
	 */
	public static TransferableObjectStructureIF getTOStructure(Class<? extends TransferableObject> clazz) {
		// Method 1: Using reflection, access the private static field of the TO structure.
		// Problem: With the old TOs it will not work since the structure is not in a static field.
		//          It might also not be the most efficient way to do this.
//		Exception lastException = null;
//		for (Field field: clazz.getDeclaredFields()) {
//			if (Modifier.isStatic(field.getModifiers())) {
//				try {
//					field.setAccessible(true);
//					Object o = field.get(null);
//					if (o instanceof TransferableObjectStructureIF) {
//						return (TransferableObjectStructureIF) o;
//					}
//				} catch (Exception e) {
//					lastException = e;
//				}
//			}
//		}
//		throw new IllegalStateException(lastException);
		
		// Method 2: Create a new TO instance and return the structure.
		// Problem: We assume there is an empty constructor (this assumption is often made for TOs anyway).
		try {
			return clazz.newInstance().getTOStructure();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Performs a shallow removeAll. Removes from the original Collection all TOs whose key matches
	 * a TO in the toRemove Collection.
	 * @param <T> Type of the TO.
	 * @param original Collection from which to remove TOs
	 * @param toRemove Collection that contains the TOs to remove from the original Collection
	 * @return true if the original collection changed as a result of the call
	 */
	public static <T extends TransferableObject> boolean shallowRemoveAll(Collection<T> original,
			Collection<T> toRemove){
		
		/* We need to get a real TO to create the comparator. Since we know that this method has nothing
		 * to do if toRemove is empty, we'll get it from that one. */
		if (toRemove.isEmpty()){
			return false;
		}
		Comparator<T> comparator = createShallowTOComparator(toRemove.iterator().next(), NULL_COMPARATOR_ENUM.NULL_FIRST);
		
		return CollectionUtil.removeAll(original, toRemove, comparator);
	}
	
	/**
	 * Performs a shallow retainAll. Removes from the original Collection all TOs whose key doesn't match
	 * a TO in the toRemove Collection.
	 * @param <T> Type of the TO.
	 * @param original Collection from which to remove TOs
	 * @param toRemove Collection that contains the TOs to retain in the original Collection
	 * @return true if the original collection changed as a result of the call
	 */
	public static <T extends TransferableObject> boolean shallowRetainAll(Collection<T> original,
			Collection<T> toRemove){
		
		/* We need to get a real TO to create the comparator. Since we know that this method simply clears
		 * the original list if toRemove is empty, we'll get it from that one. */
		if (toRemove.isEmpty()){
			original.clear();
			return true;
		}
		Comparator<T> comparator = createShallowTOComparator(toRemove.iterator().next(), NULL_COMPARATOR_ENUM.NULL_FIRST);
		
		return CollectionUtil.retainAll(original, toRemove, comparator);
	}
	
	/**
	 * Performs a shallow remove. Removes from the original Collection the toRemove TO, trying to find a match
	 * by key.
	 * @param <T> Type of the TO.
	 * @param original Collection from which to remove the TO
	 * @param toRemove TO to remove
	 * @return true if the collection changed as a result of the call
	 */
	public static <T extends TransferableObject> boolean shallowRemove(Collection<T> original,
			T toRemove){
		Comparator<T> comparator = createShallowTOComparator(toRemove, NULL_COMPARATOR_ENUM.NULL_FIRST);
		return CollectionUtil.remove(original, toRemove, comparator);
	}
	
	/**
	 * Performs a shallow contains. Looks in the collection for a TO matching the provided TO
	 * by key.
	 * @param <T> Type of the TO.
	 * @param collection Collection in which to look for the TO
	 * @param toFind TO to look for
	 * @return true if the collection contains the TO.
	 */
	public static <T extends TransferableObject> boolean shallowContains(Collection<T> collection,
			T toFind){
		Comparator<T> comparator = createShallowTOComparator(toFind, NULL_COMPARATOR_ENUM.NULL_FIRST);
		return CollectionUtil.contains(collection, toFind, comparator);
	}
	
	/**
	 * Creates and returns a set of all values for the provided column found within the provided collection of TOs.
	 * Null values will be ignored and not inserted into the set.
	 * @param <T> TO's type.
	 * @param <V> Value's type.
	 * @param collection List of TOs to search in.
	 * @param column Definition of the column to look at.
	 * @return
	 */
	public static <T extends TransferableObject, V> Set<V> extractValueSetFromTOList (Collection<T> collection,
			ColumnInfo<V> column){
		Set<V> set = new HashSet<V>();
		
		for(T currentTO : collection){
			V value = currentTO.get(column);
			if (value != null){
				set.add(value);
			}
		}
		
		return set;
	}
}
