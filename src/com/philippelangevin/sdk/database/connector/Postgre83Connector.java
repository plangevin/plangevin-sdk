package com.philippelangevin.sdk.database.connector;

public class Postgre83Connector implements RemoteDatabaseConnectorIF{
	
	private static final String DRIVER_POSTGRE_CLASS = "org.postgresql.Driver";
	private static final String PROTOCOL = "jdbc:postgresql://";
	private static final String BASE_URL = PROTOCOL + "%s:%d/%s";
	
	private String serverName = null;
	private int port = -1;
	private String databaseName = null;
	
	public Postgre83Connector(){ /* do nothing */ }
	
	public Postgre83Connector( String serverName, int port, String databaseName ) {
		setConnectorInfo( serverName, port, databaseName );
	}
	
	public Postgre83Connector(String url) {
		String[] params = url.substring(PROTOCOL.length()).split(":|/");
		setConnectorInfo(params[0], Integer.parseInt(params[1]), params[2]);
	}
	
	@Override
	public void setConnectorInfo(String serverName, int port, String databaseName) {
		this.serverName = serverName;
		this.port = port;
		this.databaseName = databaseName;
	}

	@Override
	public String getClassForName() {
		return DRIVER_POSTGRE_CLASS;
	}

	@Override
	public String getConnectionString() {
		return String.format(BASE_URL, serverName, port, databaseName);
	}

	/**
	 * do nothing for the moment
	 */
	@Override
	public void setUseCursors(boolean useCursors) {
	}

	@Override
	public boolean isCaseSensitive() {
		return true;
	}
	
	@Override
	public boolean isReturningSupported() {
		return true;
	}
}
