package com.philippelangevin.sdk.database.connector;

public interface RemoteDatabaseConnectorIF extends DatabaseConnectorIF {
	public void setConnectorInfo(String serverName, int port, String databaseName) ;
}
