/**
 * Class that creates a new DataBaseConnection<BR>
 * 
 * @author ipainchaud<BR>
 * Copyright:
 * (c) 2008, C-Tec Inc. - All rights reserved
 */

/*
 * History
 * Date			Name		BT	Description
 * 2008-05-07	FOstiguy	18	Initial version
 * 
 */

package com.philippelangevin.sdk.database.dbAccess;

import com.philippelangevin.sdk.database.connector.DatabaseConnectorFactory;
import com.philippelangevin.sdk.database.connector.DatabaseConnectorIF;
import com.philippelangevin.sdk.database.connector.DatabaseDefinitionIF;
import com.philippelangevin.sdk.database.connector.MySQLConnector;
import com.philippelangevin.sdk.database.connector.Postgre83Connector;
import com.philippelangevin.sdk.database.connector.SQLServerJTDSConnector;
import com.philippelangevin.sdk.database.connector.SQLServerMicrosoftConnector;
import com.philippelangevin.sdk.database.connector.SQLiteConnector;

public class DatabaseConnectionFactory {
	public static DatabaseConnection buildConnection(DatabaseDefinitionIF databaseDefinition)	{
		DatabaseConnectorIF dbConnector = DatabaseConnectorFactory.buildDBConnector(databaseDefinition) ;
		return buildConnection(dbConnector, "", "") ;
	}
	
	public static DatabaseConnection buildConnection(
			DatabaseDefinitionIF databaseDefinition,
			String user,
			String password) {
		DatabaseConnectorIF dbConnector = DatabaseConnectorFactory.buildDBConnector( databaseDefinition );
		return buildConnection( dbConnector, user, password );
	}
	
	public static DatabaseConnection buildConnection(
			DatabaseDefinitionIF databaseDefinition,
			String user,
			String password,
			int srvPort) {
		DatabaseConnectorIF dbConnector = DatabaseConnectorFactory.buildDBConnector( databaseDefinition, srvPort );
		return buildConnection( dbConnector, user, password );
	}
	
	public static DatabaseConnection buildConnection(
			DatabaseConnectorIF dbConnector,
			String user,
			String password ) {
		if (dbConnector instanceof SQLiteConnector){
			return new SQLiteDatabaseConnection( dbConnector ) ;
		}
		else if (dbConnector instanceof Postgre83Connector){
			return new PostgreDatabaseConnection( dbConnector, user, password);
		} 
		else if (dbConnector instanceof SQLServerJTDSConnector || dbConnector instanceof SQLServerMicrosoftConnector){
			return new SQLServerDatabaseConnection( dbConnector, user, password );
		}
		else if (dbConnector instanceof MySQLConnector)	{
			return new MySQLDatabaseConnection((MySQLConnector) dbConnector, user, password) ;
		}
		
		return null ;
	}
}