package com.philippelangevin.sdk.dataStructure;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.philippelangevin.sdk.util.ObservableList;

public class CopyUtil {
	/**
	 * Copies the element using the appropriate method:
	 *  - for a Collection recursive calls will be invoked for each value
	 *  - for a Map recursive calls will be invoked for each value (keys will use the same instances!)
	 *  - for an array recursive calls will be invoked for each value
	 *  - for an Object the copy constructor will be invoked, if it fails clone will be invoked,
	 *    if it also fails the same instance will be used
	 * @param <E> Any type of object that can be copied
	 * @param element Any object to copy
	 * @return The copied object (could be the same instance)
	 * @throws UnsupportedOperationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <E> E copy(E element) throws UnsupportedOperationException {
		try {
			if (element == null) {
				return null;
				
			} else if (element instanceof Collection) {
				// This is a Collection
				Collection<?> oldCollection = (Collection) element;
				Collection newCollection;
				
				if (element.getClass().getCanonicalName().equals("java.util.Arrays.ArrayList")) {
					// This is a patch to copy lists created using Arrays.asList()
					newCollection = new ArrayList();
				} else if (element instanceof ObservableList) {
					// Ugly patch for ObservableList, because of the listeningContent variable
					newCollection = new ObservableList<E>(((ObservableList)element).isListeningContent());
				} else {
					// We create the same collection
					newCollection = (Collection) element.getClass().getDeclaredConstructor().newInstance();
				}
				
				for (Object currElement: oldCollection) {
					newCollection.add(copy(currElement));
				}
				return (E) newCollection;
				
			} else if (element instanceof Map) {
				// This is a Map
				Map<?,?> oldMap = (Map) element;
				Map newMap = (Map) element.getClass().getDeclaredConstructor().newInstance();
				
				for (Entry<?,?> entry: oldMap.entrySet()){
					// Note that we don't copy the key, we use the same instance
					newMap.put(entry.getKey(), copy(entry.getValue()));
				}
				
				return (E) newMap;
				
			} else if (element.getClass().isArray()) {
				// This is an array
				Object[] newArray = (Object[]) Array.newInstance(element.getClass().getComponentType(), ((Object[])element).length);
				for (int i = 0; i < newArray.length; i++) {
					newArray[i] = copy(((Object[])element)[i]);
				}
				return (E) newArray;
				
			} else {
				// This is a normal value
				try {
					// We try the copy constructor first
					if (element.getClass().isMemberClass() && !Modifier.isStatic(element.getClass().getModifiers())) {
						// This is a really special case: an inner class that isn't static.
						// We will need this element's parent to access its constructor, since it depends on it.
						// this$0 is a special field that refers to the parent instance
						Field parentField = element.getClass().getDeclaredField("this$0");
						parentField.setAccessible(true);
						Object parent = parentField.get(element);
						// The constructor will have both the parent and the element as params
						return (E) element.getClass().getDeclaredConstructor(parent.getClass(), element.getClass()).newInstance(parent, element);
					} else {
						// This is the normal case of a copy constructor
						return (E) element.getClass().getDeclaredConstructor(element.getClass()).newInstance(element);
					}
				} catch (NoSuchMethodException copyConstructorException) {
					// Copy constructor failed, we try to clone
					if (element instanceof Cloneable) {
						try {
							return (E) element.getClass().getDeclaredMethod("clone").invoke(element);
						} catch (NoSuchMethodException cloneException) {
							//fall-through...
						} catch (InvocationTargetException cloneException) {
							if (cloneException.getCause() instanceof CloneNotSupportedException) {
								//shouldn't happen since it implements Cloneable
								//we dropped
							} else if (cloneException.getCause() instanceof Exception) {
								//unknown exception happened in clone()
								throw (Exception)cloneException.getCause();
							} else {
								//damn!.. that's an error!
								throw (Error)cloneException.getCause();
							}
						}
					}
					// Copy constructor and clone failed, we return the same instance
					return element;
				}
			}
		} catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}
	
	/**
	 * Same as {@link CopyUtil#copy(Object)}, but doesn't try any exception (returns null).
	 * @param element
	 * @param <E>
	 * @return
	 */
	public static <E> E copyQuiet(E element) {
		try {
			return copy(element);
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
