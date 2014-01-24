package com.philippelangevin.sdk.addressBook.contact.infoType.editors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.philippelangevin.sdk.addressBook.contact.ContactController;
import com.philippelangevin.sdk.addressBook.tos.AddressTO;
import com.philippelangevin.sdk.addressBook.tos.ContactCategoryTO;
import com.philippelangevin.sdk.addressBook.tos.ContactInfoTOIF;
import com.philippelangevin.sdk.addressBook.tos.EmailTO;
import com.philippelangevin.sdk.addressBook.tos.PhoneTO;
import com.philippelangevin.sdk.translationManagement.MessageBundle;
import com.philippelangevin.sdk.translationManagement.TranslationBundleFactory;
import com.philippelangevin.sdk.uiUtil.DialogReturnType;

public abstract class EditorIF extends JDialog {

	private static final long serialVersionUID = -7134183803937365430L;
	
	protected static final MessageBundle messageBundle = TranslationBundleFactory.getTranslationBundle() ;
	
	protected ContactInfoTOIF to = null ;
	protected String currentInfoType ;
	
	protected JPanel mainPanel = null ;
	private JComboBox<ContactCategoryTO> categoryComboBox = null ;
	private JButton okButton = null ;
	private JButton cancelButton = null ;
	
//	private Class<? extends ContactInfoTOIF> editingClass = null ;
	
	private DialogReturnType returnValue = DialogReturnType.CANCEL ;

//	public EditorIF(Class<? extends ContactInfoTOIF> editingClass)	{
//		this.editingClass = editingClass ;
	public EditorIF()	{

	}
	
	public void setContactInfoTO(ContactInfoTOIF to){
		this.to = to ;
		
		setCurrentInfoType() ;
		
		this.setContentPane(getMainPanel()) ;

		this.setTitle(messageBundle.getString(currentInfoType + ".EditorDialogTitle")) ;
		
		updateGuiWithTO() ;
		updateContactCategoryGuiWithTO() ;

		this.pack() ;
		this.setModal(true) ;
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE) ;
	}
	

	public DialogReturnType showDialog() {
		super.setVisible(true);
		
		return returnValue ;
	}


	protected JPanel addBottomPanel()	{
		JPanel categoryPanel = new JPanel(new MigLayout("inset 10,wrap 2", "[][grow,fill]")) ;
		categoryPanel.add(new JLabel(messageBundle.getString("CategoryLabel"))) ;
		categoryPanel.add(getCategoryComboBox()) ;
		
		mainPanel.add(categoryPanel, "span,grow") ;
		
		JPanel buttonPanel = new JPanel(new MigLayout("alignx center", "grow,fill,100!", "sg,grow,fill,30!")) ;
		buttonPanel.add(getOkButton()) ;
		buttonPanel.add(getCancelButton()) ;
		
		mainPanel.add(buttonPanel, "span,grow") ;
		return categoryPanel ;
	}
	
	private JComboBox<ContactCategoryTO> getCategoryComboBox()	{
		if (categoryComboBox == null){
			categoryComboBox = new JComboBox<ContactCategoryTO>(ContactController.getContactCategories().toArray(new ContactCategoryTO[0])) ;
		}
		
		return categoryComboBox ;
	}
	

	private JButton getOkButton()	{
		if (okButton == null){
			okButton = new JButton(messageBundle.getString("ButtonOk")) ;
			okButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					updateTOWithGui() ;
					updateContactCategoryTOWithGui() ;
					returnValue = DialogReturnType.OK ;
					dispose() ;
				}
			}) ;
		}
		
		return okButton ; 
	}
	
	private JButton getCancelButton()	{
		if (cancelButton == null){
			cancelButton = new JButton(messageBundle.getString("ButtonCancel")) ;
			cancelButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					returnValue = DialogReturnType.CANCEL ;
					dispose() ;
				}
			}) ;
		}
		
		return cancelButton ; 
	}

	public ContactCategoryTO getSelectedCategory()	{
		return (ContactCategoryTO) categoryComboBox.getSelectedItem() ;
	}
	
	private void updateContactCategoryTOWithGui()	{
		to.setContactCategory(((ContactCategoryTO)getCategoryComboBox().getSelectedItem()).getId()) ;
	}
	private void updateContactCategoryGuiWithTO()	{
		ContactCategoryTO contactCategoryTO = new ContactCategoryTO() ;
		contactCategoryTO.setId(to.getContactCategory()) ;
		categoryComboBox.setSelectedItem(contactCategoryTO) ;	
	}
	
	/**
	 * A convenient method to get the translated string using a properly formatted key
	 * The TO represented table will be used as the right part
	 * Ex: <representedTable>.<partialKey> -- Address.CivicNumber  
	 * @param partialKey	The right part of the key
	 * @return	The correct translation ready to print
	 */
	protected String getTranslatedText(String partialKey){
		return messageBundle.getString(currentInfoType + "." + partialKey) ;
	}
	
	protected abstract void setCurrentInfoType() ;

	protected abstract JPanel getMainPanel() ;
	protected abstract void updateTOWithGui() ;
	protected abstract void updateGuiWithTO() ;

	public static DialogReturnType showProperEditor(ContactInfoTOIF workingTO) {
		EditorIF gui = null ;
		
		if (workingTO instanceof AddressTO)	{
			gui = new AddressGui() ;
		}
		else if (workingTO instanceof PhoneTO)	{
			gui = new PhoneGui() ;
		}
		else if (workingTO instanceof EmailTO)	{
			gui = new EmailGui() ;
		}
		
		gui.setContactInfoTO(workingTO) ;
		
		return gui.showDialog() ;
	}
}
