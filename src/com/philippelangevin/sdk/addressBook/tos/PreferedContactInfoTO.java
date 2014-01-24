package com.philippelangevin.sdk.addressBook.tos;

import com.philippelangevin.sdk.addressBook.tos.PreferedContactInfoTOStructure.ColumnNames;
import com.philippelangevin.sdk.database.transferableObject.TransferableObject;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectStructureIF;

public class PreferedContactInfoTO extends TransferableObject {

	private static final long serialVersionUID = 7628836827749062916L;
	
	private PreferedContactInfoTOStructure structure = new PreferedContactInfoTOStructure() ;

	public PreferedContactInfoTO()	{
	}
	public PreferedContactInfoTO(PreferedContactInfoTO to)	{
		super (to) ;
	}

	
	public Integer getContactId()	{
		return get(ColumnNames.contactId) ;
	}
	public void setContactId(Integer contactId)	{
		set(ColumnNames.contactId, contactId) ;
	}
	
	public Integer getContactInfoType()	{
		return get(ColumnNames.contactInfoType) ;
	}
	public void setContactInfoType(Integer contactInfoType)	{
		set(ColumnNames.contactInfoType, contactInfoType) ;
	}

	public Integer getPreferedId()	{
		return get(ColumnNames.preferedId) ;
	}
	public void setPreferedId(Integer preferedId)	{
		set(ColumnNames.preferedId, preferedId) ;
	}

	
	@Override
	public TransferableObjectStructureIF getTOStructure() {
		return structure ;
	}

}
