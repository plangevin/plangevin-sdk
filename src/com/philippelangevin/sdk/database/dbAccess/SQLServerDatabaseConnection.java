/**
 * Don't directly use this class.
 * Instead use CTecDBAccess. Go see on the Wiki
 * Database connector.
 * 
 * @author cgendreau, fostiguy<BR>
 * Copyright:
 * (c) 2007,2008, C-Tec Inc. - All rights reserved
 */

/*
 * History
 * Date			Name		BT	Description
 * 2007-07-03	CGendreau		Initial version
 * 
 * 2008-05-07	FOstiguy	18	The class now extends DataBaseConnector
 * 
 */

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

public class SQLServerDatabaseConnection extends DatabaseConnection {

//	/**
//	 * Use Constructor with DatabaseConnectorIF instead of this one.
//	 * @param userName
//	 * @param password
//	 * @param serverName
//	 * @param dbName
//	 */
//	@Deprecated
//	public SQLServerDatabaseConnection( String userName, String password,
//			String serverName, String dbName ) {
//		setConnection( userName, password, serverName, dbName );
//	}
	public SQLServerDatabaseConnection(DatabaseConnectorIF dbConnector, String userName , String password){
		setConnection( dbConnector, userName, password );
	}
	
//	/**
//	 * Use setConnection with DatabaseConnectorIF instead of this one.
//	 */
//	@Deprecated
//	public void setConnection( String userName, String password,
//			String serverURL, String dbName ) {
//		this.userName = userName;
//		this.password = password;
//		this.dbConnector = DatabaseConnectorFactory.getDefaultMSSQLDBConnector( dbName );
//	}
	
	@Override
	public void setConnection(DatabaseConnectorIF dbConnector, String userName, String password) {
		this.dbConnector = dbConnector;
		this.userName = userName;
		this.password = password;
	}
	
	/**
	 * Permet de fermer la connexion à la base de données
	 */
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

	/**
	 * Permet d'exécuter une requête sur la connexion active à la base de
	 * données. Cette méthode retourne un ResultSet qui contient les résultats
	 * de la requête. Le ResultSet doit être fermé avec la méthode close() après
	 * utilisation. Si une erreur survient, la méthode retourne null.
	 * 
	 * @param sqlStatement
	 *            Requête à exécuter
	 * @throws SQLException
	 * @return ResultSet Resultat de la requête
	 */
	@Override
	public ResultSet executeQuery( String sqlStatement ) throws SQLException {
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

	/**
	 * Permet d'exécuter une requête et d'obtenir un ResultSet qui supporte les
	 * UpdateRow Le ResultSet doit être fermé avec la méthode
	 * getStatement().close() après utilisation. Si une erreur survient, la
	 * méthode retourne null.
	 * 
	 * @param sqlStatement
	 *            Requête à exécuter
	 * @throws SQLException
	 * @return ResultSet Resultat de la requête
	 */
	@Override
	public ResultSet executeUpdatableQuery( String sqlStatement )
			throws SQLException {
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

	/**
	 * Permet d'exécuter un Update,Insert ou Delete sur la connection active à
	 * la base de données.
	 * 
	 * @param sqlStatement
	 *            Update à exécuter
	 * @return int Nombre d'enregistrement mis à jour
	 * @throws SQLException
	 */
	@Override
	public int executeUpdate( String sqlStatement ) throws SQLException {
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
		try {
			if( null == dbConnection ) {
				Class.forName( dbConnector.getClassForName() );
				dbConnection = DriverManager.getConnection( dbConnector.getConnectionString(),
						userName, password );
			}
		} catch( ClassNotFoundException e ) {
			e.printStackTrace();
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

	/**
	 * Permet de se connecter à la base de données.
	 * 
	 * @param userName
	 *            Nom d'utilisateur sur la base de données
	 * @param password
	 *            Mot de passe sur la base de données
	 * @return Succès ou échec
	 * @throws SQLException
	 */
	@Override
	public boolean openConnection() throws SQLException {
		if( getConnection() == null ) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Permet de préparer un statement sur la connection active de la base de
	 * données
	 * 
	 * @param sqlStatement
	 *            Statement à préparer
	 * @return Le prepareStatement
	 * @throws SQLException
	 */
	@Override
	public PreparedStatement prepareStatement( String sqlStatement )
			throws SQLException {
		return dbConnection.prepareStatement( sqlStatement );
	}

	/**
	 * Permet de uploader un fichier binaire dans un champ de la base de données.
	 * @param cmd pour PrepareStatement
	 * @param fileIn
	 * @return Nombre de colone mise à jour
	 * @throws SQLException
	 * @throws FileNotFoundException
	 */
	@Override
	public int updateBinaryFile( String cmd, File fileIn ) throws SQLException,
			FileNotFoundException {
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
	
	/**
	 * Fonction qui permet de tester la connexion sur la base
	 * de données
	 * @param usePersistentConnection Valeur booléenne indiquant si on persiste à se reconnecter
	 * @return Connexion présente ou non
	 */
	@Override
	public boolean isConnectionAlive(boolean usePersistentConnection) {
		boolean isConnectionAlive = true;
		
		try {
			if(!isClosed() ) {
				String sqlQuery = "SELECT GETDATE()";
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
	public void setAutoCommit( boolean autoCommit ) throws SQLException {
		dbConnection.setAutoCommit(autoCommit);
	}
	
	@Override
	public void commit() throws SQLException {
		dbConnection.commit();
	}

	@Override
	public void deferConstraints() {
		throw new UnsupportedOperationException("Deferred Constraints are not supported by SQLServer");
	}

	@Override
	public void undeferConstraints() {
		throw new UnsupportedOperationException("Deferred Constraints are not supported by SQLServer");
	}
	
}
