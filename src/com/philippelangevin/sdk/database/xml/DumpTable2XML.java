package com.philippelangevin.sdk.database.xml;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.philippelangevin.sdk.database.connector.SQLServerJTDSConnector;
import com.philippelangevin.sdk.database.dbAccess.DatabaseConnection;
import com.philippelangevin.sdk.database.dbAccess.SQLServerDatabaseConnection;
import com.philippelangevin.sdk.xml.XMLHelper;

/**
 * <p> Title: {@link DumpTable2XML} <p>
 * <p> Description: Simply dumps the content of a sql request into an xml file.<BR></p>
 * <p> Company : C-Tec world <p>
 * 
 * @author israel israel@ctecworld.com
 * Copyright: (c) 2009, C-Tec Inc. - All rights reserved
 */
/*
 * History
 * ------------------------------------------------
 * Date       Name        BT   Description
 * 2008-12-03 IPainchaud       initial revision
 */
public class DumpTable2XML {
	
	public static void dump( DatabaseConnection dbAccess, File outputFile, String sqlQuery ) throws SQLException, IOException {
		Document dom = getDomFromQuery( dbAccess, sqlQuery );
		XMLHelper.saveXMLFile( dom, outputFile.getAbsolutePath(), XMLHelper.SAVING_MODES.SIMPLY_OVERWRITE );
	}
	
	public static Document getDomFromQuery( DatabaseConnection dbAccess, String sqlQuery ) throws SQLException {
		Document dom = XMLHelper.newDocument( "root", null );
		/*
		 * create/close all the other tags
		 */
		ResultSet rs = dbAccess.executeQuery( sqlQuery );
		ResultSetMetaData metaData = rs.getMetaData();
		int colCount = metaData.getColumnCount();
		while( rs.next() ) {
			Element row = XMLHelper.addChild( dom, dom.getDocumentElement(), "row", null, true );
			for( int i = 1; i <= colCount; i++ ) {
				String colName = metaData.getColumnName( i );
				Object colContent = rs.getObject( i );
				String colTextContent = (null == colContent) ? "":colContent.toString().trim();

				XMLHelper.addChild( dom, row, colName, colTextContent, true );
			}
		}
		return dom;
	}
	
	/**
	 * WARNING!!!
	 * sqlQuery must have an ORDER BY statement AND THE FIRST SELECT COLUMN MUST BE THE SAME AS THE
	 * ONE IN THE ORDER BY
	 * @param dbAccess
	 * @param outputFile
	 * @param sqlQuery
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void dumpOrderBy( DatabaseConnection dbAccess, File outputFile, String sqlQuery ) throws SQLException, IOException {
		final String ORDER_BY = "ORDER BY";
		/*
		 * find the "order by" statement to be able to recognize them when we get them
		 */
		String orderBy = sqlQuery.substring( sqlQuery.indexOf( ORDER_BY ) + ORDER_BY.length() + 1 );
		String orderByList[] = orderBy.split( "\\s*,\\s*" );
		int nbOfOrderBy = orderByList.length;
		if( 0 == nbOfOrderBy ) {
			throw new AssertionError( "no " + ORDER_BY + " statement into sqlQuery when calling dumpOrderBy(...)" );
		}
		String lastOrderByList[] = new String[nbOfOrderBy];
		for( int i = 0; i < nbOfOrderBy; i ++ ) {
			/*
			 * strip the column names to only keep names (Ex.: "lang.descKey " ==> "descKey")
			 */
			int splitingIndex = orderByList[i].indexOf( '.' );
			if( -1 < splitingIndex ) {
				orderByList[i] = orderByList[i].substring( splitingIndex + 1 ).trim();
			} else {
				orderByList[i] = orderByList[i].trim();
			}
		}
		/*
		 * create the root node
		 */
		Document dom = XMLHelper.newDocument( "root", null );
		Node parentElement = dom.getDocumentElement();
		dom.insertBefore( dom.createComment( "Created on " + new Date().toString() ), parentElement );
//		parentElement.appendChild( dom.createComment( "Created the " + new Date().toString() ) );
		/*
		 * create every other nodes
		 */
		ResultSet rs = dbAccess.executeQuery( sqlQuery );
		ResultSetMetaData metaData = rs.getMetaData();
		int colCount = metaData.getColumnCount();
		while( rs.next() ) {
			for( int i = 1; i <= colCount; i++ ) {
				String colName = metaData.getColumnName( i );
				Object colContent = rs.getObject( i );
				String colTextContent = (null == colContent) ? "":colContent.toString().trim();

				int j;
				/*
				 * look if the tag to create is among the "ORDER BY" ones
				 */
				for( j = 0; j < nbOfOrderBy; j++ ) {
					if( colName.equalsIgnoreCase( orderByList[j] ) ) {
						/*
						 * The tag to create IS among the "ORDER BY" ones, check if its value is 
						 * the same as the last one of this type. If it is of the same value, don't
						 * worry, we don't need to duplicate it.
						 */
						if( !colTextContent.equals( lastOrderByList[j] ) ) {
							/*
							 * the value of this node is not the same as the last one, meaning 
							 * that we need to create a new parent node
							 */
							if( dom.getDocumentElement() != parentElement && null != lastOrderByList[j] ) {
								/*
								 * we get here when we have to go back(down) of some level
								 */
								parentElement = parentElement.getParentNode();
								for( int k = j + 1; k < nbOfOrderBy; k++ ) {
									lastOrderByList[k] = null;
									parentElement = parentElement.getParentNode();
								}
							}
							/*
							 * create the parent node
							 */
							parentElement = XMLHelper.addChild( 
									dom, 
									parentElement,
									colName, 
									colTextContent, 
									true );
							lastOrderByList[j] = colTextContent;
						}
						break;
					}
				}
				if( j == nbOfOrderBy ) {
					/*
					 * the tag to create is NOT among the "ORDER BY" one, create it!
					 */
					XMLHelper.addChild( dom, parentElement, colName, colTextContent, true );
				}
			}
		}
		XMLHelper.saveXMLFile( dom, outputFile.getAbsolutePath(), XMLHelper.SAVING_MODES.SIMPLY_OVERWRITE );
	}
	
	/**
	 * args[0] is the output file name.
	 * args[1] is the SQL query that is to be dumped!
	 * args[2] is the server name
	 * args[3] is the port used 
	 * args[4] is the user name
	 * args[5] is the password
	 * @param args
	 */
	public static void main( String args[] ) {
		try {
			dump( 
					new SQLServerDatabaseConnection( 
							new SQLServerJTDSConnector( 
									args[2], 
									Integer.parseInt( args[3] ), 
									"test.db" 
								),
								args[4], 
								args[5] 
							), 
							new File( args[0] ), 
							args[1] 
						);
		} catch( SQLException e ) {
			e.printStackTrace();
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}
}
