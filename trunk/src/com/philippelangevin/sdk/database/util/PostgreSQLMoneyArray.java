package com.philippelangevin.sdk.database.util;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import com.philippelangevin.sdk.dataStructure.Money;

/**
 * This class provides {@link java.sql.Array} interface for Money[] to be used as PostgreSQL <code>numeric(12,2)</code> array.
 * 
 * <p><b>Warning:</b> This class has not been tested.
 * 
 * @author Valentine Gogichashvili
 *
 * Adapted from: http://valgogtech.blogspot.com/2009/02/passing-arrays-to-postgresql-database.html
 */
public class PostgreSQLMoneyArray implements Array {
	
	private final Money[] moneyArray;
	private final String stringValue;
	
	/**
	 * Initializing constructor
	 * @param moneyArray
	 */
	public PostgreSQLMoneyArray(Money[] moneyArray) {
		this.moneyArray = moneyArray;
		this.stringValue = moneyArrayToPostgreSQLNumericArray(this.moneyArray);
	}
	
	@Override
	public String toString() {
		return stringValue;
	}
	
	/**
	 * This static method can be used to convert an string array to string representation of PostgreSQL text array.
	 * @param moneyArray a source String array
	 * @return string representation of a given text array
	 */
	public static String moneyArrayToPostgreSQLNumericArray(Money[] moneyArray) {
		if ( moneyArray == null ) {
			return null;
		}
		final int al = moneyArray.length;
		if ( al == 0 ) {
			return "{}";
		}
		StringBuilder sb = new StringBuilder( 2 + al * 13 ); // numeric (12,2) is max 12 characters total + the period.
		sb.append('{');
		for (int i = 0; i < al; i++) {
			if ( i > 0 ) sb.append(',');
			sb.append(moneyArray[i].toString());
		}
		sb.append('}');
		return sb.toString();
	}
	
	@Override
	public void free() throws SQLException {
	}
	
	@Override
	public Object getArray() throws SQLException {
		return moneyArray == null ? null : Arrays.copyOf(moneyArray, moneyArray.length);
	}
	
	@Override
	public Object getArray(Map<String, Class<?>> map) throws SQLException {
		return getArray();
	}
	
	@Override
	public Object getArray(long index, int count) throws SQLException {
		return moneyArray == null ? null : Arrays.copyOfRange(moneyArray, (int)index, (int)index + count );
	}
	
	@Override
	public Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
		return getArray(index, count);
	}
	
	@Override
	public int getBaseType() throws SQLException {
		return java.sql.Types.NUMERIC;
	}
	
	@Override
	public String getBaseTypeName() throws SQLException {
		return "numeric";
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
