package com.philippelangevin.sdk.database.connector;

public class SQLServerMicrosoftConnector implements RemoteDatabaseConnectorIF{

	private static final String DRIVER_MS_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String CURSOR_STR = ";selectMethod=cursor";
	private static final String BASE_URL = "jdbc:sqlserver://%s:%d;databaseName=%s";
	
	private String serverName = null;
	private int port = -1;
	private String databaseName = null;
	private boolean useCursors = true;
	
	public SQLServerMicrosoftConnector(){ /* do nothing */ }
	
	public SQLServerMicrosoftConnector( String serverName, int port,
			String databaseName ) {
		setConnectorInfo( serverName, port, databaseName );
	}
	
	@Override
	public void setConnectorInfo(String serverName, int port,
			String databaseName) {
		this.serverName = serverName;
		this.port = port;
		this.databaseName = databaseName;
	}
	
	@Override
	public String getClassForName() {
		return DRIVER_MS_CLASS;
	}

	@Override
	public String getConnectionString() {
		String connectionUrl = String.format(BASE_URL,serverName,port,databaseName);
		if(useCursors){
			connectionUrl += CURSOR_STR;
		}
		connectionUrl += ";";
		return connectionUrl;
	}

	@Override
	public void setUseCursors(boolean useCursors) {
		this.useCursors = useCursors;
	}

	@Override
	public boolean isCaseSensitive() {
		return false;
	}
	
	@Override
	public boolean isReturningSupported() {
		return false;
	}
}
