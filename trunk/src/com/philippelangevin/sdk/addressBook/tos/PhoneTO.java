package com.philippelangevin.sdk.addressBook.tos;

import com.philippelangevin.sdk.addressBook.tos.PhoneTOStructure.ColumnNames;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectStructureIF;

public class PhoneTO extends ContactInfoTOIF {

	private static final long serialVersionUID = 7628836827749062916L;
	
	private PhoneTOStructure structure = new PhoneTOStructure() ;

	public PhoneTO(PhoneTO to)	{
		super (to) ;
	}
	public PhoneTO() {
	}

	@Override
	public Integer getContactId()	{
		return get(ColumnNames.contactId) ;
	}
	@Override
	public void setContactId(Integer contactId)	{
		set(ColumnNames.contactId, contactId) ;
	}
	
	@Override
	public Integer getId()	{
		return get(ColumnNames.id) ;
	}
	@Override
	public void setId(Integer id)	{
		set(ColumnNames.id, id) ;
	}

	public String getPhoneNumber()	{
		return get(ColumnNames.phoneNumber) ;
	}
	public void setPhoneNumber(String phoneNumber)	{
		set(ColumnNames.phoneNumber, phoneNumber) ;
	}

	@Override
	public Integer getContactCategory()	{
		return get(ColumnNames.contactCategory) ;
	}
	@Override
	public void setContactCategory(Integer contactCategory)	{
		set(ColumnNames.contactCategory, contactCategory) ;
	}

	
	@Override
	public TransferableObjectStructureIF getTOStructure() {
		return structure ;
	}


	@Override
	public String getOneLineStringRepresentation() {
		return getPhoneNumber() ;
	}

}
