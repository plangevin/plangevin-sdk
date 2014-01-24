package com.philippelangevin.sdk.xml;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * This class eases the use of an xml configuration file.
 *
 * It can be used as a singleton, but is not one since you can access the constructors.
 * 
 * @author israel
 *
 */
public class ConfigFileManagementUtility {
	private static ConfigFileManagementUtility m_configFileControllerInstance = null;
	private static final String DEFAULT_FILE_NAME = System.getProperty("user.dir") + "/userPreferences.xml";
	private static final String DEFAULT_ROOT_NODE_NAME = "UserPreferences";
	
	private String m_config_file_name = null;
	private String m_rootName = null;
	
	private Document m_configDom = null;
	
	private XPath m_xpath = null;
	
	private CryptingTool m_cryptingTool = null;
	
	public ConfigFileManagementUtility() {
		this( DEFAULT_FILE_NAME, DEFAULT_ROOT_NODE_NAME );
	}
	
	public ConfigFileManagementUtility( String fullConfigFileName, String rootNodeName ) {
		if( null != fullConfigFileName ) {
			m_config_file_name = fullConfigFileName;
		}
		if( null != rootNodeName ) {
			m_rootName = rootNodeName;
		}
		
		File xmlFile = new File( m_config_file_name );
		
		if( !xmlFile.exists() ) {
			m_configDom = XMLHelper.newDocument( m_rootName, null );
		} else {
			try {
				m_configDom = XMLHelper.readXML( xmlFile );
			} catch (Exception e) {
				m_configDom = XMLHelper.newDocument( m_rootName, null );
			}
		}
		m_xpath = XPathFactory.newInstance().newXPath();
	}
	
	public String getConfigFileName() {
		return m_config_file_name;
	}
	
	public synchronized static ConfigFileManagementUtility getInstance( String configFileName, String rootNodeName ) {
		getInstance();
		if( null != configFileName ) {
			m_configFileControllerInstance.m_config_file_name = configFileName;
		}
		if( null != rootNodeName ) {
			m_configFileControllerInstance.m_rootName = rootNodeName;
		}
		return m_configFileControllerInstance;
	}
	
	public synchronized static ConfigFileManagementUtility getInstance() {
		if( null == m_configFileControllerInstance ) {
			m_configFileControllerInstance = new ConfigFileManagementUtility();
		}
		return m_configFileControllerInstance;
	}
	
	public static void setConfigFileName( String fileName ) {
		if( null == m_configFileControllerInstance ) {
			throw new AssertionError( "Error, you must set the configuration file name before getting an instance of this class.");
		}
		m_configFileControllerInstance.m_config_file_name = fileName;
	}
	
	public static void setRootNodeName( String rootName ) {
		if( null == m_configFileControllerInstance ) {
			throw new AssertionError( "Error, you must set the root node name before getting an instance of this class.");
		}
		m_configFileControllerInstance.m_rootName = rootName;
	}
	
	public Document getDocument() {
		return m_configDom;
	}
	
	/**
	 * @param key
	 * @param val value can be virtualy anything. Anything that can be
	 * processed by toString().
	 */
	public synchronized void setConfigParam( Object key, Object val ) {
		
		String value = (val == null)? null: val.toString();
		String skey = key.toString();
		try {
			Node node = (Node)evaluateXPath( skey, XPathConstants.NODE );
			if( null == node ) {
				addChild( skey, value );
			} else {
				node.setTextContent(value);
			}
		} catch (XPathExpressionException e) {
			System.err.println( "Info dump:\n\tkey == " + skey + "\n\tvalue == " + value );
			e.printStackTrace();
		}
	}
	
	/**
	 * This method creates a new configuration node in the config file.
	 * It is useful for configuration parameters that require sub-nodes.
	 * @param key
	 * @param replaceNode If this is false, we'll return the config node if it already
	 *                    existed in the document (instead of creating a new one).
	 * @return
	 */
	public synchronized Node createConfigParamNode( Object key, boolean replaceNode ) {
		String skey = key.toString();
		try {
			Node oldNode = (Node)evaluateXPath( skey, XPathConstants.NODE );
			if ( null != oldNode ) {
				if (!replaceNode) {
					return oldNode;
				} else {
					oldNode.getParentNode().removeChild(oldNode);
				}
			}
			
			return XMLHelper.addChild(m_configDom, m_configDom.getDocumentElement(), skey, null, true);
		} catch (XPathExpressionException e) {
			System.err.println( "Info dump:\n\tkey == " + skey + "\n\treplaceNode == " + replaceNode );
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized String getConfigParam( Object key ) {
		String skey = key.toString();
		try {
			Node node = (Node)evaluateXPath( skey, XPathConstants.NODE );
			if( null == node ) {
				return null;
			}
			return node.getTextContent();
		} catch ( XPathExpressionException e ) {
			System.err.println( "Info dump:\n\tkey == " + skey );
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This method is useful for configuration parameters that have sub-nodes.
	 * @param key The node we're looking for (has to be just above the root level).
	 * @return
	 */
	public synchronized Node getConfigParamNode( Object key ) {
		String skey = key.toString();
		try {
			return (Node)evaluateXPath( skey, XPathConstants.NODE );
		} catch ( XPathExpressionException e ) {
			System.err.println( "Info dump:\n\tkey == " + skey );
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized String getConfigParam( Object key, Object defaultValue ) {
		String skey = key.toString();
		try {
			Node node = (Node)evaluateXPath( skey, XPathConstants.NODE );
			if( null == node ) {
				addChild( skey, defaultValue );
				return String.valueOf( defaultValue );
			} else {
				return node.getTextContent();
			}
		} catch ( XPathExpressionException e ) {
			System.err.println( "Info dump:\n\tkey == " + skey + "\n\tdefaultValue == " + defaultValue );
			e.printStackTrace();
		}
		return null;
	}
	
	public Object evaluateXPath( String key, QName returnType ) throws XPathExpressionException {
		return m_xpath.evaluate( "/" + m_rootName + "/" + key, m_configDom, returnType );
	}
	
	private synchronized void addChild( String key, Object val ) throws XPathExpressionException {
		String value = (val == null)? null: val.toString();
		Element parent = m_configDom.getDocumentElement();
		String[] elementList = key.split( "/" );
		int i;
		StringBuilder sb = new StringBuilder( 200 );
		for( i = 0; i < elementList.length - 1; i++ ) {
			sb.append( elementList[i] );
			Element parentProspect = ( Element )evaluateXPath( sb.toString(), XPathConstants.NODE );
			if( null == parentProspect ) {
				parent = XMLHelper.addChild( m_configDom, parent, elementList[i], null, true );
			} else {
				parent = parentProspect;
			}
			sb.append( '/' );
		}
		parent = XMLHelper.addChild( m_configDom, parent, elementList[i], value, true );
	}
	
	public synchronized boolean saveConfig() {
		m_configDom.normalize();
		return XMLHelper.saveXMLFile( m_configDom, m_config_file_name );
	}

	/*  _____                             _   _                              _   _               _
	 * | ____|_ __   ___ _ __ _   _ _ __ | |_(_) ___  _ __    _ __ ___   ___| |_| |__   ___   __| |___
	 * |  _| | '_ \ / __| '__| | | | '_ \| __| |/ _ \| '_ \  | '_ ` _ \ / _ \ __| '_ \ / _ \ / _` / __|
	 * | |___| | | | (__| |  | |_| | |_) | |_| | (_) | | | | | | | | | |  __/ |_| | | | (_) | (_| \__ \
	 * |_____|_| |_|\___|_|   \__, | .__/ \__|_|\___/|_| |_| |_| |_| |_|\___|\__|_| |_|\___/ \__,_|___/
	 *                        |___/|_|
	 * Pros and cons to embed encryption into this class
	 * Pros:
	 *  - dead simple to use
	 *  - absolutely no dependencies added to this class (true for CTec dependencies and external jar dependencies)
	 *  - this advantage is true only against the current version of EncryptionHelper... can specify you own encryption key.
	 * Cons:
	 *  - some code is kind of repeated since EncryptionHelper existed prior to this
	 *  - kind of heavier IF someone uses this class and EncryptionHelper
	 *  - EncryptionHelper is currently safer (harder to decrypt)
	 * Conclusion:
	 *  I didn't want to break make files by adding dependencies and it is truly dead symple to use like this...
	 */
	
	public synchronized void setConfigParamEncryption( Object key, Object value, String encryptionKey ) {
		setConfigParam(
				String.valueOf( key ),
				encrypt( null == value ? null:String.valueOf( value ), encryptionKey )
		);
	}
	
	public String encrypt( String value, String encryptionKey ) {
		try {
			if( null == m_cryptingTool ) {
				m_cryptingTool = new CryptingTool();
			}
			return null == value ? null:asHex( m_cryptingTool.encrypt( value.getBytes(), encryptionKey ) );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized String getConfigParamEncryption( Object key, String encryptionKey ) {
		String hexEncValue = getConfigParam( key );
		return decrypt( hexEncValue, encryptionKey );
	}
	
	public String decrypt( String value, String encryptionKey ) {
		try {
			if( null == m_cryptingTool ) {
				m_cryptingTool = new CryptingTool();
			}
			return null == value ? null:new String( m_cryptingTool.decrypt( asBytes( value ), encryptionKey ) );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static byte[] asBytes( String sHex ) {
		if( 1 == sHex.length() % 2 ) {
			System.err.println( "The xml value is not a valid encrypted value");
		}

		byte[] ba = new byte[sHex.length() / 2];
		for( int i = 0; i < sHex.length() / 2; i++ ) {
			ba[i] = (Integer.decode(
					"0x" + sHex.substring( i * 2, (i + 1) * 2 ) )).byteValue();
		}
		return ba;
	}

	private static String asHex( byte buf[] ) {
		StringBuffer strbuf = new StringBuffer( buf.length * 2 );
		int i;

		for( i = 0; i < buf.length; i++ ) {
			if( (buf[i] & 0xff) < 0x10 ) {
				strbuf.append( "0" );
			}

			strbuf.append( Long.toString( buf[i] & 0xff, 16 ) );
		}

		return strbuf.toString();
	}

	private static class CryptingTool {
		private String				m_key			= null;
		private SecretKey			m_skey			= null;
		private Cipher				m_cipher		= null;
		private PBEParameterSpec	m_pbeParamSpec	= null;
		byte[]						m_salt			= {
															(byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
															(byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99
													};

		public CryptingTool() throws NoSuchAlgorithmException, NoSuchPaddingException {
			m_cipher = Cipher.getInstance( "PBEWithMD5AndDES" );
			m_pbeParamSpec = new PBEParameterSpec( m_salt, 20 );
		}

		private void manageKey( String key ) throws NoSuchAlgorithmException, InvalidKeySpecException {
			if( null == key ) {
				key = "»°4+2#_TtoO8( ";
			}
			if( null == m_skey || !key.equals( m_key ) ) {
				m_key = key;
				PBEKeySpec pbeKeySpec;
				SecretKeyFactory keyFac;
				pbeKeySpec = new PBEKeySpec( m_key.toCharArray() );
				keyFac = SecretKeyFactory.getInstance( "PBEWithMD5AndDES" );
				m_skey = keyFac.generateSecret( pbeKeySpec );
			}
		}

		public byte[] encrypt( byte[] dataToEncrypt, String key )
				throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
			manageKey( key );
			m_cipher.init( Cipher.ENCRYPT_MODE, m_skey, m_pbeParamSpec );
			return m_cipher.doFinal( dataToEncrypt );
		}

		public byte[] decrypt( byte[] dataToDecrypt, String key )
				throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
			manageKey( key );
			m_cipher.init( Cipher.DECRYPT_MODE, m_skey, m_pbeParamSpec );
			return m_cipher.doFinal( dataToDecrypt );
		}
	}
}
