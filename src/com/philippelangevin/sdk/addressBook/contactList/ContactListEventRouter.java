package com.philippelangevin.sdk.addressBook.contactList;

import com.philippelangevin.sdk.uiUtil.interfaces.ControllerIF;
import com.philippelangevin.sdk.uiUtil.interfaces.EventRouterIF;

public class ContactListEventRouter implements EventRouterIF {

	private ContactListController controller = null ;
	private ContactListGui gui = null ;
	
	public ContactListEventRouter(ContactListController contactListController) {
		setController(contactListController) ;
	}

	@Override
	public void setController(ControllerIF controller) {
		this.controller = (ContactListController) controller ;
	}

	@Override
	public ControllerIF getController() {
		return controller ;
	}

	@Override
	public ContactListGui getGUI() {
		if (gui == null)	{
			gui = new ContactListGui(this) ;
		}
		
		return gui ;
	}

	@Override
	public void showGUI(boolean show) {
		getGUI().setVisible(show) ;
	}

	@Override
	public void dispose() {
		getGUI().dispose() ;
	}

}
