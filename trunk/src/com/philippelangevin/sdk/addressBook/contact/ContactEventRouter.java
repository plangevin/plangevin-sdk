package com.philippelangevin.sdk.addressBook.contact;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.philippelangevin.sdk.addressBook.contact.infoType.ContactInfoTypeTableModel;
import com.philippelangevin.sdk.addressBook.tos.ContactInfoTypeTO;
import com.philippelangevin.sdk.addressBook.tos.ContactTypeTO;
import com.philippelangevin.sdk.uiUtil.interfaces.ControllerIF;
import com.philippelangevin.sdk.uiUtil.interfaces.EventRouterIF;

public class ContactEventRouter implements EventRouterIF {
	
	private ContactController controller = null ;
	private ContactGui gui = null ;
	
	public ContactEventRouter(ContactController controller){
		setController(controller) ;
	}
	
	@Override
	public void setController(ControllerIF controller) {
		this.controller = (ContactController) controller ;
	}

	@Override
	public ContactController getController() {
		return controller ;
	}

	@Override
	public ContactGui getGUI() {
		if (gui == null)	{
			gui = new ContactGui(this) ;
		}
		return gui;
	}

	@Override
	public void showGUI(boolean show) {
		getGUI().setVisible(show) ;
	}

	@Override
	public void dispose() {
		getGUI().dispose() ;
	}

	public ActionListener getCancelActionListener() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose() ;
			}
		} ;
	}

	public ActionListener getSaveActionListener() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String guiFullName = ((ContactGui)getGUI()).getContactNameTextField().getText() ;
				Integer guiContactTypeId = ((ContactTypeTO)((ContactGui)getGUI()).getContactTypeComboBox().getSelectedItem()).getId() ;
				
				if (!guiFullName.equals(getController().getCurrentContactFullName()) ||
						!guiContactTypeId.equals(getController().getCurrentContactTypeId()))	{
					getController().setCurrentContactFullName(guiFullName) ;
					getController().setCurrentContactType(guiContactTypeId) ;
					
					getController().saveContactTO() ;
				}
				
				for (ContactInfoTypeTO contactInfoType : ContactController.getContactInfoTypes())	{
					ContactInfoTypeTableModel model = ((ContactGui)getGUI()).getContactInfoModel(contactInfoType) ;

					if (model.isDirty()){
						getController().saveContactInfos(model.getContactInfos()) ;
						
						model.setContactInfos(getController().getCurrentContactInfos(contactInfoType)) ;
					}
					
					if (model.preferedHasChanged())	{
						getController().setPreferedContactInfoType(contactInfoType.getId(), model.getPreferedContactInfo()) ;
					}
				}
				
				getGUI().updateContent(false) ;
			}
		} ;
	}
}
