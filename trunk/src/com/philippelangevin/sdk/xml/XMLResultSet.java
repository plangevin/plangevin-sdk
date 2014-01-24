package com.philippelangevin.sdk.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@Deprecated
public class XMLResultSet {

	private static final long serialVersionUID = 1L;
	
	private TreeMap<String, ArrayList<String>> xmlTreeMap = null;
	private XPathRequester xPathRequester = null;
	private NodeList nodeList;
	private Document dom = null;
	
	public XMLResultSet(Document dom) {
		this.dom = dom;
	}
	
	public void executeQuery(String xpathQuery){
		try {
			nodeList = getXPathRequester(dom).evaluate(xpathQuery);
			xmlTreeMap = getXPathRequester(dom).getLeaves(nodeList);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
	}

	public String getString(String xmlTag) {
		String dataStr = null;
		
		if(xmlTreeMap.get(xmlTag) !=null ){
			dataStr = xmlTreeMap.get(xmlTag).get(0).toString();
		}else{
			System.out.println("Missing tag : " + xmlTag);
			dataStr = "";
		}
		return dataStr;
	}
	
	
	public int getInt(String xmlTag) {
		String dataStr = null;
		int intValue = -1;
		
		if(xmlTreeMap.get(xmlTag) !=null ){
			dataStr = xmlTreeMap.get(xmlTag).get(0).toString();
			intValue = Integer.parseInt(dataStr);
		}else{
			System.out.println("Missing tag : " + xmlTag);
		}
		return intValue;
	}
	
	
	public float getFloat(String xmlTag) {
		String dataStr = null;
		float floatValue = -1;
		
		if(xmlTreeMap.get(xmlTag) !=null ){
			dataStr = xmlTreeMap.get(xmlTag).get(0).toString();
			floatValue = Float.parseFloat(dataStr);
		}else{
			System.out.println("Missing tag : " + xmlTag);
		}
		return floatValue;
	}
	
	
	public double getDouble(String xmlTag) {
		String dataStr = null;
		double doubleValue = -1;
		
		if(xmlTreeMap.get(xmlTag) !=null ){
			dataStr = xmlTreeMap.get(xmlTag).get(0).toString();
			doubleValue = Double.parseDouble(dataStr);
		}else{
			System.out.println("Missing tag : " + xmlTag);
		}
		return doubleValue;
	}
	
	
	public long getLong(String xmlTag) {
		String dataStr = null;
		long longValue = -1;
		
		if(xmlTreeMap.get(xmlTag) !=null ){
			dataStr = xmlTreeMap.get(xmlTag).get(0).toString();
			longValue = Long.parseLong(dataStr);
		}else{
			System.out.println("Missing tag : " + xmlTag);
		}
		return longValue;
	}
	
	public boolean getBoolean(String xmlTag) {
		String dataStr = null;
		boolean boolValue = false;
		
		if(xmlTreeMap.get(xmlTag) !=null ){
			dataStr = xmlTreeMap.get(xmlTag).get(0).toString();
			boolValue = Boolean.parseBoolean(dataStr);
		}else{
			System.out.println("Missing tag : " + xmlTag);
		}
		return boolValue;
	}
	
	public Date getDate(String xmlTag){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		String strDate = "";
		Date date = null;
		
		if(xmlTreeMap.get(xmlTag) !=null ){
			strDate = xmlTreeMap.get(xmlTag).get(0).toString();
			try {
				date = dateFormat.parse(strDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("Missing tag : " + xmlTag);
			try {
				date = dateFormat.parse("1-1-1");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}
	
	private XPathRequester getXPathRequester(Document dom) {
		if (xPathRequester == null) {
			xPathRequester = new XPathRequester(dom, null);
		}
		return xPathRequester;
	}
}
