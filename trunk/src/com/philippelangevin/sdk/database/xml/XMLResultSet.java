package com.philippelangevin.sdk.database.xml;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p> Title: {@link XMLResultSet} <p>
 * <p> Description: Used by XmlDAO to return ResultSets... No implementation of ResultSet was 
 * available, we had to implement our own.</p>
 * <p> Company : C-Tec world <p>
 * 
 * @author israel israel@ctecworld.com
 * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
 */
/*
 * History
 * ------------------------------------------------
 * Date         Name        BT   Description
 * 2009-??-??   israel           initial Revision
 */
public class XMLResultSet implements ResultSet	{
	
	protected XMLResultSetMetaData m_md = null;
	private AbstractList<String> m_ColumnNames = null;
	protected AbstractList<AbstractList<String>> m_rows = null;
	private AbstractList<String> m_currentRow = null;
	private boolean m_preferIds2Text = true;
	
	private int m_rowId = -1;
	private boolean m_wasNull = false;
	
	public XMLResultSet() {
		m_md = new XMLResultSetMetaData();
		m_ColumnNames = m_md.getColumnNameList();
		m_rows = new ArrayList<AbstractList<String>>();
	}
	
	/**
	 * Build a RS row with n and n's brother and sisters. 
	 * Only consider the current level of the node given. It won't consider deeper (node's childs)
	 * because it would be harder to fill correctly the RS afterward and because it would be very
	 * hard otherwise to control which level to insert and which not to.
	 * @param n
	 * @param rs
	 * @return
	 */
	static private AbstractList<String> buildRSRow( Node n, XMLResultSet rs ) {
		assert null != n;
		AbstractList<String> row = new ArrayList<String>();
		String nodeName = null;
		String nodeValue = null;
		/*
		 * get the primary key
		 */
		Node parent = n.getParentNode();
		while( null != parent ) {
			Node parentValueNode = parent.getFirstChild();
			if( "#text".equals( parentValueNode.getNodeName() ) ) {
				nodeValue = parentValueNode.getNodeValue();
				if( null != nodeValue ) {
					nodeValue = nodeValue.trim();
					nodeName = parent.getLocalName();
					if( !nodeValue.isEmpty() ) {
						int index = rs.m_md.addColumn( nodeName );
						setColValue( row, index, nodeValue );
					}
				}
			}
			parent = parent.getParentNode();
		}
		/*
		 * get the remaining values
		 */
		while( null != n ) {
			/*
			 * the local name is the name without the name space
			 * the node name is the name with the name space
			 * Here, we take the local name for simplicity
			 */
			nodeName = n.getLocalName();
			if( null != nodeName ) {
				/*
				 * Get the value of the node.
				 * Yes, the value is in the first child node's value, just like the attributes.
				 */
				Node child = n.getFirstChild();
				nodeValue = (null == child) ? null:child.getNodeValue();
				/*
				 * Get the attributes
				 */
				if( n.hasAttributes() && rs.m_preferIds2Text ) {
					NamedNodeMap nnm = n.getAttributes();
					nodeValue = nnm.item( 0 ).getNodeValue();
					if( 1 < nnm.getLength() ) {
						System.err.println( 
								"***************\n" +
								"WARNING! A xml node given to " + XMLResultSet.class.getCanonicalName() + " contains more than one attribute...\n" +
								"This is not possible to put more than one information into a column of a ResultSet. I will only consider the first one.\n" +
								"***************"
								);
						Thread.dumpStack();
					}
				}
				int index = rs.m_md.addColumn( nodeName );
				setColValue( row, index, nodeValue );
			}
			n = n.getNextSibling();
		} // end while
		return row;
	}

	static public ResultSet buildResultSet( Node node, boolean preferIds2Text ) {
		XMLResultSet rs = new XMLResultSet();
		rs.m_preferIds2Text = preferIds2Text;
		rs.m_rows.add( buildRSRow( node.getFirstChild(), rs ) );
		return rs;
	}
	
	static public ResultSet buildResultSet( NodeList nodeList ) {
		return buildResultSet( nodeList, true );
	}
	
	static public ResultSet buildResultSet( NodeList nodeList, boolean preferIds2Text ) {
		XMLResultSet rs = new XMLResultSet();
		rs.m_preferIds2Text = preferIds2Text;
		int nbOfNodeInList = nodeList.getLength();
		for( int i = 0; i < nbOfNodeInList; i++ ) {
			rs.m_rows.add( buildRSRow( nodeList.item( i ).getFirstChild(), rs ) );
		}
		return rs;
	}
	
	/**
	 * @see addColumns2RS
	 * @param colDescList
	 * @param fieldValues
	 * @return
	 */
	static public ResultSet buildResultSet( String[][] values, Object...metaData  ) {
		return addColumns2RS( null, values, metaData );
	}
	
	/**
	 * When rs is null, create a brand new ResultSet and fill it with "col2Add"'s values.
	 * When rs is NOT null, assumes that "col2Add" is in fact additional columns to add to "rs".
	 * The parameter "metaData" is of type Object. This makes it easier to add columns. However,
	 * this function was made to function with XMLTagInfo. Hence, if metaData is in fact an 
	 * instance of XMLTagInfo, it will look if this XMLTagInfo.getTagName() returns null. If it
	 * returns null, it will skip this column. This makes it easy to add 
	 * TransferableObject.AdditionalFields and have a resultSet only containing the fields that 
	 * really come from the database.
	 * @param rs ResultSet that will contain the values found in "col2Add"
	 * @param col2Add values to add to "rs" (ResultSet)
	 * @param metaData Simply strings or XMLTagInfos defining the column headers
	 * @return
	 */
	static public ResultSet addColumns2RS( ResultSet rs, String[][] col2Add, Object...metaData ) {
		try {
			XMLResultSet xmlRS;
			if( null == rs ) {
				rs = xmlRS = new XMLResultSet();
			} else {
				xmlRS = (XMLResultSet)rs;
				if( col2Add.length != xmlRS.m_rows.size() && 0 != xmlRS.m_rows.size() ) {
					throw new IllegalArgumentException( "col2Add does not match rs. They don't have the same number of rows" );
				}
			}
			XMLResultSetMetaData rsmd = (XMLResultSetMetaData)rs.getMetaData();
			int nbOfRow = col2Add.length;
			int nbOfCol = metaData.length;
			/*
			 * insert column by column every data to add
			 * Column by column because XMLResultSetMetaData.addColumn is the longest process 
			 * (in these 2 loops) and we want to do it only once by column.
			 */
			for( int colIndex = 0; colIndex < nbOfCol; colIndex++ ) {
				if( metaData[colIndex] instanceof XMLTagInfo && null == ((XMLTagInfo)metaData[colIndex]).getTagName() ) {
					continue;
				}
				int colAdded = rsmd.addColumn( metaData[colIndex].toString() );
				for( int rowIndex = 0; rowIndex < nbOfRow; rowIndex++ ) {
					AbstractList<String> row;
					if( rowIndex < xmlRS.m_rows.size() ) {
						row = xmlRS.m_rows.get( rowIndex );
					} else {
						row = new ArrayList<String>();
						xmlRS.m_rows.add( row );
					}
					row.add( colAdded, col2Add[rowIndex][colIndex] );
				}
			}
			return rs;
		} catch( SQLException e ) {
			/*
			 * not supposed to happen... unless they are use by XMLResultSet or 
			 * XMLResultSetMetaData
			 */
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This is used to add a new row to the ResultSet (containing the exact same columns).
	 * If rs==null we build a new ResultSet with the data provided.
	 * Object...metaData has to match exactly rs.getMetaData() (same columns at the same indexes).
	 * @param rs The ResultSet used to add the new row
	 * @param rows2Add The values to add to rs
	 * @param metaData The column headers
	 * @return The ResultSet containing the added row
	 */
	static public ResultSet addRows2RS( ResultSet rs, String[][] rows2Add, Object...metaData ) {
		if (rows2Add == null) {
			System.err.println("XMLResultSet.addRows2RS() rows2Add is null.");
		} else if (rs == null) {
			return buildResultSet(rows2Add, metaData);
		} else {
			try {
				XMLResultSet xmlRS = (XMLResultSet) rs;
				XMLResultSetMetaData xmlMD = (XMLResultSetMetaData) xmlRS.getMetaData();
				AbstractList<String> mdNameList = xmlMD.getColumnNameList();
				
				if (metaData.length != mdNameList.size()) {
					System.err.println("XMLResultSet.addRows2RS() The size of the meta data provided is different than the size of the ResultSet's meta data.");
					return null;
				}
				for (int i = 0; i < metaData.length; i++) {
					if (!metaData[i].toString().equals(mdNameList.get(i))) {
						System.err.println("XMLResultSet.addRows2RS() The meta data provided (" + metaData[i].toString() + ") is different than the ResultSet's meta data (" + mdNameList.get(i) + ").");
						return null;
					}
				}
				
				for (int rowIndex = 0; rowIndex < rows2Add.length; rowIndex++) {
					AbstractList<String> newRow = new ArrayList<String>();
					newRow.addAll(Arrays.asList(rows2Add[rowIndex]));
					xmlRS.m_rows.add(newRow);
				}
				
				return xmlRS;
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Useful to set a value to a column or simply to add a column to the current row of the result
	 * set given (rs).
	 * @param rs
	 * @param colName
	 * @param value
	 */
	static public void setColValue( ResultSet rs, String colName, String value ) {
		XMLResultSet xmlrs = (XMLResultSet)rs;
		setColValue( xmlrs, xmlrs.m_md.addColumn( colName ), value );
	}
	
	static public void setColValue( ResultSet rs, int colIndex, String value ) {
		setColValue( ((XMLResultSet)rs).m_currentRow, colIndex, value );
	}
	
	synchronized static private void setColValue( AbstractList<String> row, int colIndex, String value ) {
		int curRowSize = row.size();
		if( colIndex < curRowSize ) {
			row.set( colIndex, ( null == value || value.isEmpty() ) ? null:value );
		} else {
			for( int i = curRowSize; i < colIndex; i++ ) {
				row.add( i, null );
			}
			row.add( colIndex, ( null == value || value.isEmpty() ) ? null:value );
		}
	}
	
	static public int addColumn( ResultSet rs, String colName ) {
		XMLResultSet xmlrs = (XMLResultSet)rs;
		return xmlrs.m_md.addColumn( colName );
	}
	
	/**
	 * to be used for debugging only!!!
	 * Prints the content the of the result set.
	 */
	public void dumpContent() {
		System.out.println( "XMLResultSet.dumpContent()" );
		for( String colTitle : m_ColumnNames ) {
			System.out.print( String.format( "%25s | ", colTitle ) );
		}
		System.out.println();
		for( AbstractList<String> row : m_rows ) {
			for( String val: row ) {
				if( null != val && 25 < val.length() ) {
					val = val.substring( 0, 25 );
				}
				System.out.print( String.format( "%25s | ", val ) );
			}
			System.out.println();
		}
	}
	
	/**
	 * will add the rows contained by rs at the end of this resultSet. rs must have the same 
	 * columns at the same indexes.
	 * @param rs
	 */
	public void addAllRows( ResultSet rs ) {
		try {
			XMLResultSet xmlRS = (XMLResultSet) rs;
			XMLResultSetMetaData xmlMD = (XMLResultSetMetaData) xmlRS.getMetaData();
			AbstractList<String> mdNameList = xmlMD.getColumnNameList();
			
			/* add the missing columns */
			for( String colName: mdNameList ) {
				if( !this.m_ColumnNames.contains( colName ) ) {
					addColumn( this, colName );
				}
			}
			/* go through rs (the resultSet to merge) and add its content to "this" */
			AbstractList<String> currentRSColumns = this.m_md.getColumnNameList();
			while( xmlRS.next() ) {
				/* create the new row */
				AbstractList<String> newRow = new ArrayList<String>( this.m_md.getColumnNameList().size() );
				for( int i = 0; i < this.m_md.getColumnNameList().size(); i++ ) {
					newRow.add( null ); // fill the array for easier value replacements
				}
				/* fill the new row */
				for( int i = 0; i < mdNameList.size() ; i++ ) {
					int index = currentRSColumns.indexOf( mdNameList.get( i ) );
					newRow.set( index, xmlRS.getString( i + 1 ) );
				}
				/* add it to "this" */
				this.m_rows.add( newRow );
			}
		} catch( SQLException e ) {
			e.printStackTrace();
		}
	}
	
	/* *****************************************************************************
	 *  ____                 _ _   ____       _   
	 * |  _ \ ___  ___ _   _| | |_/ ___|  ___| |_ 
	 * | |_) / _ \/ __| | | | | __\___ \ / _ \ __|
	 * |  _ <  __/\__ \ |_| | | |_ ___) |  __/ |_ 
	 * |_| \_\___||___/\__,_|_|\__|____/ \___|\__|
	 *                                            
	 *  ___                 _                           _        _   _             
	 * |_ _|_ __ ___  _ __ | | ___ _ __ ___   ___ _ __ | |_ __ _| |_(_) ___  _ __  
	 *  | || '_ ` _ \| '_ \| |/ _ \ '_ ` _ \ / _ \ '_ \| __/ _` | __| |/ _ \| '_ \ 
	 *  | || | | | | | |_) | |  __/ | | | | |  __/ | | | || (_| | |_| | (_) | | | |
	 * |___|_| |_| |_| .__/|_|\___|_| |_| |_|\___|_| |_|\__\__,_|\__|_|\___/|_| |_|
	 *               |_|                                                           
	 ***************************************************************************** */
	
	@Override
	public boolean next() throws SQLException {
		m_rowId++;
		if( m_rowId < m_rows.size() ) {
			m_currentRow = m_rows.get( m_rowId );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		if( 0 < columnIndex && m_ColumnNames.size() >= columnIndex ) {
			if( m_currentRow.size() < columnIndex ) {
				/*
				 * sometimes the current row can have less values than their is columns... in that
				 * case the missing values are all null!
				 */
				return null;
			}
			Object obj = m_currentRow.get( columnIndex -1 );
			m_wasNull = null == obj;
			return obj;
		} else {
			throw new SQLException( "Column label/index not valid (index == " + columnIndex + ")" );
		}
	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> classType) throws SQLException {
		return classType.cast(getObject(columnIndex)) ;
	}

	@Override
	public <T> T getObject(String columnName, Class<T> classType) throws SQLException {
		return classType.cast(getObject(columnName)) ;
	}
	
	@Override
	public Object getObject(String columnLabel) throws SQLException {
		return getObject( findColumn( columnLabel ) );
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		String s = getString( columnIndex );
		return (null != s) ? Boolean.parseBoolean( s ):false;
	}

	@Override
	public boolean getBoolean(String columnLabel) throws SQLException {
		return getBoolean( findColumn( columnLabel ) );
	}
	
	private long getTime( String s, String format ) throws SQLException {
		SimpleDateFormat dateFormat = new SimpleDateFormat( format );
		dateFormat.setLenient( true );
		try {
			return dateFormat.parse( s ).getTime();
		} catch( ParseException e ) {
			e.printStackTrace();
			throw new SQLException( "Invalid data format found into the xml node: " + s );
		}
	}
	
	@Override
	public Date getDate(int columnIndex) throws SQLException {
		String date = getString( columnIndex );
		if( -1 != date.indexOf( 'T' ) ) {
			return new Date( getTime( date, "yyyy-MM-dd'T'HH:mm" ) );
		} else {
			return new Date( getTime( date, "yyyy-MM-dd" ) );
		}
	}

	@Override
	public Date getDate(String columnLabel) throws SQLException {
		return getDate( findColumn( columnLabel ) );
	}
	
	@Override
	public Time getTime(int columnIndex) throws SQLException {
		return new Time( getTime( getString( columnIndex ), "HH:mm" ) );
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		return getTime( findColumn( columnLabel ) );
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return new Timestamp( getTime( getString( columnIndex ), "yyyy-MM-dd'T'HH:mm" ) );
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return getTimestamp( findColumn( columnLabel ) );
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		String s = getString( columnIndex );
		return (null != s) ? Integer.parseInt( s ):0;
	}

	@Override
	public int getInt(String columnLabel) throws SQLException {
		return getInt( findColumn( columnLabel ) );
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		String s = getString( columnIndex );
		return (null != s) ? Long.parseLong( s ):0;
	}

	@Override
	public long getLong(String columnLabel) throws SQLException {
		return getLong( findColumn( columnLabel ) );
	}
	
	@Override
	public String getString(int columnIndex) throws SQLException {
		Object obj = getObject( columnIndex );
		return (null == obj) ? null:obj.toString();
	}

	@Override
	public String getString(String columnLabel) throws SQLException {
		return getString( findColumn( columnLabel ) );
	}
	
	@Override
	public float getFloat(int columnIndex) throws SQLException {
		String s = getString( columnIndex );
		return (null != s) ? Float.parseFloat( s ):0;
	}

	@Override
	public float getFloat(String columnLabel) throws SQLException {
		return getFloat( findColumn( columnLabel ) );
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		String s = getString( columnIndex );
		return (null != s) ? Double.parseDouble( s ):0;
	}

	@Override
	public double getDouble(String columnLabel) throws SQLException {
		return getDouble( findColumn( columnLabel ) );
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return m_md;
	}
	
	@Override
	public boolean wasNull() throws SQLException {
		return m_wasNull;
	}

	@Override
	public boolean absolute(int row) throws SQLException {
		int size = m_rows.size();
		if( size < Math.abs( row ) ) {
			return false;
		}
		if( 0 < row ) {
			m_rowId = row - 2;
		} else {
			m_rowId = size + ( row - 1 );
		}
		return this.next();
	}

	@Override
	public void beforeFirst() throws SQLException {
		m_rowId = -1;
		m_currentRow = null;
	}

	@Override
	public void afterLast() throws SQLException {
		m_rowId = m_rows.size();
		m_currentRow = null;
	}

	@Override
	public boolean first() throws SQLException {
		return absolute( 1 );
	}

	@Override
	public boolean last() throws SQLException {
		return absolute( m_rows.size() );
	}

	@Override
	public boolean isFirst() throws SQLException {
		return m_rowId == 0;
	}

	@Override
	public boolean isLast() throws SQLException {
		return m_rowId == m_rows.size() - 1;
	}

	@Override
	public int getRow() throws SQLException {
		return m_rowId + 1;
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		int index = m_ColumnNames.indexOf( columnLabel );
		if( -1 == index ) {
			return -1;
		}
		return index + 1;
	}

	@Override
	public void close() throws SQLException {
		return;
	}

	@Override
	public Statement getStatement() throws SQLException {
		return null;
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
	public int getType() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Array getArray(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Array getArray(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel, int scale)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Blob getBlob(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Blob getBlob(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte getByte(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Clob getClob(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Clob getClob(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getConcurrency() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCursorName() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFetchDirection() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFetchSize() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getHoldability() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Ref getRef(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Ref getRef(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public short getShort(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal)
			throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public URL getURL(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void insertRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isClosed() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean previous() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void refreshRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean relative(int rows) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean rowInserted() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateArray(int columnIndex, Array x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateArray(String columnLabel, Array x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, int length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}
	

	@Override
	public void updateBigDecimal(String columnLabel, BigDecimal x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, int length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x,
			long length) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream,
			long length) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBoolean(String columnLabel, boolean x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateByte(String columnLabel, byte x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader,
			int length) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateClob(String columnLabel, Reader reader)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateDate(String columnLabel, Date x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateDouble(String columnLabel, double x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateFloat(String columnLabel, float x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateInt(String columnLabel, int x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateLong(String columnLabel, long x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNClob(int columnIndex, NClob clob) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNClob(String columnLabel, NClob clob) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNString(int columnIndex, String string)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNString(String columnLabel, String string)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateNull(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateObject(String columnLabel, Object x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scaleOrLength)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateObject(String columnLabel, Object x, int scaleOrLength)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xmlObject)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML xmlObject)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateShort(String columnLabel, short x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateString(String columnLabel, String x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateTime(String columnLabel, Time x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateTimestamp(String columnLabel, Timestamp x)
			throws SQLException {
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
