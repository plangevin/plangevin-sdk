package com.philippelangevin.sdk.database.dbAccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.philippelangevin.sdk.database.connector.DatabaseConnectorIF;

public class SQLiteDatabaseConnection extends DatabaseConnection {

	public SQLiteDatabaseConnection( DatabaseConnectorIF dbConnector ){
		setConnection(dbConnector, null, null) ;
	}
	
	@Override
	public void setConnection(DatabaseConnectorIF dbConnector, String userName,	String password) {
		this.dbConnector = dbConnector ;
		this.userName = userName ;
		this.password = password ;
	}

	@Override
	public void closeConnection() {
		try {
			if( dbConnection != null ) {
				dbConnection.close();
				dbConnection = null;
			}
		} catch( SQLException e ) {
			e.printStackTrace();
		}
	}

	@Override
	public ResultSet executeQuery(String sqlStatement) throws SQLException {
		ResultSet rs = null;
		Statement stmt = null;

		if( dbConnection == null ) {
			throw new SQLException( "No database connection" );
		}
		
		stmt = dbConnection.createStatement();
		if(stmt != null) {
			rs = stmt.executeQuery(sqlStatement);
		}
		
		return rs;
	}

	@Override
	public ResultSet executeUpdatableQuery(String sqlStatement)	throws SQLException {
		ResultSet rs = null;
		Statement stmtUpdate = null;

		if( dbConnection == null ) {
			throw new SQLException( "No database connection" );
		}
		stmtUpdate = dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
		if (stmtUpdate != null) {
			try {
				rs = stmtUpdate.executeQuery(sqlStatement);
			} finally {
				try {
					//we cannot close the Statement because we return the ResultSet
					//stmtUpdate.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return rs;
	}

	@Override
	public int executeUpdate(String sqlStatement) throws SQLException {
		int updatedRow = -1;
		Statement stmt = null;

		if( dbConnection == null ) {
			throw new SQLException( "No database connection" );
		}
		try {
			stmt = dbConnection.createStatement();
			try {
				updatedRow = stmt.executeUpdate(sqlStatement);
			} finally {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch( SQLException sqlE ) {
			throw new SQLException( "executeUpdate SQL Command : "
					+ sqlStatement + "\n" + sqlE.getMessage() );
		}
		
		return updatedRow;
	}

	@Override
	protected Connection getConnection() throws SQLException {
		if( null == dbConnection ) {
			try {
				Class.forName( dbConnector.getClassForName() );
				dbConnection = DriverManager.getConnection( dbConnector.getConnectionString() );
			} catch( ClassNotFoundException e ) {
				e.printStackTrace();
			}
		}
		
		return dbConnection;
	}

	@Override
	public boolean isClosed() throws SQLException {
		if( dbConnection == null ) {
			return true;
		}
		return dbConnection.isClosed();
	}

	@Override
	public boolean openConnection() throws SQLException {
		if( getConnection() == null ) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public PreparedStatement prepareStatement(String sqlStatement)	throws SQLException {
		return getConnection().prepareStatement( sqlStatement );
	}

	@Override
	public int updateBinaryFile(String cmd, File fileIn) throws SQLException, FileNotFoundException {
		int rowUpdated = -1;
		
		PreparedStatement ps = dbConnection.prepareStatement( cmd );
		if (ps != null) {
			try {
				int fileLength = (int) fileIn.length();
				InputStream streamedJpg = new FileInputStream(fileIn);
				ps.setBinaryStream(1, streamedJpg, fileLength);
				rowUpdated = ps.executeUpdate();
			} finally {
				try {
					ps.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return rowUpdated;
	}

	@Override
	public boolean isConnectionAlive(boolean usePersistentConnection) {
		boolean isConnectionAlive = true;
		
		try {
			if(!isClosed() ) {
				String sqlQuery = "SELECT date('now')";
				ResultSet rs = null;
				
				try {
					if(!usePersistentConnection){
						openConnection();
					}
					
					rs = executeQuery( sqlQuery );
					rs.next();
					rs.getStatement().close();
				} catch( SQLException sqlEx ) {
					isConnectionAlive = false;
				}
				finally	{
					/*
					 * Close connection if it was open
					 */
					if (!usePersistentConnection){
						closeConnection() ;
					}
				}
			} else {
				isConnectionAlive = false;
			}
		} catch( SQLException e ) {
			e.printStackTrace();
		}
		
		return isConnectionAlive;
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		dbConnection.setAutoCommit(autoCommit);
	}

	@Override
	public void commit() throws SQLException {
		dbConnection.commit() ;
	}

	@Override
	public void deferConstraints() {
		throw new UnsupportedOperationException("Cannot defer constraints for SQLite DB") ;
	}

	@Override
	public void undeferConstraints() {
		throw new UnsupportedOperationException("Cannot undefer constraints for SQLite DB") ;
	}
}
