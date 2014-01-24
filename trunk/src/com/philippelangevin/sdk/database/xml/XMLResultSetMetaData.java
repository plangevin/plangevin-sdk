package com.philippelangevin.sdk.database.xml;

import static java.sql.Types.VARCHAR;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;

public class XMLResultSetMetaData implements ResultSetMetaData {
	
	private AbstractList<String> m_ColumnNames = null;

	public XMLResultSetMetaData( AbstractList<String> ColumnNames ) {
		m_ColumnNames = ColumnNames;
	}
	
	protected XMLResultSetMetaData() { 
		this( new ArrayList<String>() );
	}
	
	/**
	 * @param colName
	 * @param colClass
	 * @return the column index
	 */
	protected int addColumn( String colName ) {
		int index = m_ColumnNames.indexOf( colName );
		if( 0 > index ) {
			/*
			 * adding this column
			 */
			index = m_ColumnNames.size();
			m_ColumnNames.add( colName );
		}
		return index;
	}
	
	protected AbstractList<String> getColumnNameList() {
		return m_ColumnNames;
	}
	
	@Override
	public String getCatalogName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		/*
		 * because our values come from a xml file, this is very hard to know the value type...
		 * Always consider it as an object!
		 */
		return Object.class.getCanonicalName();
	}

	@Override
	public int getColumnCount() throws SQLException {
		return m_ColumnNames.size();
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}


	/**
	 * @param column starting at 1 like SQL ResultSet
	 */
	@Override
	public String getColumnLabel(int column) throws SQLException {
		return m_ColumnNames.get(column-1);
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		return m_ColumnNames.get(column-1);
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		return VARCHAR;
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		return getColumnClassName( column );
	}

	/* ***************************************************************************************
	 *  _   _       _     _                 _                           _           _ 
	 * | \ | | ___ | |_  (_)_ __ ___  _ __ | | ___ _ __ ___   ___ _ __ | |_ ___  __| |
	 * |  \| |/ _ \| __| | | '_ ` _ \| '_ \| |/ _ \ '_ ` _ \ / _ \ '_ \| __/ _ \/ _` |
	 * | |\  | (_) | |_  | | | | | | | |_) | |  __/ | | | | |  __/ | | | ||  __/ (_| |
	 * |_| \_|\___/ \__| |_|_| |_| |_| .__/|_|\___|_| |_| |_|\___|_| |_|\__\___|\__,_|
	 *                               |_|                                              
	 *************************************************************************************** */
	@Override
	public int getPrecision(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getScale(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSchemaName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTableName(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCurrency(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int isNullable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadOnly(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSearchable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWritable(int column) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

}
