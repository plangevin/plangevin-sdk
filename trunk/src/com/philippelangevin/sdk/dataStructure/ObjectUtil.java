/**
 * 
 */
package com.philippelangevin.sdk.dataStructure;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pcharette
 * @date 2010-10-21
 */
public final class ObjectUtil {
	/**
	 * Defines the behavior of null elements when comparing TO fields using createTOComparator().
	 * NULL_FIRST: Places null elements first when sorting.
	 * NULL_LAST: Places null elements last when sorting.
	 * NULL_EQUAL: Considers null elements equal for a field (will continue to the next field if any).
	 * NULL_EXCEPTION: Throws NullPointerException for null elements (default behavior).
	 */
	public static enum NULL_COMPARATOR_ENUM {
		NULL_FIRST, NULL_LAST, NULL_EQUAL, NULL_EXCEPTION, ;
	}

	/**
	 * Comparator using {@link ObjectUtil#compare(Object, Object, NULL_COMPARATOR_ENUM)}. The advantage of this class is
	 * that it manage <code>null</code>.
	 * @author pcharette
	 * @date 2011-02-16
	 * @param <T>
	 */
	public static class ObjectComparator implements Comparator<Object> {

		private NULL_COMPARATOR_ENUM nullComparatorEnum;

		public ObjectComparator(NULL_COMPARATOR_ENUM nullComparatorEnum) {
			this.nullComparatorEnum = nullComparatorEnum;
		}

		public ObjectComparator() {
			this(NULL_COMPARATOR_ENUM.NULL_FIRST);
		}

		@Override
		public int compare(Object o1, Object o2) {
			return ObjectUtil.compare(o1, o2, nullComparatorEnum);
		};

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ObjectComparator) {
				return nullComparatorEnum == ((ObjectComparator) obj).nullComparatorEnum;
			}
			return false;
		}
	}

	/**
	 * Safe equals which handles <code>null</code>. Two <code>null</code> objects are equals.
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean equals(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
		// plefebvre 2011-04-13: Commented code below, TOs should use a deep equal by default.
//		if (o1 == null) {
//			return o2 == null;
//		} else if (o2 == null) {
//			return false;
//		} else if (o1 instanceof TransferableObject) {
//			if (!(o2 instanceof TransferableObject)) {
//				return false;
//			} else {
//				return ((TransferableObject)o1).deepEquals(o2);
//			}
//		} else if (o1 instanceof Entry) {
//			if (!(o2 instanceof Entry)) {
//				return false;
//			} else {
//				Entry<?,?> e1 = (Entry<?,?>)o1;
//				Entry<?,?> e2 = (Entry<?,?>)o2;
//				return ObjectUtil.equals(e1.getKey(), e2.getKey()) && ObjectUtil.equals(e1.getValue(), e2.getValue());
//			}
//		} else if (o1 instanceof Map) {
//			if (!(o2 instanceof Map)) {
//				return false;
//			} else {
//				Map<?,?> m1 = (Map<?,?>)o1;
//				Map<?,?> m2 = (Map<?,?>)o2;
//				return ObjectUtil.equals(m1.entrySet(), m2.entrySet());
//			}
//		} else if (o1 instanceof Iterable) {
//			if (!(o2 instanceof Iterable)) {
//				return false;
//			} else {
//				if (o1 instanceof List && !(o2 instanceof List) ||
//						o1 instanceof Set && !(o2 instanceof Set) ||
//						o1 instanceof Queue && !(o2 instanceof Queue)) {
//					return false;
//				} else {
//					Iterator<?> i1 = ((Iterable<?>)o1).iterator();
//					Iterator<?> i2 = ((Iterable<?>)o2).iterator();
//					while (i1.hasNext()) {
//						if (!i2.hasNext() || !ObjectUtil.equals(i1.next(), i2.next())) {
//							return false;
//						}
//					}
//					return !i2.hasNext();
//				}
//			}
//		} else if (o1.getClass().isArray()) {
//			if (!o2.getClass().isArray()) {
//				return false;
//			} else {
//				Object[] a1 = (Object[])o1;
//				Object[] a2 = (Object[])o2;
//				if (a1.length != a2.length) {
//					return false;
//				} else {
//					for (int i = 0; i < a1.length; i++) {
//						if (!ObjectUtil.equals(a1[i], a2[i])) {
//							return false;
//						}
//					}
//					return true;
//				}
//			}
//		} else {
//			return o1.equals(o2);
//		}
	}

	/**
	 * Safe equals which handles <code>null</code>, but considers two <code>null</code> objects to not be equal.
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean equalsNotNull(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return false;
		}
		return equals(o1, o2);
	}

	/**
	 * Compares two comparable objects, null is considered smaller than any non-null value.
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static int compare(Object o1, Object o2) {
		return compare(o1, o2, NULL_COMPARATOR_ENUM.NULL_FIRST);
	}

	/**
	 * Compares two comparable objects, the behaviour of null is defined by the enum.
	 * @param o1
	 * @param o2
	 * @param comparatorEnum
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int compare(Object o1, Object o2, NULL_COMPARATOR_ENUM comparatorEnum) {
		if (o1 == null) {
			// o1 is null, o2 might be null
			switch (comparatorEnum) {
			case NULL_EXCEPTION:
				throw new NullPointerException();
			case NULL_EQUAL:
				return 0;
			case NULL_FIRST:
				if (o2 == null) {
					return 0;
				} else {
					return -1;
				}
			case NULL_LAST:
				if (o2 == null) {
					return 0;
				} else {
					return 1;
				}
			default:
				throw new UnsupportedOperationException();
			}
		} else if (o2 == null) {
			// o1 is not null, o2 is null
			switch (comparatorEnum) {
			case NULL_EXCEPTION:
				throw new NullPointerException();
			case NULL_EQUAL:
				return 0;
			case NULL_FIRST:
				return 1;
			case NULL_LAST:
				return -1;
			default:
				throw new UnsupportedOperationException();
			}
		} else {
			return ((Comparable) o1).compareTo(o2);
		}
	}

	private ObjectUtil() {
	}

	/**
	 * Invokes the method "clone" on the object provided, and returns it casted.
	 * A usage example would be if you have an object of type Map<Integer, String>,
	 * and would like to clone it regardless whether it is a HashMap, TreeMap, etc.
	 * @param <T>
	 * @param o The object to clone
	 * @return
	 * @throws IllegalArgumentException If clone was not supported by this object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invokeClone(T o) {
		if (o == null) {
			return null;
		} else {
			try {
				return (T) o.getClass().getMethod("clone").invoke(o);
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException();
			}
		}
	}

	/**
	 * Listener used for the method {@link ObjectUtil#extractSingletonsAsList(Class, Class, Class, SingletonFoundListener)}.
	 */
	public static interface SingletonFoundListener<T> {

		/**
		 * This method is called by {@link ObjectUtil#extractSingletonsAsList(Class, Class, Class, SingletonFoundListener)}
		 * to filter the returned list. Any exception thrown by this method will be printed to the console but will not be
		 * propagated and will do the same as if this method returns <code>false</code>.
		 * @param containerClazz The current class of the field.
		 * @param name The field name of singleton.
		 * @param singleton The field value before it is added to the list.
		 * @return <code>true</code> to add the field to the list or <code>false</code> to skip it.
		 */
		public boolean singletonFound(Class<?> containerClazz, String name, T singleton);
	}

	private static final int PUBLIC_STATIC_FINAL_MODIFIERS = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

	/**
	 * Extract the singleton fields from this class and all its super classes.
	 * @param <T> The type of the container and the singletons items.
	 * @param itemClazz The class of the container and the singletons items.
	 * @return The list of singletons in the itemClazz and its super classes. This list is modifiable and can
	 * safely be used.
	 */
	public static <T> ArrayList<? extends T> extractSingletonsAsList(Class<T> itemClazz) {
		return extractSingletonsAsList(itemClazz, itemClazz, true);
	}

	/**
	 * Extract the singleton fields from this class and all its super classes.
	 * @param <T> The type of the container and the singletons items.
	 * @param clazz The class of the container and the singletons items.
	 * @return The array of singletons in the itemClazz and its super classes.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] extractSingletons(Class<T> clazz) {
		ArrayList<? extends T> list = extractSingletonsAsList(clazz);
		return list.toArray((T[]) Array.newInstance(clazz, list.size()));
	}

	/**
	 * Extract the singleton fields from a container class and all its ancestor until stopAtClazz. This method also
	 * call the listener if not <code>null</code> to filter the array.
	 * @param <T> The type of the singletons items.
	 * @param <U> The type of the container.
	 * @param containerClazz The class of the container.
	 * @param itemClazz The class of the singletons items.
	 * @param stopAtClazz The class to stop the search at. This must be super class of containerClazz if you don't
	 * want an empty list to be returned.
	 * @param listener The listener used to filter the list.
	 * @return The array of singletons in the itemClazz and its super classes. This array is modifiable and can
	 * safely be used.
	 */
	@SuppressWarnings("unchecked")
	public static <T, U> T[] extractSingletons(Class<U> containerClazz, Class<T> itemClazz, Class<? super U> stopAtClazz, SingletonFoundListener<T> listener) {
		ArrayList<? extends T> list = extractSingletonsAsList(containerClazz, itemClazz, stopAtClazz, listener);
		return list.toArray((T[]) Array.newInstance(itemClazz, list.size()));
	}

	/**
	 * Extract the singleton fields from a container class and all its ancestor until stopAtClazz. This method also
	 * call the listener if not <code>null</code> to filter the list.
	 * @param <T> The type of the singletons items.
	 * @param <U> The type of the container.
	 * @param containerClazz The class of the container.
	 * @param itemClazz The class of the singletons items.
	 * @param stopAtClazz The class to stop the search at. This must be super class of containerClazz if you don't
	 * want an empty list to be returned.
	 * @param listener The listener used to filter the list.
	 * @return The list of singletons in the itemClazz and its super classes. This list is modifiable and can
	 * safely be used.
	 */
	public static <T, U> ArrayList<? extends T> extractSingletonsAsList(Class<U> containerClazz, Class<T> itemClazz, Class<? super U> stopAtClazz, SingletonFoundListener<T> listener) {
		//list the class to check for type
		ArrayList<Class<? super U>> classes = new ArrayList<Class<? super U>>();
		for (Class<? super U> cls = containerClazz; cls != stopAtClazz; cls = cls.getSuperclass()) {
			classes.add(cls);
		}
		//reverse the order so the last in hierarchy will be at the end
		Collections.reverse(classes);
		//parse the hierarchy
		ArrayList<T> list = new ArrayList<T>();
		for (Class<? super U> cls : classes) {
			//parse the fields and keep only the public static final fields
			for (Field field : cls.getDeclaredFields()) {
				if (itemClazz.isAssignableFrom(field.getType()) && (field.getModifiers() & PUBLIC_STATIC_FINAL_MODIFIERS) == PUBLIC_STATIC_FINAL_MODIFIERS) {
					try {
						//no choice in case the container class is not visible
						field.setAccessible(true);
						T o = itemClazz.cast(field.get(null));
						if (o != null) {
							boolean add = true;
							if (listener != null) {
								try {
									add = listener.singletonFound(cls, field.getName(), o);
								} catch (Exception e) {
									e.printStackTrace();
									add = false;
								}
							}
							if (add) {
								list.add(o);
							}
						}
					} catch (IllegalArgumentException e) {
						//shouldn't happen because the field is static
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						//shouldn't happen because the field is public
						e.printStackTrace();
					}
				}
			}
		}
		return list;
	}

	/**
	 * Extract the singleton fields from a container class. This method can also check in the
	 * super classes of the container.
	 * @param <T> The type of the singletons items.
	 * @param <U> The type of the container.
	 * @param containerClazz The class of the container.
	 * @param itemClazz The class of the singletons items.
	 * @param recursiveSearch If <code>true</code> the container and all its super classes are
	 * searched for the singletons.
	 * @return The list of singletons in the itemClazz and its super classes. This list is modifiable and can
	 * safely be used.
	 */
	public static <T, U> ArrayList<? extends T> extractSingletonsAsList(Class<U> containerClazz, Class<T> itemClazz, boolean recursiveSearch) {
		if (recursiveSearch) {
			return extractSingletonsAsList(containerClazz, itemClazz, Object.class, null);
		} else {
			return extractSingletonsAsList(containerClazz, itemClazz, containerClazz.getSuperclass(), null);
		}
	}

	/**
	 * Extract the singleton fields from a container class. This method can also check in the
	 * super classes of the container.
	 * @param <T> The type of the singletons items.
	 * @param <U> The type of the container.
	 * @param containerClazz The class of the container.
	 * @param itemClazz The class of the singletons items.
	 * @param recursiveSearch If <code>true</code> the container and all its super classes are
	 * searched for the singletons.
	 * @return The array of singletons in the itemClazz and its super classes.
	 */
	@SuppressWarnings("unchecked")
	public static <T, U> T[] extractSingletons(Class<U> containerClazz, Class<T> itemClazz, boolean recursiveSearch) {
		ArrayList<? extends T> list = extractSingletonsAsList(containerClazz, itemClazz, recursiveSearch);
		return list.toArray((T[]) Array.newInstance(itemClazz, list.size()));
	}

	/**
	 * Utility method used to create an hashcode on the array of object.
	 * @param objs The array of objects to create an hashcode for.
	 * @return The computed hashcode of the array.
	 */
	public static int hashCode(Object... objs) {
		int seed = HashCodeUtil.SEED;
		for (Object o : objs) {
			seed = HashCodeUtil.hash(seed, o);
		}
		return seed;
	}

	/**
	 * This method tries to add a {@link PropertyChangeListener} to the bean. This method use reflection to call
	 * a method called addPropertyChangeListener that takes a {@link PropertyChangeListener} as a sole parameter.
	 * This method returns a boolean to inform if the call worked. You can safely use this method on a non-bean object.
	 * @param bean The bean to add the listener to.
	 * @param listener The listener to add to the bean.
	 * @return <code>true</code> if the call to addPropertyChangeListener worked.
	 * @throws IllegalArgumentException If bean or listener are <code>null</code>.
	 */
	public static boolean addPropertyChangeListener(Object bean, PropertyChangeListener listener) throws IllegalArgumentException {
		if (bean == null) {
			throw new IllegalArgumentException("bean must not be null");
		}
		if (listener == null) {
			throw new IllegalArgumentException("listener must not be null");
		}

		try {
			Method add = bean.getClass().getMethod("addPropertyChangeListener", PropertyChangeListener.class);
			add.invoke(bean, listener);
			return true;
		} catch (NoSuchMethodException e) {
			//ignore
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			//log but ignore
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			//log but ignore
		}
		return false;
	}

	/**
	 * This method tries to remove a {@link PropertyChangeListener} of the bean. This method use reflection to call
	 * a method called removePropertyChangeListener that takes a {@link PropertyChangeListener} as a sole parameter.
	 * This method returns a boolean to inform if the call worked. You can safely use this method on a non-bean object.
	 * @param bean The bean to remove the listener from.
	 * @param listener The listener to remove from the bean.
	 * @return <code>true</code> if the call to removePropertyChangeListener worked.
	 * @throws IllegalArgumentException If bean or listener are <code>null</code>.
	 */
	public static boolean removePropertyChangeListener(Object bean, PropertyChangeListener listener) {
		if (bean == null) {
			throw new IllegalArgumentException("bean must not be null");
		}
		if (listener == null) {
			throw new IllegalArgumentException("listener must not be null");
		}

		try {
			Method add = bean.getClass().getMethod("removePropertyChangeListener", PropertyChangeListener.class);
			add.invoke(bean, listener);
			return true;
		} catch (NoSuchMethodException e) {
			//ignore
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			//log but ignore
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			//log but ignore
		}
		return false;
	}
	
	/**
	 * This method map a list of bean by one of its property. This can be useful if you want to group a list by a certain property. This
	 * method also make a type check on the property type. To get the method to use to retrieve the property, this method use the first bean
	 * of the list. If the list is empty, the method returns an empty map. This method only throws {@link RuntimeException}, if {@link #mapBeans(List, String, Class)}
	 * throw a non-runtime exception, the exception is wrapped and rethrown.
	 * @param <B> The bean type.
	 * @param <P> The property type.
	 * @param beans The list of beans to group.
	 * @param propertyName The name of the property to use to group.
	 * @param propertyClass The type of the property to use to group.
	 * @return The mapped beans, never <code>null</code>.
	 * @throws RuntimeException Any exception thrown by {@link #mapBeans(List, String, Class)}.
	 */
	public static <B,P> Map<P,List<B>> mapBeansSilent(Collection<B> beans, String propertyName, Class<P> propertyClass) throws RuntimeException {
		try {
			return mapBeans(beans, propertyName, propertyClass);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * This method map a list of bean by one of its property. This can be useful if you want to group a list by a certain property. This
	 * method also make a type check on the property type. To get the method to use to retrieve the property, this method use the first bean
	 * of the list. If the list is empty, the method returns an empty map.
	 * @param <B> The bean type.
	 * @param <P> The property type.
	 * @param beans The list of beans to group.
	 * @param propertyName The name of the property to use to group.
	 * @param propertyClass The type of the property to use to group.
	 * @return The mapped beans, never <code>null</code>.
	 * @throws IllegalArgumentException If no method can be used to retrieve the property on the first bean of the list or if the call to the
	 * method used to retrieve the property throws it.
	 * @throws NullPointerException If any parameter is <code>null</code>.
	 * @throws IllegalAccessException If the method used to retrieve the property on a bean can't be accessed.
	 * @throws InvocationTargetException If the method used to retrieve the property throws an exception
	 * @throws IntrospectionException If the bean information can't be retrieve.
	 */
	public static <B,P> Map<P,List<B>> mapBeans(Collection<B> beans, String propertyName, Class<P> propertyClass) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IntrospectionException {
		Map<P, List<B>> map = new HashMap<P, List<B>>(beans.size());
		//empty map if empty
		if (!beans.isEmpty()) {
			Method reader = null;
			//map the beans
			for (B b : beans) {
				if (reader == null) {
					//get all the properties from the first element of the list
					for (PropertyDescriptor pd : Introspector.getBeanInfo(b.getClass()).getPropertyDescriptors()) {
						if (pd.getName().equals(propertyName) && propertyClass.isAssignableFrom(pd.getPropertyType())) {
							reader = pd.getReadMethod();
							break;
						}
					}
					//no method to read the property
					if (reader == null) {
						throw new IllegalArgumentException("No method available to read property: "+propertyName+" of type: "+propertyClass.getSimpleName());
					}
				}
				@SuppressWarnings("unchecked")
				P p = (P) reader.invoke(b);
				List<B> l = map.get(p);
				if (l == null) {
					map.put(p, l = new ArrayList<B>());
				}
				l.add(b);
			}
		}
		return map;
	}
	
	/**
	 * This method map a list of bean by one of its property. This can be useful if you want to access a bean by a certain property. This
	 * method also make a type check on the property type. To get the method to use to retrieve the property, this method use the first bean
	 * of the list. If the list is empty, the method returns an empty map. This method only throws {@link RuntimeException}, if {@link #mapBeans(List, String, Class)}
	 * throw a non-runtime exception, the exception is wrapped and rethrown.
	 * @param <B> The bean type.
	 * @param <P> The property type.
	 * @param beans The list of beans to group.
	 * @param propertyName The name of the property to use to group.
	 * @param propertyClass The type of the property to use to group.
	 * @return The mapped beans, never <code>null</code>.
	 * @throws RuntimeException Any exception thrown by {@link #mapBeans(List, String, Class)}.
	 */
	public static <B,P> Map<P,B> mapBeansOnceSilent(Collection<B> beans, String propertyName, Class<P> propertyClass) throws RuntimeException {
		try {
			return mapBeansOnce(beans, propertyName, propertyClass);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * This method map a list of bean by one of its property. This can be useful if you want to access a bean by a certain property. This
	 * method also make a type check on the property type. To get the method to use to retrieve the property, this method use the first bean
	 * of the list. If the list is empty, the method returns an empty map.
	 * @param <B> The bean type.
	 * @param <P> The property type.
	 * @param beans The list of beans to group.
	 * @param propertyName The name of the property to use to group.
	 * @param propertyClass The type of the property to use to group.
	 * @return The mapped beans, never <code>null</code>.
	 * @throws IllegalArgumentException If no method can be used to retrieve the property on the first bean of the list or if the call to the
	 * method used to retrieve the property throws it.
	 * @throws NullPointerException If any parameter is <code>null</code>.
	 * @throws IllegalAccessException If the method used to retrieve the property on a bean can't be accessed.
	 * @throws InvocationTargetException If the method used to retrieve the property throws an exception
	 * @throws IntrospectionException If the bean information can't be retrieve.
	 */
	public static <B,P> Map<P,B> mapBeansOnce(Collection<B> beans, String propertyName, Class<P> propertyClass) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IntrospectionException {
		Map<P, B> map = new HashMap<P, B>(beans.size());
		//empty map if empty
		if (!beans.isEmpty()) {
			Method reader = null;
			//map the beans
			for (B b : beans) {
				if (reader == null) {
					//get all the properties from the first element of the list
					for (PropertyDescriptor pd : Introspector.getBeanInfo(b.getClass()).getPropertyDescriptors()) {
						if (pd.getName().equals(propertyName) && propertyClass.isAssignableFrom(pd.getPropertyType())) {
							reader = pd.getReadMethod();
							break;
						}
					}
					//no method to read the property
					if (reader == null) {
						throw new IllegalArgumentException("No method available to read property: "+propertyName+" of type: "+propertyClass.getSimpleName());
					}
				}
				@SuppressWarnings("unchecked")
				P p = (P) reader.invoke(b);
				map.put(p, b);
			}
		}
		return map;
	}
}
