package com.philippelangevin.sdk.database.connector;

public class MySQLConnector implements RemoteDatabaseConnectorIF {
	private static final String DRIVER_MYSQL_CLASS = "com.mysql.jdbc.Driver" ;
	private static final String PROTOCOL = "jdbc:mysql://";
	private static final String BASE_URL = PROTOCOL + "%s:%d/%s";
	
	private String serverName = null;
	private int port = -1;
	private String databaseName = null;

	public MySQLConnector(String serverName, int port, String databaseName)	{
		setConnectorInfo(serverName, port, databaseName) ;
	}
	
	@Override
	public void setConnectorInfo(String serverName, int port, String databaseName) {
		this.serverName = serverName;
		this.port = port;
		this.databaseName = databaseName;
	}

	@Override
	public String getClassForName() {
		return DRIVER_MYSQL_CLASS ;
	}

	@Override
	public String getConnectionString() {
		return String.format(BASE_URL, serverName, port, databaseName) ;
	}

	@Override
	public void setUseCursors(boolean useCursors) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isCaseSensitive() {
		return true;
	}

	@Override
	public boolean isReturningSupported() {
		return false;
	}
}
