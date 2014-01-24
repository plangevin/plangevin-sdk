package com.philippelangevin.sdk.database.connector;

import java.io.File;

public class SQLiteConnector implements LocalDatabaseConnectorIF {

	private static final String DRIVER_JTDS_CLASS = "org.sqlite.JDBC";
	private static final String BASE_URL = "jdbc:sqlite:%s";
	
	private File databaseFile ;
	
	public SQLiteConnector(String databaseRootDirectory, String dbName)	{
		setConnectorInfo(new File(databaseRootDirectory, dbName)) ;
	}
	
	@Override
	public void setConnectorInfo(File databaseFile) {
		this.databaseFile = databaseFile.getAbsoluteFile() ;
	}

	@Override
	public String getClassForName() {
		return DRIVER_JTDS_CLASS ;
	}

	@Override
	public String getConnectionString() {
		return String.format(BASE_URL, databaseFile.getAbsolutePath()) ;
	}

	@Override
	public void setUseCursors(boolean useCursors) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isCaseSensitive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReturningSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	public File getDatabaseFile()	{
		return databaseFile ;
	}
}
