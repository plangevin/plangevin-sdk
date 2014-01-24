package com.philippelangevin.sdk.database.connector;

public interface DatabaseConnectorIF {
	public String getClassForName();
	public String getConnectionString();
	
	/**
	 * Informs the driver to use server a side-cursor, which permits more than
	 * one active statement on a connection.
	 * @param useCursors
	 */
	public void setUseCursors(boolean useCursors);
	
	public boolean isCaseSensitive();
	
	public boolean isReturningSupported();
}
