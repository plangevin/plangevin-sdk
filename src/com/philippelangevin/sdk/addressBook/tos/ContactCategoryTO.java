package com.philippelangevin.sdk.addressBook.tos;

import com.philippelangevin.sdk.addressBook.tos.ContactCategoryTOStructure.ColumnNames;
import com.philippelangevin.sdk.database.transferableObject.TransferableObject;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectStructureIF;
import com.philippelangevin.sdk.translationManagement.TranslationBundleFactory;

public class ContactCategoryTO extends TransferableObject {

	private static final long serialVersionUID = -2534680263917178455L;
	
	private static ContactCategoryTOStructure structure = new ContactCategoryTOStructure() ;

	public ContactCategoryTO()	{
		
	}
	
	public ContactCategoryTO(ContactCategoryTO phoneTypeTO){
		super(phoneTypeTO) ;
	}
	
	public Integer getId()	{
		return get(ColumnNames.id) ; 
	}
	public void setId(Integer id)	{
		set(ColumnNames.id, id) ;
	}
	
	public String getNameKey()	{
		return get(ColumnNames.nameKey) ; 
	}
	public void setNameKey(String nameKey)	{
		set(ColumnNames.nameKey, nameKey) ;
	}

	@Override
	public TransferableObjectStructureIF getTOStructure() {
		return structure;
	}

	@Override
	public String toString()	{
		return TranslationBundleFactory.getTranslationBundle().getString(getNameKey()) ;
	}
}
