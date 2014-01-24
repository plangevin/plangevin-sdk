/**
 * 
 */
package com.philippelangevin.sdk.dataStructure;

import java.util.Arrays;
import java.util.List;

/**
 * Generic tuple class enclosing useful class implementation. Those classes are not abstract but should be created
 * with more explicit names, ex: Cell, Dimension, MapKeyValue, ...
 * @author pcharette
 * @date 2010-09-20
 */
public class Tuple {

	/**
	 * A 2 values tuple.
	 * @author pcharette
	 * @date 2010-09-20
	 */
	@SuppressWarnings("unchecked")
	public static class Pair<T,U> extends Tuple {
		public Pair() {
			super(2);
		}
		
		public Pair(Pair<T,U> pair) {
			super(pair);
		}
		
		public Pair(T first, U second) {
			super(first, second);
		}
		
		public T getFirst() {
			return (T) get(0);
		}

		public U getSecond() {
			return (U)get(1);
		}

		protected void setFirst(T first) {
			set(0, first);
		}
		
		protected void setSecond(U second) {
			set(1, second);
		}
	}
	
	/**
	 * A 3 values tuple.
	 * @author pcharette
	 * @date 2010-09-20
	 */
	@SuppressWarnings("unchecked")
	public static class Triple<T,U,V> extends Tuple {
		public Triple() {
			super(3);
		}
		
		public Triple(T first, U second, V third) {
			super(first, second, third);
		}
		
		public Triple(Triple<T,U,V> triple) {
			super(triple);
		}
		
		public T getFirst() {
			return (T) get(0);
		}

		public U getSecond() {
			return (U)get(1);
		}

		public V getThird() {
			return (V) get(2);
		}
		
		protected void setFirst(T first) {
			set(0, first);
		}

		protected void setSecond(U second) {
			set(1, second);
		}
		
		protected void setThird(V third) {
			set(2, third);
		}
	}
	
	/**
	 * A 4 values tuple.
	 * @author pcharette
	 * @date 2010-09-20
	 */
	@SuppressWarnings("unchecked")
	public static class Quadruple<T,U,V,W> extends Tuple {
		public Quadruple() {
			super(4);
		}
		
		public Quadruple(Quadruple<T,U,V,W> quad) {
			super(quad);
		}
		
		public Quadruple(T first, U second, V third, W fourth) {
			super(first, second, third, fourth);
		}
		
		public T getFirst() {
			return (T) get(0);
		}

		public U getSecond() {
			return (U)get(1);
		}
		
		public V getThird() {
			return (V) get(2);
		}

		public W getFourth() {
			return (W) get(3);
		}
		
		protected void setFirst(T first) {
			set(0, first);
		}

		protected void setSecond(U second) {
			set(1, second);
		}
		
		protected void setThird(V third) {
			set(2, third);
		}
		
		protected void setFourth(W fourth) {
			set(3, fourth);
		}
	}
	
	/**
	 * The size of the tuple is immutable.
	 */
	protected final Object[] values;
	
	/**
	 * Creates a tuple with the specified size.
	 * @param size
	 */
	public Tuple(int size) {
		this.values = new Object[size];
	}
	
	/**
	 * Creates a tuple with the given values and the size of the array.
	 * @param values
	 */
	public Tuple(Object ... values) {
		this.values = Arrays.copyOf(values, values.length);
	}
	
	/**
	 * Copy constructor.
	 * @param tuple
	 */
	public Tuple(Tuple tuple) {
		this.values = Arrays.copyOf(tuple.values, tuple.values.length);
	}
	
	/**
	 * Returns the object at the given position.
	 * @param pos
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	protected Object get(int pos) throws ArrayIndexOutOfBoundsException {
		return values[pos];
	}
	
	/**
	 * Puts the object at the given position.
	 * @param pos
	 * @param value
	 * @throws ArrayIndexOutOfBoundsException
	 */
	protected void set(int pos, Object value) throws ArrayIndexOutOfBoundsException {
		values[pos] = value;
	}
	
	/**
	 * Returns the immutable size of this tuple.
	 * @return
	 */
	protected int getSize() {
		return values.length;
	}
	
	/**
	 * Utility method to transform a tuple in list.
	 * @return
	 */
	public List<Object> toList() {
		return Arrays.asList(values);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Arrays.toString(values);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return ObjectUtil.hashCode(values);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tuple) {
			Tuple t = (Tuple)obj;
			return values.length == t.values.length && Arrays.deepEquals(values, t.values);
		}
		return super.equals(obj);
	}
}
