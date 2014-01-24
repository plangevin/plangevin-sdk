package com.philippelangevin.sdk.addressBook.contact;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.philippelangevin.sdk.addressBook.contact.infoType.ContactInfoTypePanel;
import com.philippelangevin.sdk.addressBook.contact.infoType.ContactInfoTypeTableModel;
import com.philippelangevin.sdk.addressBook.tos.ContactInfoTOIF;
import com.philippelangevin.sdk.addressBook.tos.ContactInfoTypeTO;
import com.philippelangevin.sdk.addressBook.tos.ContactTypeTO;
import com.philippelangevin.sdk.translationManagement.MessageBundle;
import com.philippelangevin.sdk.translationManagement.TranslationBundleFactory;
import com.philippelangevin.sdk.uiUtil.DialogRunningModes;
import com.philippelangevin.sdk.uiUtil.interfaces.EventRouterIF;
import com.philippelangevin.sdk.uiUtil.interfaces.GuiIF;

public class ContactGui extends JDialog implements GuiIF {

	private static final long serialVersionUID = 7583221682678340116L;
	
	private static final MessageBundle messageBundle = TranslationBundleFactory.getTranslationBundle() ;
	
	private DialogRunningModes currentRunningMode = DialogRunningModes.NewEntry ;
	
	private ContactEventRouter eventRouter = null ;
	
	private JPanel mainPanel = null ;
	private Map<ContactInfoTypeTO, ContactInfoTypePanel> contactInfoPanelMap = new HashMap<ContactInfoTypeTO, ContactInfoTypePanel>() ;
	private JPanel buttonsPanel = null ;
	
	private JTextField contactName = null ;
	private JComboBox<ContactTypeTO> contactTypeComboBox = null ;
	private JButton applyButton = null ;
	private JButton cancelButton = null ;

	public ContactGui(ContactEventRouter eventRouter){
		setEventRouter(eventRouter) ;
		
		this.setTitle(getTitle()) ;
		
		this.setContentPane(getMainPanel()) ;
		
		this.setModal(true) ;
		this.pack() ;
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE) ;
	}
	
	@Override
	public void setVisible(boolean show) {
		if (show)	{
			updateContent(true) ;
		}
		
		super.setVisible(show);
	}



	private JPanel getMainPanel() {
		if (mainPanel == null){
			mainPanel = new JPanel(new MigLayout("wrap 1", "grow,fill,400!", "[]10[]10[]10[][]")) ;
			
			mainPanel.add(new JLabel(messageBundle.getString("ContactName")), "split") ;
			mainPanel.add(getContactNameTextField(), "grow,wrap") ;
			
			mainPanel.add(new JLabel(messageBundle.getString("CategoryLabel")), "split") ;
			mainPanel.add(getContactTypeComboBox(), "grow,wrap") ;
			
			for (ContactInfoTypeTO contactInfoType : ContactController.getContactInfoTypes())	{
				mainPanel.add(getContactInfoTypePanel(contactInfoType)) ;
			}

			mainPanel.add(getButtonsPanel()) ;
		}
		
		return mainPanel ;
	}
	
	public void updateContent(boolean refreshContactInfos)	{
		getContactNameTextField().setText(eventRouter.getController().getCurrentContactFullName()) ;
		
		Integer contactTypeId = eventRouter.getController().getCurrentContactTypeId() ;
		if (contactTypeId != null){
			ContactTypeTO contactType = new ContactTypeTO() ;
			contactType.setId(eventRouter.getController().getCurrentContactTypeId()) ;
			getContactTypeComboBox().setSelectedItem(contactType) ;
		}
		else	{
			getContactTypeComboBox().setSelectedIndex(-1) ;
		}
		
		if (refreshContactInfos){
			for (ContactInfoTypeTO contactInfoType : contactInfoPanelMap.keySet())	{
				List<ContactInfoTOIF> contactInfos = eventRouter.getController().getCurrentContactInfos(contactInfoType) ;
				Integer preferedContactInfoId = eventRouter.getController().getCurrentContactPreferedContactInfoId(contactInfoType) ;
				Class<? extends ContactInfoTOIF> clazz = eventRouter.getController().getClassForInfoType(contactInfoType) ;
				
				contactInfoPanelMap.get(contactInfoType).setContactInfos(contactInfos, clazz, preferedContactInfoId) ;
			}
		}
	}
	
	public JTextField getContactNameTextField()	{
		if (contactName == null){
			contactName = new JTextField() ;
			
			contactName.setText(eventRouter.getController().getCurrentContactFullName()) ;
		}
		
		return contactName ;
	}
	
	
	public JComboBox<ContactTypeTO> getContactTypeComboBox()	{
		if (contactTypeComboBox == null){
			contactTypeComboBox = new JComboBox<ContactTypeTO>(ContactController.getContactTypes().toArray(new ContactTypeTO[0])) ;
		}
		
		return contactTypeComboBox ;
	}

	
	private JPanel getButtonsPanel()	{
		if (buttonsPanel == null){
			buttonsPanel = new JPanel(new MigLayout("alignx center", "grow,fill,120!", "sg,grow,fill,30!")) ;
			
			buttonsPanel.add(getApplyButton()) ;
			buttonsPanel.add(getCancelButton()) ;
		}
		
		return buttonsPanel ;
	}
	
	private JButton getApplyButton()	{
		if (applyButton == null){
			applyButton = new JButton(messageBundle.getString("ButtonSave")) ;
			applyButton.addActionListener(eventRouter.getSaveActionListener()) ;
		}
		
		return applyButton ;
	}
	
	private JButton getCancelButton()	{
		if (cancelButton == null){
			cancelButton = new JButton(messageBundle.getString("ButtonCancel")) ;
			cancelButton.addActionListener(eventRouter.getCancelActionListener()) ;
		}
		
		return cancelButton ;
	}
	
	private ContactInfoTypePanel getContactInfoTypePanel(ContactInfoTypeTO contactInfoType)	{
		ContactInfoTypePanel panel = contactInfoPanelMap.get(contactInfoType) ; 

		if (panel == null)	{
			panel = new ContactInfoTypePanel(contactInfoType.getNameKey()) ;
			
			contactInfoPanelMap.put(contactInfoType, panel) ;
		}
		
		return panel ;
	}

	public ContactInfoTypeTableModel getContactInfoModel(ContactInfoTypeTO contactInfoType)	{
		return getContactInfoTypePanel(contactInfoType).getModel() ;
	}

	private String getCurrentRunningModeTitle()	{
		String titleStringKey ;
		
		switch (currentRunningMode) {
		case NewEntry:
			titleStringKey = "CreateContact" ;
			break;
			
		case EditEntry:
			titleStringKey = "EditContact" ;
			break ;
			
		case ViewEntry:
		default:
			throw new UnsupportedOperationException("Running mode ViewEntry is not implemented yet") ;
		}
		
		return messageBundle.getString(titleStringKey) ;
	}
	
	@Override
	public boolean setRunningMode(DialogRunningModes runningMode) {
		this.currentRunningMode = runningMode ;
		
		this.setTitle(getCurrentRunningModeTitle()) ;
		
		return true ;
	}
	
	@Override
	public void setEventRouter(EventRouterIF eventRouter) {
		this.eventRouter = (ContactEventRouter) eventRouter ;
	}

	@Override
	public String getTitle() {
		return getCurrentRunningModeTitle() ;
	}

}
