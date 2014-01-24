package com.philippelangevin.sdk.addressBook.contact.infoType.editors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.philippelangevin.sdk.addressBook.tos.PhoneTO;
import com.philippelangevin.sdk.addressBook.tos.Tables;

public class PhoneGui extends EditorIF {
	
	private static final long serialVersionUID = -3388423651336574193L;
	
	private JTextField phoneTextField = null ;
	
	protected JPanel getMainPanel() {
		if (mainPanel == null)	{
			mainPanel = new JPanel(new MigLayout("wrap 2", "[][grow,fill,200!]")) ;
			
			mainPanel.add(new JLabel(getTranslatedText("DataLabel"))) ;
			mainPanel.add(getPhoneJTextField()) ;

			addBottomPanel() ;
		}
		
		return mainPanel ;
	}
	
	private JTextField getPhoneJTextField()	{
		if (phoneTextField == null){
			phoneTextField = new JTextField() ;
		}
		
		return phoneTextField ;
	}
	
	public String getPhoneNumber()	{
		return getPhoneJTextField().getText() ;
	}

	@Override
	protected void updateTOWithGui() {
		((PhoneTO)to).setPhoneNumber(getPhoneNumber()) ;
	}

	@Override
	protected void updateGuiWithTO() {
		getPhoneJTextField().setText(((PhoneTO)to).getPhoneNumber()) ;
	}
	
	@Override
	protected void setCurrentInfoType() {
		this.currentInfoType = Tables.PHONE.toString() ;		
	}

}
