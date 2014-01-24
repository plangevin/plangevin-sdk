package com.philippelangevin.sdk.addressBook.tos;

import com.philippelangevin.sdk.database.transferableObject.TransferableObject;

public abstract class ContactInfoTOIF extends TransferableObject {

	private static final long serialVersionUID = -5835963558513411516L;

	public ContactInfoTOIF()	{
	}
	public ContactInfoTOIF(TransferableObject to) {
		super(to) ;
	}

	public abstract Integer getContactId() ;
	public abstract void setContactId(Integer contactId) ;
	
	public abstract Integer getId() ;
	public abstract void setId(Integer Id) ;
	
	public abstract Integer getContactCategory() ;
	public abstract void setContactCategory(Integer contactCategory) ;
	
	public abstract String getOneLineStringRepresentation() ;
}
