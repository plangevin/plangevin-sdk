package com.philippelangevin.sdk.database.connector;

public interface DatabaseDefinitionIF {
	public String getName() ;
	public DBType getDBType() ;
	public String getLocation() ;
}
