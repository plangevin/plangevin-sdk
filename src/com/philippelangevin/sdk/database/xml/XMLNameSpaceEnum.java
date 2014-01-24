package com.philippelangevin.sdk.database.xml;

public enum XMLNameSpaceEnum {
	/*
	 *  for more details, see the schema files (xsd) found into 
	 *    https://colmatec240/svn/General/CTecDataStructure/trunk/xml/
	 */
	// xmlns:gt="http://www.ctecworld.com/generalTypes" 
	// xmlns:ct="http://www.ctecworld.com/ctecTypes"
	// xmlns:prj="http://www.ctecworld.com/project" 
	// xmlns:po="http://www.ctecworld.com/purchaseOrder" 
	// xmlns:pl="http://www.ctecworld.com/projectLibrary"


	/*
	 * NameSpaces used into ProjectLibrary.xsd & Project.xsd
	 */
	project			{ @Override
	public String getFullURL() { return "http://www.ctecworld.com/project"; }
					  @Override
					public String getAbbreviation() { return "prj"; }},
	generalTypes	{ @Override
	public String getFullURL() { return "http://www.ctecworld.com/generalTypes"; }
					  @Override
					public String getAbbreviation() { return "gt"; }},
	ctecTypes		{ @Override
	public String getFullURL() { return "http://www.ctecworld.com/ctecTypes"; }
					  @Override
					public String getAbbreviation() { return "ct"; }},
	purchaseOrder	{ @Override
	public String getFullURL() { return "http://www.ctecworld.com/purchaseOrder"; }
					  @Override
					public String getAbbreviation() { return "po"; }},
	projectLibrary	{ @Override
	public String getFullURL() { return "http://www.ctecworld.com/projectLibrary"; }
					  @Override
					public String getAbbreviation() { return "pl"; }},
	;
	public abstract String getFullURL();
	public abstract String getAbbreviation();
}
