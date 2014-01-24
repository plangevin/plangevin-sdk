package com.philippelangevin.sdk.database;

import java.sql.SQLException;

import com.philippelangevin.sdk.database.connector.DBType;
import com.philippelangevin.sdk.database.connector.DatabaseDefinitionIF;
import com.philippelangevin.sdk.database.dbAccess.DatabaseConnectionFactory;
import com.philippelangevin.sdk.database.dbAccess.MySQLDatabaseConnection;

public class DatabaseTests {

	public enum DatabaseDefinition implements DatabaseDefinitionIF {
		INVENTAIRE806("langevin.dyndns.org", "Inventaire806", DBType.MySQL), 
		;

		private String serverPath ;
		private String dbName ;
		private DBType dbType ;
		
		private DatabaseDefinition(String serverPath, String dbName, DBType dbType){
			this.serverPath = serverPath ;
			this.dbName = dbName ;
			this.dbType = dbType ;
		}
		
		@Override
		public String getLocation()	{
			return serverPath ;
		}
		
		@Override
		public String getName() {
			return this.dbName ;
		}

		@Override
		public DBType getDBType() {
			return this.dbType ;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MySQLDatabaseConnection conn = (MySQLDatabaseConnection) DatabaseConnectionFactory.buildConnection(DatabaseDefinition.INVENTAIRE806, "plangevin", "45387z") ;
		try {
			conn.openConnection() ;
			
			conn.executeUpdate("CREATE TABLE `Test43` (`Col1` VARCHAR( 30 ) NOT NULL , `Col2` INT NOT NULL , `Col3` BOOLEAN NOT NULL )") ; 
			
			conn.closeConnection() ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
