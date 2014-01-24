/**
 * 
 */
package com.philippelangevin.sdk.dataStructure;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author pcharette
 *
 */
public final class CollectionUtil {

	/**
	 * Check if all the objects in both c1 and c2 are equals.
	 * @param c1 The first collection.
	 * @param c2 The second collection.
	 * @return <code>true</code> if all the elements in both collections are equals.
	 */
	public static boolean collectionsEqual(Collection<?> c1, Collection<?> c2) {
		if (c1.size() != c2.size()) {
			return false;
		}
		for (Object o : c1) {
			if (!c2.contains(o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if all the elements in both list are equals at the same position. This method may returns
	 * <code>false</code> even if {@link #collectionsEqual(Collection, Collection)} returns <code>true</code>
	 * but the opposite if not true.
	 * @param l1 The first list.
	 * @param l2 The second list.
	 * @return <code>true</code> if both lists are in the same order and if all their elements are equals.
	 */
	public static boolean listsEqual(List<?> l1, List<?> l2) {
		if (l1.size() != l2.size()) {
			return false;
		}
		for (int i = 0; i < l1.size(); ++i) {
			if (!ObjectUtil.equals(l1.get(i), l2.get(i))) {
				return false;
			}
		}
		return true;
	}

	private CollectionUtil() {
	}
	
	/**
	 * Returns whether an array contains the specified element.
	 * @param <E>
	 * @param array
	 * @param element
	 * @return
	 */
	public static <E> boolean arrayContains(E[] array, Object element) {
		if (array == null) {
			return false;
		} else {
			for (E currElement : array) {
				if (ObjectUtil.equals(currElement, element)) {
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * Returns whether the provided object is found in the provided collection according to the
	 * provided comparator.
	 * @param <V> Type of the object to look for
	 * @param coll Collection in which to look for the object
	 * @param obj Object to look for
	 * @param comp Comparator. Must at least be able to tell whether objects of V type are equal or different.
	 * Ability to sort V types not required.
	 * @return
	 */
	public static <V> boolean contains(Collection<? extends V> coll, V obj, Comparator<V> comp) {
		for (V item : coll) {
			if (comp.compare(obj, item) == 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes the provided object from the provided collection, if it is found in it according to the
	 * provided comparator.
	 * @param <V> Type of the object to remove
	 * @param coll Collection from which to remove the object
	 * @param obj Object to remove
	 * @param comp Comparator. Must at least be able to tell whether objects of V type are equal or different.
	 * Ability to sort V types not required.
	 * @return true if the collection changed as a result of the call
	 */
	public static <V> boolean remove(Collection<? extends V> coll, V obj, Comparator<V> comp) {
		for (Iterator<? extends V> iterator = coll.iterator(); iterator.hasNext(); ) {
			
			V item = iterator.next();
			if (comp.compare(obj, item) == 0) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes from the original collection any object that is found in the toRemove collection. Equality is determined
	 * according to the provided comparator.
	 * @param <V> Type of the objects contained in the collections
	 * @param original Collection from which objects should be removed
	 * @param toRemove Collection which contains all items that should be removed from original.
	 * @param comp Comparator. Must at least be able to tell whether objects of V type are equal or different.
	 * Ability to sort V types not required.
	 * @true true if the original collection changed as a result of the call
	 */
	public static <V> boolean removeAll(Collection<? extends V> original, Collection<? extends V> toRemove, Comparator<V> comp) {
		boolean changed = false;
		for (Iterator<? extends V> it = original.iterator(); it.hasNext();) {
			if (contains(toRemove, it.next(), comp)) {
				it.remove();
				changed = true;
			}
		}
		
		return changed;
	}
	
	/**
	 * Retains in the original collection only objects that are found in the toRemove collection.
	 * In other words, removes all elements that are not found in the toRemove collection.
	 * Equality is determined according to the provided comparator.
	 * @param <V> Type of the objects contained in the collections
	 * @param original Collection from which objects should be removed
	 * @param toRemove Collection which contains all items that should be retained in original.
	 * @param comp Comparator. Must at least be able to tell whether objects of V type are equal or different.
	 * Ability to sort V types not required.
	 * @true true if the original collection changed as a result of the call
	 */
	public static <V> boolean retainAll(Collection<? extends V> original, Collection<? extends V> toRemove, Comparator<V> comp) {
		boolean changed = false;
		for (Iterator<? extends V> it = original.iterator(); it.hasNext();) {
			if (!contains(toRemove, it.next(), comp)) {
				it.remove();
				changed = true;
			}
		}
		
		return changed;
	}
	
	/**
	 * Same utility as {@link Arrays#asList(Object...)}.
	 * @param <V>
	 * @param values The list of values, those must be consequent with {@link Object#hashCode()}.
	 * @return The new {@link HashSet}.
	 */
	public static <V> HashSet<V> asHashSet(V ... values) {
		return new HashSet<V>(Arrays.asList(values));
	}
}
