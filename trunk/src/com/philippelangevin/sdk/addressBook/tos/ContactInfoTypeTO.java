package com.philippelangevin.sdk.addressBook.tos;

import com.philippelangevin.sdk.addressBook.tos.ContactInfoTypeTOStructure.ColumnNames;
import com.philippelangevin.sdk.database.transferableObject.TransferableObject;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectStructureIF;

public class ContactInfoTypeTO extends TransferableObject {

	private static final long serialVersionUID = -3795625226970702171L;
	
	private static ContactInfoTypeTOStructure structure = new ContactInfoTypeTOStructure() ;
	
	public ContactInfoTypeTO()	{
		
	}
	public ContactInfoTypeTO(ContactInfoTypeTO to){
		super (to) ;
	}
	
	public Integer getId()	 {
		return get(ColumnNames.id) ;
	}
	public void setId(Integer id)	{
		set(ColumnNames.id, id) ;
	}
	
	public String getNameKey()	{
		return get(ColumnNames.nameKey) ;
	}
	public void setNameKey(String name)	{
		set(ColumnNames.nameKey, name) ;
	}
	
	public String getTableName()	{
		return get(ColumnNames.tableName) ;
	}
	public void setTableName(String tableName)	{
		set(ColumnNames.tableName, tableName) ;
	}

	@Override
	public TransferableObjectStructureIF getTOStructure() {
		return structure ;
	}

}
