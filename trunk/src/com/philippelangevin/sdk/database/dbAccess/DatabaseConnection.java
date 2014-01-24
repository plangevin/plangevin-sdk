/**
 * Abstract class that gives abstract DataBase services
 * (query, connection, etc.)<BR>
 * 
 * @author fostiguy<BR>
 * Copyright:
 * (c) 2008, C-Tec Inc. - All rights reserved
 */

/*
 * History
 * Date			Name		BT	Description
 * 2008-05-07	FOstiguy	18	Initial version
 * 
 */

package com.philippelangevin.sdk.database.dbAccess;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.philippelangevin.sdk.database.connector.DatabaseConnectorIF;

public abstract class DatabaseConnection {
	
	protected Connection dbConnection = null;
	
	protected String password = null;
	protected String userName = null;
	
	protected DatabaseConnectorIF dbConnector = null;
	
	private byte[] m_saveAsFilebuf = null;
	
	public DatabaseConnection() {
		this.password = null;
		this.userName = null;
	}
	
//	@Deprecated
//	public abstract void setConnection(String userName, String password,
//			String serverName, String dbName);
	
	public abstract void setConnection(DatabaseConnectorIF dbConnector, String userName, String password);
	
	/**
	 * Closes database connection
	 */
	public abstract void closeConnection();

	/**
	 * Permet d'ex�cuter une requ�te sur la connexion active � la base de
	 * donn�es. Cette m�thode retourne un ResultSet qui contient les r�sultats
	 * de la requ�te. Le ResultSet doit �tre ferm� avec la m�thode close() apr�s
	 * utilisation. Si une erreur survient, la m�thode retourne null.
	 * 
	 * @param sqlStatement	Requ�te à ex�cuter
	 * @throws SQLException
	 * @return ResultSet Resultat de la requ�te
	 */
	public abstract ResultSet executeQuery( String sqlStatement ) throws SQLException;
	
	/**
	 * Permet d'ex�cuter une requ�te et d'obtenir un ResultSet qui supporte les
	 * UpdateRow Le ResultSet doit �tre ferm� avec la m�thode
	 * getStatement().close() apr�s utilisation. Si une erreur survient, la
	 * m�thode retourne null.
	 * 
	 * @param sqlStatement	Requ�te � ex�cuter
	 * @throws SQLException
	 * @return ResultSet Resultat de la requ�te
	 */
	public abstract ResultSet executeUpdatableQuery( String sqlStatement )
			throws SQLException;

	/**
	 * Permet d'exécuter un Update,Insert ou Delete sur la connexion active à
	 * la base de données.
	 * 
	 * @param sqlStatement
	 *            Update à exécuter
	 * @return int Nombre d'enregistrement mis à jour
	 * @throws SQLException
	 */
	public abstract int executeUpdate( String sqlStatement ) throws SQLException;
	
	/**
	 * Permet d'obtenir un fichier binaire de la base de données et de le
	 * sauvegarder sur le disque.
	 * @param cmd
	 * @param fileOut
	 * @param binaryColumnName
	 * @throws SQLException
	 * @throws IOException
	 */
	public boolean getBinaryFile( String cmd, File fileOut, String binaryColumnName )
			throws SQLException, IOException {
		ResultSet rs = null;
		try {
			rs = executeQuery( cmd );
			return getBinaryFile( rs, fileOut, binaryColumnName );
		} catch( SQLException sqlEx ) {
			throw new SQLException( "getBinaryFile SQL Command : " + cmd + "\n"
					+ sqlEx.getMessage() );
		} finally {
			rs.getStatement().close();
			if( fileOut.length() <= 0 ) {
				fileOut.delete();
			}
		}
	}
	
	/**
	 * Permet d'obtenir un fichier binaire de la base de données et de le
	 * sauvegarder sur le disque.
	 * @param rs resultSet containing the binary field to recover
	 * @param fileOut
	 * @param binaryColumnName
	 * @throws SQLException
	 * @throws IOException
	 */
	public boolean getBinaryFile( ResultSet rs, File fileOut,
			String binaryColumnName ) throws SQLException, IOException {
		try {
			return saveAsFile( rs.getBinaryStream( binaryColumnName ), fileOut );
		} catch( SQLException sqlEx ) {
			throw new SQLException( "getBinaryFile with ResultSet\n"
					+ sqlEx.getMessage() );
		} finally {
			if( fileOut.length() <= 0 ) {
				fileOut.delete();
			}
		}
	}
	
	/**
	 * Permet d'obtenir les fichiers binaires contenus dans la colonne
	 * binaryColumnName du ResultSet rs résultant de la commande cmd et de les
	 * sauvegarder sur le disque.
	 * @param cmd
	 * @param binaryColumnName
	 * @param path
	 * @param fileName
	 * @param fileExtension
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public int getBinaryFiles( String cmd, String binaryColumnName,
			String path, String fileName, String fileExtention )
			throws SQLException, IOException {
		ResultSet rs = null;
		int nbFiles = 0;
		try {
			rs = executeQuery( cmd );
			nbFiles = getBinaryFiles( rs, binaryColumnName, path, fileName,
					fileExtention );
			rs.getStatement().close();
			return nbFiles;
		} catch( SQLException sqlEx ) {
			throw new SQLException( "getBinaryFile SQL Command : " + cmd + "\n"
					+ sqlEx.getMessage() );
		}
	}
	
	/**
	 * Permet d'obtenir les fichiers binaires contenus dans la colonne
	 * binaryColumnName du ResultSet rs et de les sauvegarder sur le disque.
	 * @param rs resultSet containing the binary field to recover
	 * @param binaryColumnName
	 * @param path
	 * @param fileName
	 * @param fileExtension
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public int getBinaryFiles( ResultSet rs, String binaryColumnName,
			String path, String fileName, String fileExtention )
			throws SQLException, IOException {
		int i = 0;
		if( path.charAt( path.length() - 1 ) != File.separatorChar ) {
			path += File.separatorChar;
		}
		if( fileExtention.charAt( 0 ) != '.' ) {
			fileExtention = '.' + fileExtention;
		}
		try {
			while( rs.next() ) {
				if( saveAsFile( rs.getBinaryStream( binaryColumnName ), new File(
						path + fileName + (i + 1) + fileExtention ) ) ){
					i++;
				}
			}
			return i;
		} catch( SQLException sqlEx ) {
			throw new SQLException( "getBinaryFile with ResultSet\n"
					+ sqlEx.getMessage() );
		}
	}
	
	/**
	 * Permet de sauvegarder un flux d'entrée sur
	 * disque.
	 * @param is
	 * @param fileOut
	 * @throws IOException
	 */
	private boolean saveAsFile( InputStream is, File fileOut ) throws
			IOException {
		BufferedInputStream bis = null;
		FileOutputStream out = null;
		int len;

		if( null == is || null == fileOut ){
			return false;
		}
		if( null == m_saveAsFilebuf ) {
			m_saveAsFilebuf = new byte[4 * 1024];
		}

		try {
			bis = new BufferedInputStream( is );
			out = new FileOutputStream( fileOut );

			while( (len = is.read( m_saveAsFilebuf, 0, m_saveAsFilebuf.length )) != -1 ) {
				out.write( m_saveAsFilebuf, 0, len );
			}
		} finally {
			try {
				bis.close();
			} catch( Throwable e ) {}
			
			try {
				out.close();
			} catch( Throwable e ) {}
			
			if( fileOut.length() <= 0 ) {
				fileOut.delete();
			}
		}
		return true;
	}
	
	protected abstract Connection getConnection() throws SQLException;
	
	public abstract boolean isClosed() throws SQLException;

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
	public abstract boolean openConnection() throws SQLException;

	/**
	 * Permet de préparer un statement sur la connection active de la base de
	 * données
	 * 
	 * @param sqlStatement
	 *            Statement à préparer
	 * @return Le prepareStatement
	 * @throws SQLException
	 */
	public abstract PreparedStatement prepareStatement( String sqlStatement )
			throws SQLException;
	
	public PreparedStatement prepareStatement(StringBuilder sqlStatement) throws SQLException {
		return prepareStatement(sqlStatement.toString());
	}

	/**
	 * Permet de uploader un fichier binaire dans un champ de la base de données.
	 * @param cmd pour PrepareStatement
	 * @param fileIn
	 * @return Nombre de colone mise à jour
	 * @throws SQLException
	 * @throws FileNotFoundException
	 */
	public abstract int updateBinaryFile( String cmd, File fileIn ) throws SQLException,
			FileNotFoundException;
	
	/**
	 * Fonction qui permet de tester la connexion sur la base
	 * de données
	 * @param usePersistentConnection Valeur booléenne indiquant si on persiste à se reconnecter
	 * @return Connexion présente ou non
	 */
	public abstract boolean isConnectionAlive(boolean usePersistentConnection);

	public abstract void setAutoCommit( boolean autoCommit ) throws SQLException;
	
	public abstract void commit() throws SQLException;
	
	/**
	 * Returns whether this connection is case sensitive or not.
	 * Will throw NullPointerException if the connector is not set.
	 * @return
	 */
	public boolean isCaseSensitive() {
		return dbConnector.isCaseSensitive();
	}
	
	public void setReadOnly(boolean readOnly) throws SQLException {
		dbConnection.setReadOnly(readOnly);
	}
	
	public boolean isReadOnly() throws SQLException {
		return dbConnection.isReadOnly();
	}
	
	public abstract void deferConstraints();
	
	public abstract void undeferConstraints();
	
	public boolean isReturningSupported() {
		return dbConnector.isReturningSupported();
	}
	
}
