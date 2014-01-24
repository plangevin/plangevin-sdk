package com.philippelangevin.sdk.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;
import org.xml.sax.SAXException;

/**
 * This class is intented to hold XML methods that will help XML handling.
 * @author ipainchaud
 *
 */
/*
 * History
 * Date		Name		BT	Description
 * 06-12-07	IPainchaud		initial revision
 */
public class XMLHelper {
	private static String OVERWRITE_CONFIRMATION = "Le fichier %s existe déjà. Voulez-vous l'écraser?";
	
	public static enum SAVING_MODES { SIMPLY_OVERWRITE, BACKUP_OVERWRITE, ASK_OVERWRITE };
	
	private static int SAVE_ATTEMPTS = 3;
	private static int SAVE_FAIL_SLEEP = 50;
	
	// Used to specify the type of schema to validate
	private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	
	/**
	 * @param name
	 * @return
	 */
	public static Document newDocument( String name, String nameSpace ) {
		try {
			DocumentBuilderFactory domFac = DocumentBuilderFactory
					.newInstance();
			
			domFac.setNamespaceAware( true );
			DocumentBuilder builder = domFac.newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();

			return impl.createDocument( nameSpace, name, null );
		} catch( Exception e ) {
			System.err.println( "nameSpace: " + nameSpace + "\nfile name: " + name );
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param fullFileName
	 * @return
	 * @throws SAXException
	 */
	public static Schema newSchema( String fullFileName ) throws SAXException {
		// open the schema file
		File schemaFile = new File( fullFileName );
		if( !schemaFile.exists() ) {
			System.out.println( "File: " + schemaFile.getPath()
					+ " doesn't exist" );
			return null;
		}

		SchemaFactory schemaFactory = SchemaFactory
				.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );

		return schemaFactory.newSchema( schemaFile );
	}

	/**
	 * @param dom
	 * @param schema
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void validateXML( Document dom, Schema schema )
			throws SAXException, IOException {
		Validator validator = schema.newValidator();
		validator.validate( new DOMSource( dom.getFirstChild() ) );
	}

	/**
	 * Validates a XML file against a XSD file, throws an exception if not valid.
	 * @param xmlFile
	 * @param xsdFile
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void validateXML(File xmlFile, File xsdFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		docFactory.setValidating(true);
		docFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		DocumentBuilder parser = docFactory.newDocumentBuilder();
		parser.setErrorHandler(null);
		Document document = parser.parse(xmlFile);
		
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Source schemaFile = new StreamSource(xsdFile);
		Schema schema = schemaFactory.newSchema(schemaFile);
		
		Validator validator = schema.newValidator();
		validator.validate(new DOMSource(document));
	}

	/**
	 * add a text child Element/Node to a Document. If minOccurs1 == false and
	 * nodeText == null, no Element will be added. If nodeText == null and
	 * minOccurs1 == true, an empty element will be created.
	 * @param dom Document who receives the new Element/Node
	 * @param parent Parent Element to whom will be added the Element/Node
	 * @param nodeText Text to add to the new Element
	 * @param minOccurs1 Specifies if this Element is a mandatory one or not
	 * @param attributes Pairs of strings describing the attributes to add to
	 * the created Element.
	 * @return The created Element or null if minOccurs1 == false and nodeText
	 * == null.
	 */
	public static Element addChild( Document dom, Node parent,
			String nodeName, String nodeText, boolean minOccurs1,
			String... attributes ) {
		
		if( !minOccurs1 ) {
			if( null == nodeText ){
				return null;
			} else if( 0 == nodeText.compareTo("null") ) {
				return null;
			}
		}
		
		// create the node
		Element elem = dom.createElement( nodeName );
		// add all the attributes to be added
		
		if( attributes.length > 0 ) {
			if( attributes.length % 2 != 0 ) {
				throw new IllegalArgumentException(
						"Attributes string parameters must be given as name and value pairs. addChild() must be called with an even number of attribute Strings." );
			}
			for( int i = 0; i < attributes.length; i += 2 ) {
				elem.setAttribute( attributes[i], attributes[i + 1] );
			}
		}
		
		// add the text child node to the created Node (elem)
		/* */
		if( null != nodeText ) {
			Node node = dom.createTextNode( nodeText );
			elem.appendChild( node );
		}
		// append this child node to the parent
		parent.appendChild( elem );
		return elem;
	}

	public static Element addChildNS( Document dom, Node parent, String nameSpaceURI,
			String nodeName, String nodeText, boolean minOccurs1, String... attributes ) {
		
		if( !minOccurs1 ) {
			if( null == nodeText ){
				return null;
			} else if( 0 == nodeText.compareTo("null") ) {
				return null;
			}
		}
		
		// create the node
		Element elem = dom.createElementNS( nameSpaceURI, nodeName );
		// add all the attributes to be added
		if( attributes.length > 0 ) {
			if( attributes.length % 3 != 0 ) {
				throw new IllegalArgumentException(
						"Attributes string parameters must be given as name and value pairs. addChild() must be called with an even number of attribute Strings." );
			}
			for( int i = 0; i < attributes.length; i += 3 ) {
				elem.setAttributeNS( attributes[i], attributes[i + 1], attributes[i + 2] );
			}
		}
		
		// add the text child node to the created Node (elem)
		/* */
		if( null != nodeText ) {
			Node node = dom.createTextNode( nodeText );
			elem.appendChild( node );
		}
		// append this child node to the parent
		parent.appendChild( elem );
		return elem;
	}

	public static Element addChild( Document dom, Node parent, String nsPrefix, String nsURL,
			String nodeName, String nodeText, boolean minOccurs1, Node refChild, String... attributes ) {
		
		if( !minOccurs1 ) {
			if( null == nodeText ){
				return null;
			} else if( 0 == nodeText.compareTo("null") ) {
				return null;
			}
		}
		
		// create the node
		Element elem = dom.createElementNS( nsURL, nodeName );
		elem.setPrefix( nsPrefix );
		// add all the attributes to be added
		if( attributes.length > 0 ) {
			if( attributes.length % 4 != 0 ) {
				throw new IllegalArgumentException(
						"Attributes string parameters must be given as name and value pairs. addChild() must be called with an even number of attribute Strings." );
			}
			for( int i = 0; i < attributes.length; i += 4 ) {
				Attr attribute = dom.createAttributeNS( attributes[i + 1], attributes[i + 2] );
				attribute.setPrefix( attributes[i] );
				attribute.setValue( attributes[i + 3] );
				elem.setAttributeNodeNS( attribute );
			}
		}
		
		// add the text child node to the created Node (elem)
		/* */
		if( null != nodeText ) {
			Node node = dom.createTextNode( nodeText );
			elem.appendChild( node );
		}
		// append this child node to the parent
		parent.insertBefore( elem, refChild );
		return elem;
	}

	/**
	 * does exactly the same thing as saveXMLFile except that it won't save it
	 * to a file but rather return a byte[] buffer
	 * @param dom
	 */
	public static byte[] getDomBytes( Document dom ) {
		ByteArrayOutputStream baos = getDomBytesStream(dom);
		return (baos == null)? null: baos.toByteArray();
	}
	
	public static ByteArrayOutputStream getDomBytesStream( Document dom ) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream( 500000 );
			StreamResult streamResult = new StreamResult( baos );
			
			transformXML( dom, streamResult, "UTF-8" );
			
			return baos;
			
		} catch( TransformerException e ) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Saves the XML Document (DOM) into an xml file. If a file with the same
	 * name already exists, rename the existing one by adding ".bak" suffix and
	 * then creates the new one.
	 * @param dom
	 * @param fullFileName
	 * @throws FileNotFoundException
	 */
	public static boolean saveXMLFile( Document dom, String fullFileName ) {
		return saveXMLFile( dom, fullFileName, SAVING_MODES.BACKUP_OVERWRITE );
	}

	public static boolean saveXMLFile( Document dom, String fullFileName, String encoding ) {
		return saveXMLFile( dom, fullFileName, SAVING_MODES.BACKUP_OVERWRITE, encoding );
	}

	public static boolean saveXMLFile( Document dom, String fullFileName, SAVING_MODES mode ) {
		return saveXMLFile( dom, fullFileName, mode, "UTF-8" );
	}

	public static boolean saveXMLFile( Document dom, String fullFileName, SAVING_MODES mode, String encoding ) {
		File xmlFile = new File( fullFileName );
		if( xmlFile.exists() ) {
			switch( mode ) {
			case ASK_OVERWRITE:
				/*
				 * ask the user what to do!!!
				 */
				int answer = JOptionPane.showConfirmDialog( null, String.format( OVERWRITE_CONFIRMATION, xmlFile.toString() ) );
				switch( answer ) {
				case JOptionPane.YES_OPTION:
					// do nothing!!!
					break;
				case JOptionPane.NO_OPTION:
					/*
					 * You don't want to overwrite the existing file,
					 * please give me another file name to save the file to
					 * save
					 */
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter(
							"XML file", "xml" );
					chooser.setFileFilter(filter);
					int returnVal = chooser.showSaveDialog( null );
					if( returnVal == JFileChooser.APPROVE_OPTION ) {
						xmlFile = new File( chooser.getSelectedFile().getName() );
					} else {
						return false;
					}
					break;
				case JOptionPane.CANCEL_OPTION:
					return false; // we are not saving anything!!!
				default:
					throw new AssertionError();
				}
				break;
			case BACKUP_OVERWRITE:
				// rename the existing file by adding ".bak" suffix
				File bakFile = new File( fullFileName + ".bak" );
				if( bakFile.exists() ) {
					if (!bakFile.delete()) {
						System.err.println("XMLHelper.saveXMLFile(): Failed to delete the backup file: " + bakFile.getAbsolutePath());
					}
				}
				if (!xmlFile.renameTo( bakFile )) {
					System.err.println("XMLHelper.saveXMLFile(): Failed to rename the XML file (" + xmlFile.getAbsolutePath() + ") to the backup file: " + bakFile.getAbsolutePath());
				}
				break;
			case SIMPLY_OVERWRITE:
				// do nothing!!!
				break;
			default:
				throw new AssertionError();
			}
		}
		
		xmlFile.getAbsoluteFile().getParentFile().mkdirs();
		StreamResult streamResult = null;
		
		for (int attemptNo = 1; attemptNo <= SAVE_ATTEMPTS; attemptNo++) {
			try {
				/*
				 * This is a tricky place! the output stream must be wrapped by a
				 * Writer to make sure that the INDENT option will work.
				 */
				streamResult = new StreamResult( new OutputStreamWriter(
						new FileOutputStream( xmlFile ), encoding ) );
				
				/*
				 * We were successful to create the XML file, so we break the loop.
				 */
				break;
				
			} catch( IOException e ) {
				System.err.println("Failed to write to XML file on attempt #" + attemptNo);
				e.printStackTrace();
				
				if (attemptNo < SAVE_ATTEMPTS) {
					/*
					 * We try to make the Thread sleep in order to give
					 * time to the system to free up the required resource.
					 */
					try {
						Thread.sleep(SAVE_FAIL_SLEEP);
					} catch (InterruptedException e1) {
						 /* Do nothing */
					}
					
				} else {
					System.err.println("Enough save attempts were tried, I give up on writing to this XML file!");
					return false;
				}
			}
		}

		try {
			transformXML( dom, streamResult, encoding );
			streamResult.getWriter().close();
			return true;
			
		} catch( TransformerException e ) {
			e.printStackTrace();
			return false;
		} catch ( IOException e ) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param dom
	 * @param streamResult
	 * @throws TransformerException
	 */
	public static void transformXML( Document dom, Result result, String encoding ) throws TransformerException {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			// this is a tricky place! For an unknown reason, Java 1.5 and 1.6
			// have their "indent-number" default set to 0... Well, we must
			// change that.
			try {
				tf.setAttribute( "indent-number", Integer.valueOf( 4 ) );
			} catch( IllegalArgumentException e ) {
				// do nothing! See below for more explanation:
				// the setAttribute() call causes an IllegalArgumentException in Java 1.4. So wrap the setAttribute() in a try/catch and throw away the IllegalArgumentException...
			}
			Transformer transformer = tf.newTransformer();

			DOMSource domSource = new DOMSource( dom );

			transformer.setOutputProperty( OutputKeys.ENCODING, encoding );
			transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
			transformer.transform( domSource, result );
		} catch( TransformerConfigurationException e ) {
			e.printStackTrace();
		} catch( IllegalArgumentException e ) {
			e.printStackTrace();
		} catch( TransformerFactoryConfigurationError e ) {
			e.printStackTrace();
		}
	}

	/*
	 * The following method ensures we ignore whitespace nodes when parsing the XML file.
	 * Reference: http://forums.sun.com/thread.jspa?messageID=2054303#2699961
	 */
	public static Document readXML( File xmlFile ) {
		Document dom = null;
		DocumentBuilderFactory domFac = DocumentBuilderFactory.newInstance();
		domFac.setNamespaceAware( true );
		
		try {
			DocumentBuilder domBuilder = domFac.newDocumentBuilder();
			DOMImplementation domImpl = domBuilder.getDOMImplementation();
			
			DOMImplementationLS ls = (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
			LSInput in = ls.createLSInput();
			in.setByteStream(new FileInputStream(xmlFile));
			LSParser parser = ls.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, "http://www.w3.org/2001/XMLSchema");
			
			dom = parser.parse(in);
			
		} catch( ParserConfigurationException e ) {
			e.printStackTrace();
		} catch( IOException e ) {
			e.printStackTrace();
		}
		return dom;
	}
	
	/*
	 * The following method ensures we ignore whitespace nodes when parsing the XML file.
	 * Reference: http://forums.sun.com/thread.jspa?messageID=2054303#2699961
	 */
	public static Document readXML( InputStream xmlStream ) {
		Document dom = null;
		DocumentBuilderFactory domFac = DocumentBuilderFactory.newInstance();
		domFac.setNamespaceAware( true );
		
		try {
			DocumentBuilder domBuilder = domFac.newDocumentBuilder();
			DOMImplementation domImpl = domBuilder.getDOMImplementation();
			
			DOMImplementationLS ls = (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
			LSInput in = ls.createLSInput();
			in.setByteStream(xmlStream);
			LSParser parser = ls.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, "http://www.w3.org/2001/XMLSchema");
			
			dom = parser.parse(in);

		} catch( ParserConfigurationException e ) {
			e.printStackTrace();
		}
		return dom;
	}
	
	/**
	 * parse the XML file content
	 * The following method ensures we ignore whitespace nodes when parsing the XML file.
	 * Reference: http://forums.sun.com/thread.jspa?messageID=2054303#2699961
	 * @param bytes
	 * @return
	 */
	public static Document readXML( byte[] bytes ) {
		Document dom = null;
		DocumentBuilderFactory domFac = DocumentBuilderFactory.newInstance();
		domFac.setNamespaceAware( true );
		
		try {
			DocumentBuilder domBuilder = domFac.newDocumentBuilder();
			DOMImplementation domImpl = domBuilder.getDOMImplementation();
			
			DOMImplementationLS ls = (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
			LSInput in = ls.createLSInput();
			in.setByteStream(new ByteArrayInputStream( bytes ));
			LSParser parser = ls.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, "http://www.w3.org/2001/XMLSchema");
			
			dom = parser.parse(in);

		} catch( ParserConfigurationException e ) {
			e.printStackTrace();
		}
		return dom;
	}

	public static Document getReadableDom( Document dom ){
		try {
			DOMResult domResult = new DOMResult();
			transformXML( dom, domResult, "UTF-8" );
			return (Document)domResult.getNode();
		} catch( TransformerException e ) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * initialize the xpath engine
	 * @param nsMap table of x by 2 containing the mapping between prefixes and their name spaces
	 * @see XPathRequester for more detail on use
	 */
	public static XPath newXPath( final String[][] nsMap ) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		if( null != nsMap ) {
			xpath.setNamespaceContext( new NamespaceContext() {
				@Override
				public String getNamespaceURI( String prefix ) {
					if( prefix == null ) {
						throw new NullPointerException( "Null prefix" );
					}
					for( int i = 0; i < nsMap.length; i++ ) {
						if( nsMap[i][0].equals( prefix ) ) {
							return nsMap[i][1];
						}
					}
					return XMLConstants.NULL_NS_URI;
				}

				@Override
				public String getPrefix( String arg0 ) {
					throw new UnsupportedOperationException();
				}

				@Override
				public Iterator<?> getPrefixes( String arg0 ) {
					throw new UnsupportedOperationException();
				}
			} );
		}
		return xpath;
	}

	private static void toStringRaw( Node n, int lvl, StringBuilder sb ) {
		while( null != n ) {
			sb.append( "\n" );
			for( int i = 0; i < lvl; i++ ) {
				sb.append( "   " );
			}
			sb.append( n.getLocalName() );
			sb.append( " nn: " );
			sb.append( n.getNodeName() );
			if( n.hasAttributes() ) {
				NamedNodeMap nnm = n.getAttributes();
				for( int i = 0; i < nnm.getLength(); i++ ) {
					sb.append( " @" );
					sb.append( nnm.item( i ).getLocalName() );
					sb.append( " val: " );
					sb.append( nnm.item( i ).getNodeValue() );
				}
			}
			sb.append( " val: " );
			sb.append( n.getNodeValue() );
			if( n.hasChildNodes() ) {
				sb.append( " nb of childs: " );
				sb.append( n.getChildNodes().getLength() );
				toStringRaw( n.getFirstChild(), lvl + 1, sb );
			}
			n = n.getNextSibling();
		}
	}

	private static void toString( Node n, int lvl, StringBuilder sb ) {
		while( null != n ) {
			/*
			 * Only tags that have 1 child contains text... and these are the one we want.
			 * Isn't it nice!!!
			 */
			if( 1 == n.getChildNodes().getLength() ) {
				/*
				 * the local name is the name without the name space
				 * the node name is the name with the name space
				 * Here, we take the local name for simplicity
				 */
				String localName = n.getLocalName();
				sb.append( "\n" );
				/*
				 * indent of 3 spaces for each level
				 */
				for( int i = 0; i < lvl; i++ ) {
					sb.append( "   " );
				}
				/*
				 * print the tag name
				 */
				sb.append( localName /*+ " nn: " + n.getNodeName()*/ );
				/*
				 * print the attributes
				 */
				if( n.hasAttributes() ) {
					NamedNodeMap nnm = n.getAttributes();
					for( int i = 0; i < nnm.getLength(); i++ ) {
						/*
						 * the value of a node is always contained into the first child value...
						 * That's weird, but that's how it works
						 */
						sb.append( " @" + nnm.item( i ).getLocalName() /*"+ nn: " + n.getNodeName() + */ );
						sb.append( "=" );
						sb.append( nnm.item( i ).getNodeValue() );
					}
				}
				sb.append( " (" );
				sb.append( n.getChildNodes().getLength() );
				sb.append( ") " );
				/*
				 * print the value of the node.
				 * Yes, the value is in the first child node's value, just like the attributes.
				 */
				Node child = n.getFirstChild();
				String childValue = child.getNodeValue();
				/*
				 * If the first child's value is empty, this node doesn't contain text.
				 */
				if( !childValue.trim().equals( "" ) ) {
					sb.append( " val: " );
					sb.append( childValue );
				}
			}
			n = n.getNextSibling();
		} // end while
	}
	
	public static String toStringRaw( Node node ) {
		StringBuilder sb = new StringBuilder( 1000 );
		toStringRaw( node.getFirstChild(), 0, sb );
		return sb.toString();
	}
	
	public static String toString( Node node ) {
		StringBuilder sb = new StringBuilder( 1000 );
		toString( node.getFirstChild(), 0, sb );
		return sb.toString();
	}
	
	public static String toStringRaw( NodeList nodeList ) {
		StringBuilder sb = new StringBuilder( 5000 );
		int nbOfNode = nodeList.getLength();
		for( int i = 0; i < nbOfNode; i++ ) {
			toStringRaw( nodeList.item( i ).getFirstChild(), 0, sb );
		}
		return sb.toString();
	}
	
	public static String toString( NodeList nodeList ) {
		StringBuilder sb = new StringBuilder( 5000 );
		int nbOfNode = nodeList.getLength();
		for( int i = 0; i < nbOfNode; i++ ) {
			toString( nodeList.item( i ).getFirstChild(), 0, sb );
		}
		return sb.toString();
	}
}
