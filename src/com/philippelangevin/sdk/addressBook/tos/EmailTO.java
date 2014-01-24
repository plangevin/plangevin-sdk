package com.philippelangevin.sdk.addressBook.tos;

import com.philippelangevin.sdk.addressBook.tos.EmailTOStructure.ColumnNames;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectStructureIF;

public class EmailTO extends ContactInfoTOIF {

	private static final long serialVersionUID = -144529189951843233L;
	
	private EmailTOStructure structure = new EmailTOStructure() ;

	public EmailTO()	{
	}
	public EmailTO(EmailTO to)	{
		super (to) ;
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

	public String getEmail()	{
		return get(ColumnNames.email) ;
	}
	public void setEmail(String email)	{
		set(ColumnNames.email, email) ;
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
		// TODO Auto-generated method stub
		return structure ;
	}

	@Override
	public String getOneLineStringRepresentation() {
		return String.format("%s", getEmail()) ;
	}
}
