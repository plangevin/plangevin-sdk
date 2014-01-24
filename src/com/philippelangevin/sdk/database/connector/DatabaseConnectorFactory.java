package com.philippelangevin.sdk.database.connector;
/**
*
* @author ipainchaud <BR>
* Copyright:
* (c) 2008, C-Tec Inc. - All rights reserved
*/
/*
* History
* Date       Name        BT      Description
* 2006-09-11 IPainchaud          initial revision
*/

public class DatabaseConnectorFactory {
	public static final int DEFAULT_PORT_MSSQL = 1433;
	public static final int DEFAULT_PORT_POSTGRE = 5432;
	public static final int DEFAULT_PORT_MYSQL = 3306;
	
	public static DatabaseConnectorIF buildDBConnector( DatabaseDefinitionIF dbDefinition ) {
		return buildDBConnector(dbDefinition, null) ;
	}
	
	public static DatabaseConnectorIF buildDBConnector( DatabaseDefinitionIF dbDefinition, Integer srvPort ) {
		switch( dbDefinition.getDBType() ) {
			case PostgreSQL:
				if( null == srvPort ) {
					srvPort = DEFAULT_PORT_POSTGRE;
				}
				return new Postgre83Connector( dbDefinition.getLocation(), srvPort, dbDefinition.getName() );
				
			case MSSQL:
				if( null == srvPort ) {
					srvPort = DEFAULT_PORT_MSSQL;
				}
				return new SQLServerJTDSConnector( dbDefinition.getLocation(), srvPort, dbDefinition.getName() );
			
			case SQLite:
				return new SQLiteConnector(dbDefinition.getLocation(), dbDefinition.getName()) ;
				
			case MySQL:
				if( null == srvPort ) {
					srvPort = DEFAULT_PORT_MYSQL;
				}
				return new MySQLConnector(dbDefinition.getLocation(), srvPort, dbDefinition.getName()) ;
				
			case XML:
			
			default:
				throw new AssertionError( "Forgotten database ??? ( " + dbDefinition.getName() + " )" );
		}
	}
	
	/**
	 * use the other factory's method instead.
	 */
//	@Deprecated
//	public static DatabaseConnectorIF getDefaultMSSQLDBConnector( String dbName ) {
//		return new SQLServerJTDSConnector( "colmatec005", 1433, dbName );
//	}
}
