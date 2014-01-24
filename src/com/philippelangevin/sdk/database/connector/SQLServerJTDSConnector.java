package com.philippelangevin.sdk.database.connector;

public class SQLServerJTDSConnector implements RemoteDatabaseConnectorIF{

	private static final String DRIVER_JTDS_CLASS = "net.sourceforge.jtds.jdbc.Driver";
	private static String CURSOR_STR = ";useCursors=%s";
	private static final String BASE_URL = "jdbc:jtds:sqlserver://%s:%d/%s";
	
	private String serverName = null;
	private int port = -1;
	private String databaseName = null;
	private boolean useCursors = true;
	
	public SQLServerJTDSConnector(){ /* do nothing */ }
	
	public SQLServerJTDSConnector( String serverName, int port, String databaseName ) {
		setConnectorInfo( serverName, port, databaseName );
	}
	
	@Override
	public void setConnectorInfo(String serverName, int port, String databaseName) {
		this.serverName = serverName;
		this.port = port;
		this.databaseName = databaseName;
	}

	@Override
	public String getClassForName() {
		return DRIVER_JTDS_CLASS;
	}

	@Override
	public void setUseCursors(boolean useCursors) {
		this.useCursors = useCursors;
	}

	@Override
	public String getConnectionString() {
		String connectionUrl = String.format(BASE_URL+CURSOR_STR,
				serverName,port,databaseName,useCursors);
		return connectionUrl;
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
