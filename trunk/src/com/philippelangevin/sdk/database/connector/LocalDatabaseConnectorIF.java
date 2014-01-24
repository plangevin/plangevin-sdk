package com.philippelangevin.sdk.database.connector;

import java.io.File;

public interface LocalDatabaseConnectorIF extends DatabaseConnectorIF {
	public void setConnectorInfo(File databaseName) ;
}
