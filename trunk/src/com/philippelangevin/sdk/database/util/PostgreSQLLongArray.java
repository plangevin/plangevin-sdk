package com.philippelangevin.sdk.database.util;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

/**
 * This class provides {@link java.sql.Array} interface for Long[] to be used as PostgreSQL <code>bigint</code> array.
 * 
 * <p><b>Warning:</b> This class has not been tested.
 * 
 * @author Valentine Gogichashvili
 *
 * Adapted from: http://valgogtech.blogspot.com/2009/02/passing-arrays-to-postgresql-database.html
 */
public class PostgreSQLLongArray implements Array {
	private final Long[] longArray;
	private final String stringValue;
	
	public PostgreSQLLongArray(Long[] intArray) {
		this.longArray = intArray;
		this.stringValue = longArrayToPostgreSQLLongArrayString(longArray);
	}
	
	@Override
	public String toString() {
		return stringValue;
	}
	
	/**
	 * This static method can be used to convert a Long array to string representation of PostgreSQL Long array.
	 * @param a source long array
	 * @return string representation of a given integer array
	 */
	public static String longArrayToPostgreSQLLongArrayString(Long[] a) {
		if ( a == null ) {
			return null;
		}
		final int al = a.length;
		if ( al == 0 ) {
			return "{}";
		}
		StringBuilder sb = new StringBuilder( 2 + al * 7 ); // as we usually operate with 6 digit numbers + 1 symbol for a delimiting comma
		sb.append('{');
		for (int i = 0; i < al; i++) {
			if ( i > 0 ) sb.append(',');
			sb.append(a[i]);
		}
		sb.append('}');
		return sb.toString();
	}
	
	public static String longArrayToCommaSeparatedString(Long[] a) {
		if ( a == null ) {
			return null;
		}
		final int al = a.length;
		if ( al == 0 ) {
			return "";
		}
		StringBuilder sb = new StringBuilder( al * 7 ); // as we usually operate with 6 digit numbers + 1 symbol for a delimiting comma
		for (int i = 0; i < al; i++) {
			if ( i > 0 ) sb.append(',');
			sb.append(a[i]);
		}
		return sb.toString();
	}
	
	@Override
	public void free() throws SQLException {
	}

	@Override
	public Object getArray() throws SQLException {
		return longArray == null ? null : Arrays.copyOf(longArray, longArray.length);
	}

	@Override
	public Object getArray(Map<String, Class<?>> map) throws SQLException {
		return getArray();
	}

	@Override
	public Object getArray(long index, int count) throws SQLException {
		return longArray == null ? null : Arrays.copyOfRange(longArray, (int)index, (int)index + count );
	}

	@Override
	public Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
		return getArray(index, count);
	}

	@Override
	public int getBaseType() throws SQLException {
		return java.sql.Types.BIGINT;
	}

	@Override
	public String getBaseTypeName() throws SQLException {
		return "int8";
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResultSet getResultSet(long index, int count) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}

}
