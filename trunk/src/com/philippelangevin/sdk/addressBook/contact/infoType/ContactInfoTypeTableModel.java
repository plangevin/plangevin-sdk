package com.philippelangevin.sdk.addressBook.contact.infoType;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.philippelangevin.sdk.addressBook.contact.ContactController;
import com.philippelangevin.sdk.addressBook.tos.ContactCategoryTO;
import com.philippelangevin.sdk.addressBook.tos.ContactInfoTOIF;

public class ContactInfoTypeTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3657198307440405354L;

	private Class<? extends ContactInfoTOIF> storedClass = null ;
	
	private List<ContactInfoTOIF> contactInfos = null ;
	private int defaultIndex = -1 ;
	
	private boolean dirty = false ;
	private boolean preferedHasChange = false ;
	
	public ContactInfoTypeTableModel(List<ContactInfoTOIF> contactInfos, Class<? extends ContactInfoTOIF> storedClass, Integer defaultId) {
		this.storedClass = storedClass ;
		
		setContactInfos(contactInfos) ;
		setPreferedContactInfoId(defaultId) ;
	}
	
	public void setPreferedContactInfoId(Integer defaultId) {
		int i = 0 ;
		for (; i < contactInfos.size() && !defaultId.equals(contactInfos.get(i).getId()); i++) ;

		if (i < contactInfos.size())	{
			setDefaultIndex(i) ;
		}
		else	{
			setDefaultIndex(-1) ;
		}
	}

	public int getDefaultIndex() {
		return defaultIndex;
	}
	
	public void setDefaultIndex(Integer defaultIndex) {
		if (this.defaultIndex != defaultIndex)	{
			this.defaultIndex = defaultIndex ;
			preferedHasChange = true ;
		}
		
	}
	
	public ContactInfoTOIF getPreferedContactInfo() {
		if (getDefaultIndex() < 0)	{
			return null ;
		}
		
		return getContactInfos().get(getDefaultIndex()) ;
	}


	public void setContactInfos(List<ContactInfoTOIF> contactInfos) {
		this.contactInfos = contactInfos;
	}
	public List<ContactInfoTOIF> getContactInfos() {
		return contactInfos;
	}
	
	public void add(ContactInfoTOIF contactInfo)	{
		contactInfos.add(contactInfo) ;
		setDirty(true) ;
	}
	
	public void remove(int selectedRow) {
		contactInfos.remove(selectedRow) ;
		
		if (defaultIndex == selectedRow){
			defaultIndex = -1 ;
			preferedHasChange = true ;
		}
		else if (defaultIndex > selectedRow){
			defaultIndex-- ;
		}
		
		setDirty(true) ;
	}
	
	public Class<? extends ContactInfoTOIF> getStoredClass()	{
		return storedClass ;
	}


	@Override
	public int getColumnCount() {
		return 1 ;
	}

	@Override
	public int getRowCount() {
		return contactInfos.size() ;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String value = "" ;
		
		ContactInfoTOIF to = contactInfos.get(rowIndex) ;
		if (rowIndex == defaultIndex)	{
			value = "* " ;
		}
		
		ContactCategoryTO contactCategoryTO = new ContactCategoryTO() ;
		contactCategoryTO.setId(to.getContactCategory()) ;
		contactCategoryTO = ContactController.getContactCategories().get(ContactController.getContactCategories().indexOf(contactCategoryTO)) ;
		
		return value + to.getOneLineStringRepresentation() + " - " + contactCategoryTO.toString() ;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty ;
	}
	public boolean isDirty()	{
		return this.dirty ;
	}
	public boolean preferedHasChanged()	{
		return preferedHasChange ;
	}
}
