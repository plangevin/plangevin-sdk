package com.philippelangevin.sdk.addressBook.contactList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.philippelangevin.sdk.addressBook.AddressBookDAO;
import com.philippelangevin.sdk.addressBook.tos.ContactTO;
import com.philippelangevin.sdk.uiUtil.interfaces.ControllerIF;
import com.philippelangevin.sdk.uiUtil.interfaces.EventRouterIF;

public class ContactListController extends ControllerIF {

	private AddressBookDAO dao = null ;
	
	public ContactListController(AddressBookDAO dao){
		setDao(dao) ;
	}
	
	@Override
	protected EventRouterIF getEventRouter() {
		if (eventRouter == null){
			eventRouter = new ContactListEventRouter(this) ;
		}
		
		return eventRouter;
	}

	public void setDao(AddressBookDAO dao) {
		this.dao = dao;
	}

	public AddressBookDAO getDao() {
		return dao;
	}
	
	public List<ContactTO> getContactList()	{
		List<ContactTO> list = new ArrayList<ContactTO>() ;
		
		try {
			list = dao.selectTOList(ContactTO.class, null) ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list ;
	}
}
