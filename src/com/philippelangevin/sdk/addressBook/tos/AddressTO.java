package com.philippelangevin.sdk.addressBook.tos;

import com.philippelangevin.sdk.addressBook.tos.AddressTOStructure.ColumnNames;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectStructureIF;


public class AddressTO extends ContactInfoTOIF {

	private static final long serialVersionUID = -144529189951843233L;
	
	private AddressTOStructure structure = new AddressTOStructure() ;

	public AddressTO()	{
	}
	public AddressTO(AddressTO to)	{
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

	@Override
	public Integer getContactCategory()	{
		return get(ColumnNames.contactCategory) ;
	}
	@Override
	public void setContactCategory(Integer contactCategory)	{
		set(ColumnNames.contactCategory, contactCategory) ;
	}
	
	public Integer getCivicNumber()	{
		return get(ColumnNames.civicNumber) ;
	}
	public void setCivicNumber(Integer civicNumber)	{
		set(ColumnNames.civicNumber, civicNumber) ;
	}

	public String getStreet()	{
		return get(ColumnNames.street) ;
	}
	public void setStreet(String street)	{
		set(ColumnNames.street, street) ;
	}
	
	public String getCity()	{
		return get(ColumnNames.city) ;
	}
	public void setCity(String city)	{
		set(ColumnNames.city, city) ;
	}

	public String getZipCode()	{
		return get(ColumnNames.zipCode) ;
	}
	public void setZipCode(String zipCode)	{
		set(ColumnNames.zipCode, zipCode) ;
	}
	
	
	@Override
	public TransferableObjectStructureIF getTOStructure() {
		return structure ;
	}

	@Override
	public String getOneLineStringRepresentation() {
		return String.format("%d %s, %s, %s", getCivicNumber(), getStreet(), getCity(), getZipCode()) ;
	}
}
