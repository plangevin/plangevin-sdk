package com.philippelangevin.sdk.addressBook.tos;

import com.philippelangevin.sdk.addressBook.tos.ContactTOStructure.ColumnNames;
import com.philippelangevin.sdk.database.transferableObject.TransferableObject;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectStructureIF;


public class ContactTO extends TransferableObject {

	private static final long serialVersionUID = -144529189951843233L;
	
	private ContactTOStructure structure = new ContactTOStructure() ;

	public ContactTO()	{
	}
	public ContactTO(ContactTO to)	{
		super (to) ;
	}
	
	public Integer getId()	{
		return get(ColumnNames.id) ;
	}
	public void setId(Integer id)	{
		set(ColumnNames.id, id) ;
	}

	public String getFullName()	{
		return get(ColumnNames.fullName) ;
	}
	public void setFullName(String fullName)	{
		set(ColumnNames.fullName, fullName) ;
	}
	
	public Integer getContactType()	{
		return get(ColumnNames.contactType) ;
	}
	public void setContactType(Integer contactType)	{
		set(ColumnNames.contactType, contactType) ;
	}

	@Override
	public TransferableObjectStructureIF getTOStructure() {
		// TODO Auto-generated method stub
		return structure ;
	}
}
