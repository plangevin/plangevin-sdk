package com.philippelangevin.sdk.addressBook.contact;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.philippelangevin.sdk.addressBook.AddressBookDAO;
import com.philippelangevin.sdk.addressBook.tos.ContactCategoryTO;
import com.philippelangevin.sdk.addressBook.tos.ContactInfoTOIF;
import com.philippelangevin.sdk.addressBook.tos.ContactInfoTypeTO;
import com.philippelangevin.sdk.addressBook.tos.ContactTO;
import com.philippelangevin.sdk.addressBook.tos.ContactTOStructure;
import com.philippelangevin.sdk.addressBook.tos.ContactTypeTO;
import com.philippelangevin.sdk.addressBook.tos.PreferedContactInfoTO;
import com.philippelangevin.sdk.addressBook.tos.Tables;
import com.philippelangevin.sdk.uiUtil.interfaces.ControllerIF;
import com.philippelangevin.sdk.uiUtil.interfaces.EventRouterIF;

public class ContactController extends ControllerIF {

	private static List<ContactInfoTypeTO> contactInfoTypes = null ;
	private static List<ContactCategoryTO> contactCategories = null ;
	private static List<ContactTypeTO> contactTypes = null ;
	
	private AddressBookDAO dao = null ;
	
	private ContactTO currentContactTO = null ;
	
	public ContactController(AddressBookDAO dao)	{
		try {
			contactInfoTypes = dao.selectTOList(ContactInfoTypeTO.class, null) ;
			contactCategories = dao.selectTOList(ContactCategoryTO.class, null) ;
			contactTypes = dao.selectTOList(ContactTypeTO.class, null) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.setDao(dao) ;
	}
	
	@Override
	protected EventRouterIF getEventRouter() {
		if (eventRouter == null){
			eventRouter = new ContactEventRouter(this) ;
		}
		
		return eventRouter;
	}

	public void setDao(AddressBookDAO dao) {
		this.dao = dao;
	}

	public AddressBookDAO getDao() {
		return dao;
	}

	public void setCurrentContactTO(ContactTO currentContactTO) {
		this.currentContactTO = currentContactTO;
	}

	public ContactTO getCurrentContactTO() {
		return currentContactTO;
	}

	public List<ContactInfoTOIF> getCurrentContactInfos(ContactInfoTypeTO contactInfoType) {
		List<ContactInfoTOIF> list = new ArrayList<ContactInfoTOIF>() ;
		
		if (currentContactTO != null && currentContactTO.getId() != null){
			try {
				list = dao.getContactInfo(currentContactTO, contactInfoType) ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return list ;
	}
	
	public void saveContactInfos(List<ContactInfoTOIF> items) {
		if (items.size() > 0)	{
			try {
				dao.deleteAllContactTypeForContact(items.get(0), currentContactTO.getId()) ;
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			int id = 1 ;
			for (ContactInfoTOIF item : items){
				/*
				 * Set all ids and contact ids
				 */
				item.setId(id++) ;
				item.setContactId(currentContactTO.getId()) ;
				
				try {
					dao.insert(item) ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Will commit the default contact info id in DB.
	 * Set contactInfo to null to delete the default entry
	 * @param contactInfoType	The id of the contact info type to set the default
	 * @param contactInfo		The contactInfo to set by default.  Set to null to delete current contact default for this contact info type
	 */
	public void setPreferedContactInfoType(Integer contactInfoType, ContactInfoTOIF contactInfo) {
		PreferedContactInfoTO preferedContactInfo = new PreferedContactInfoTO() ;
		
		preferedContactInfo.setContactId(currentContactTO.getId()) ;
		preferedContactInfo.setContactInfoType(contactInfoType) ;
		
		if (contactInfo == null)	{
			dao.delete(preferedContactInfo) ;
		}
		else	{
			preferedContactInfo.setPreferedId(contactInfo.getId()) ;
			
			try {
				dao.upsert(preferedContactInfo) ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	@SuppressWarnings("unchecked")
	public Class<? extends ContactInfoTOIF> getClassForInfoType(ContactInfoTypeTO contactInfoType) {
		return (Class<? extends ContactInfoTOIF>) Tables.fromTableName(contactInfoType.getTableName()).getTransferableObjectClass() ;
	}

	public Integer getCurrentContactPreferedContactInfoId(ContactInfoTypeTO contactInfoType) {
		Integer preferedId = -1 ;
		
		if (currentContactTO != null && currentContactTO.getId() != null){
			PreferedContactInfoTO to = new PreferedContactInfoTO() ;
			to.setContactId(getCurrentContactTO().getId()) ;
			to.setContactInfoType(contactInfoType.getId()) ;
			
			try {
				to = getDao().selectTO(to) ;
				
				if (to != null)	{
					preferedId = to.getPreferedId() ;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return preferedId ;
	}

	public String getCurrentContactFullName() {
		String currentFullName = "" ;
		
		if (currentContactTO != null){
			currentFullName = currentContactTO.getFullName() ;
		}
		
		return currentFullName;
	}
	public void setCurrentContactFullName(String fullName) {
		currentContactTO.setFullName(fullName) ;
	}
	
	public Integer getCurrentContactTypeId()	{
		Integer currentContactType = null ;
		
		if (currentContactTO != null){
			currentContactType = currentContactTO.getContactType() ;
		}
		
		return currentContactType ;
	}
	public void setCurrentContactType(Integer contactType)	{
		currentContactTO.setContactType(contactType) ;
	}

	
	public static List<ContactInfoTypeTO> getContactInfoTypes() {
		return contactInfoTypes;
	}
	public static List<ContactCategoryTO> getContactCategories() {
		return contactCategories;
	}
	public static List<ContactTypeTO> getContactTypes() {
		return contactTypes;
	}

	public void saveContactTO() {
		try {
			if (currentContactTO.getId() == null)	{
				Integer max = dao.getMaxValue(currentContactTO.getTOStructure().getRepresentedTable(), ContactTOStructure.ColumnNames.id) ;
				
				if (max >= 0)	{
					currentContactTO.setId(max+1) ;
					dao.insert(currentContactTO) ;
				}
			}
			else	{
				dao.update(currentContactTO) ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
