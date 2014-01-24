package com.philippelangevin.sdk.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class holds an XML file and makes possible to make XPath requests on it.
 * To do so, it uses a DOM representation of the XML file.
 *
 * Use sample:
 * 
 *  <?xml version='1.0' encoding='utf-8'?>
 *  <project 
 *      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *      xmlns="http://www.colmatec.com/project"
 *      xsi:schemaLocation="http://www.colmatec.com/project project.xsd"
 *      lang="EN">
 *    <projectID>A01-045-748</projectID>
 *  </project>
 * 
 * This xml file sample uses the "http://www.colmatec.com/project" name 
 * space (see the xmlns tag). To be able to make any xpath requests on it,
 * you need to create a string table this way:
 * 
 *  String nsMap[][] = new String[1][2];
 *  nsMap[0][0] = "defaultNS";
 *  nsMap[0][1] = "http://www.colmatec.com/project";
 * 	test = new XPathRequester( filePath, XPathRequester.SCHEMA_VALIDATION, nsMap );
 * 
 * This way, when you can evaluate an xpath request this way:
 * 
 * 	NodeList nodeList = test.evaluate( "/defaultNS:project/child::*" );
 * 	TreeMap<String, ArrayList<String>> resultSet = test.getLeaves( nodeList );
 * 
 * And you can print the results this way:
 * 
 * 	element = resultSet.pollFirstEntry();
 * 	while( null != element ) {
 * 		System.out.print( element.getKey() + ": " );
 * 		for( int i = 0; i < element.getValue().size(); i++ ) {
 * 			System.out.print( element.getValue().get( i ) + " " );
 * 		}
 * 		System.out.println();
 * 		element = resultSet.pollFirstEntry();
 * 	}
 * 
 * or by simply calling one of the print(...) functions
 *  
 * @author ipainchaud
 */
/*
 * History
 * Date		Name		BT	Description
 * 06-11-16	IPainchaud		initial revision
 */
public class XPathRequester {

	/**
	 * available xml file validations
	 */
	public static final int NO_VALIDATION = 0;

	public static final int SCHEMA_VALIDATION = 1;

	/**
	 * variable declarations
	 */
	public Document m_dom = null;

	public XPath m_xpath = null;

	/**
	 * Creates an XPathRequester with an existing dom.
	 * See above sample for more details.
	 * @param dom org.w3c.dom.Document containing the xml file to process
	 * @param nsMap table containing all the name spaces used in the xml file 
	 * with the prefixes you want to assiciate with it. Null if the xml file
	 * uses no name space.
	 */
	public XPathRequester( Document dom, String[][] nsMap ) {
		m_dom = dom;

		m_xpath = XMLHelper.newXPath( nsMap );
	}

	/**
	 * Creates an XPathRequester with an existing File.
	 * See above sample for more details.
	 * @param xmlFile file containing the xml to process
	 * @param validation indicates the type of xml file validation to use. See 
	 * the _VALIDATION variables for possible values
	 * @param nsMap table containing all the name spaces used in the xml file 
	 * with the prefixes you want to assignate with it. Null if the xml file
	 * uses no name space.
	 */
	public XPathRequester( File xmlFile, int validation, String[][] nsMap ) {
		initDOM( xmlFile, validation );
		m_xpath = XMLHelper.newXPath( nsMap );
	}

	/**
	 * Creates an XPathRequester from scratch.
	 * See above sample for more details.
	 * @param fileURI URI to the xml file to process
	 * @param validation indicates the type of xml file validation to use. See 
	 * the _VALIDATION variables for possible values
	 * @param nsMap table containing all the name spaces used in the xml file 
	 * with the prefixes you want to assiciate with it. Null if the xml file
	 * uses no name space.
	 * @throws FileNotFoundException
	 */
	public XPathRequester( URI fileURI, int validation, String[][] nsMap )
			throws FileNotFoundException {
		File xmlFile = null;

		xmlFile = new File( fileURI );
		if( !xmlFile.exists() ) {
			throw new FileNotFoundException( "File: " + xmlFile.getPath()
					+ " doesn't exist" );
		}

		initDOM( xmlFile, validation );
		m_xpath = XMLHelper.newXPath( nsMap );
	}

	/**
	 * Creates an XPathRequester from scratch.
	 * See above sample for more details.
	 * @param filePath path to the xml file to process
	 * @param validation indicates the type of xml file validation to use. See 
	 * the _VALIDATION variables for possible values
	 * @param nsMap table containing all the name spaces used in the xml file 
	 * with the prefixes you want to assiciate with it. Null if the xml file
	 * uses no name space.
	 * @throws FileNotFoundException
	 */
	public XPathRequester( String filePath, int validation, String[][] nsMap )
			throws FileNotFoundException {
		File xmlFile = null;

		xmlFile = new File( filePath );
		if( !xmlFile.exists() ) {
			throw new FileNotFoundException( "File: " + xmlFile.getPath()
					+ " doesn't exist" );
		}

		initDOM( xmlFile, validation );
		m_xpath = XMLHelper.newXPath( nsMap );
	}

	/**
	 * @param xmlFile
	 * @param validation
	 */
	private void initDOM( File xmlFile, int validation ) {
		//			System.out.println( "Creating DOM" );
		DocumentBuilderFactory domFac = DocumentBuilderFactory.newInstance();
		// no need to specify a schema with setShema since it is specifyed in the xml file
		//			domFac.setSchema( schema );
		domFac.setNamespaceAware( true );
		if( SCHEMA_VALIDATION == validation ) {
			domFac.setValidating( true );
			domFac.setAttribute(
					"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema" );
		}
		try {
			DocumentBuilder domBuilder = domFac.newDocumentBuilder();
			// ouah! we don't need this for now! This is supposed to handle 
			// parsing error, but to complicated to do something nice in less
			// than 15 min...
			//			domBuilder.setErrorHandler( new ErrorHandler() {
			//
			//				public void error( SAXParseException arg0 ) throws SAXException {
			//					arg0.printStackTrace();
			//				}
			//
			//				public void fatalError( SAXParseException arg0 )
			//						throws SAXException {
			//					arg0.printStackTrace();
			//				}
			//
			//				public void warning( SAXParseException arg0 )
			//						throws SAXException {
			//					arg0.printStackTrace();
			//				}
			//			} );
			m_dom = domBuilder.parse( xmlFile );
		} catch( ParserConfigurationException e ) {
			e.printStackTrace();
		} catch( SAXException e ) {
			e.printStackTrace();
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * @param request
	 * @return
	 * @throws XPathExpressionException
	 */
	public NodeList evaluate( String request ) throws XPathExpressionException {
		NodeList nodeList = (NodeList) m_xpath.evaluate( request, m_dom,
				XPathConstants.NODESET );
		return nodeList;
	}

	/**
	 * @param request
	 * @return
	 * @throws XPathExpressionException
	 */
	public Object evaluate( String request, QName item )
			throws XPathExpressionException {
		return m_xpath.evaluate( request, m_dom, item );
	}

	/**
	 * @param nodeList
	 * @return
	 */
	public TreeMap<String, ArrayList<String>> getLeaves( NodeList nodeList ) {
		Node node = null;

		TreeMap<String, ArrayList<String>> resultSet = new TreeMap<String, ArrayList<String>>();
		ArrayList<String> list = null;

		for( int i = 0; i < nodeList.getLength(); i++ ) {
			node = nodeList.item( i );

			// if this node is a leaf, keep the value it contains
			if( isNodeLeave(node) ) {
				list = resultSet.get( node.getNodeName() );
				if( null == list ) {
					list = new ArrayList<String>();
					resultSet.put( node.getNodeName(), list );
				}
				list.add( node.getFirstChild().getNodeValue() );
			}
		}
		return resultSet;
	}

	/**
	 * @param nodeList
	 * @return
	 */
	public ArrayList<String> getBranches( NodeList nodeList ) {
		Node node = null;

		ArrayList<String> resultSet = new ArrayList<String>();

		for( int i = 0; i < nodeList.getLength(); i++ ) {
			node = nodeList.item( i );

			if( isNodeBranch(node) ) {
				resultSet.add( node.getNodeName() );
			}
		}
		return resultSet;
	}
	
	public boolean isNodeLeave(Node node) {
		return (node != null && 
				node.getChildNodes() != null && 
				node.getChildNodes().getLength() == 1);
	}
	
	public boolean isNodeBranch(Node node) {
		return (node != null && 
				node.getChildNodes() != null && 
				node.getChildNodes().getLength() > 1);
	}

	/**
	 * @param resultSet
	 */
	public void print( TreeMap<String, ArrayList<String>> resultSet ) {
		Map.Entry<String, ArrayList<String>> element = null;
		ArrayList<String> stringList = null;

		element = resultSet.pollFirstEntry();
		while( null != element ) {
			System.out.print( element.getKey() + ": " );
			stringList = element.getValue();
			for( int i = 0; i < stringList.size(); i++ ) {
				System.out.print( stringList.get( i ) + " " );
			}
			System.out.println();
			element = resultSet.pollFirstEntry();
		}
	}
	
	/**
	 * @param resultSet
	 */
	public void print( ArrayList<String> resultSet ) {
		for( int i = 0; i < resultSet.size(); i++ ){
			System.out.println( resultSet.get( i ) );
		}
	}
	
}
