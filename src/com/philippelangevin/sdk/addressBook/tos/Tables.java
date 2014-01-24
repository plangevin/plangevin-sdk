package com.philippelangevin.sdk.addressBook.tos;

import com.philippelangevin.sdk.database.tables.TableInfo;
import com.philippelangevin.sdk.database.transferableObject.TransferableObject;
import com.philippelangevin.sdk.database.transferableObject.metaData.TOColumnMetaDataIF;

public enum Tables implements TableInfo {
	CONTACTTYPE			("ContactType", 		ContactTypeTO.class),		// Client, Supplier, Partner, Others
	CONTACTINFOTYPE		("ContactInfoType", 	ContactInfoTypeTO.class),	// Phone, Address, Email
	CONTACTCATEGORY		("ContactCategory",		ContactCategoryTO.class),	// Home, Work, Cell, etc.
	PHONE				("Phone", 				PhoneTO.class),				// Phone numbers
	ADDRESS				("Address", 			AddressTO.class),			// Addresses
	EMAIL				("Email", 				EmailTO.class),				// Emails
	CONTACT				("Contact", 			ContactTO.class),
	PREFEREDCONTACTINFO	("PreferedContactInfo", PreferedContactInfoTO.class)
	;

	private String stringReplacement = null;
	private Class<? extends TransferableObject> toClass = null;
	
	private Tables( String strReplacement ) {
		stringReplacement = strReplacement;
	}
	
	private Tables( String strReplacement, Class<? extends TransferableObject> toClass ) {
		stringReplacement = strReplacement;
		this.toClass = toClass;
	}
	
	public static Tables fromTableName(String tableName){
		for (Tables table : Tables.values()) {
			if (table.toString().equals(tableName))	{
				return table ;
			}
		}
		
		return null ;
	}
	
	@Override
	public String toString(){
		return stringReplacement;
	}
	
	@Override
	public Class<? extends TransferableObject> getTransferableObjectClass() {
		return toClass;
	}
	
	public TOColumnMetaDataIF<?> getMetaData() {
		return null;
	}
}
