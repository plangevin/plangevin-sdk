package com.philippelangevin.sdk.addressBook.contact.infoType.editors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.philippelangevin.sdk.addressBook.tos.EmailTO;
import com.philippelangevin.sdk.addressBook.tos.Tables;

public class EmailGui extends EditorIF {
	
	private static final long serialVersionUID = -3388423651336574193L;
	
	private JTextField emailTextField = null ;

	protected JPanel getMainPanel() {
		if (mainPanel == null)	{
			mainPanel = new JPanel(new MigLayout("wrap 2", "[][grow,fill,200!]")) ;
			
			mainPanel.add(new JLabel(getTranslatedText("DataLabel"))) ;
			mainPanel.add(getEmailJTextField()) ;

			addBottomPanel() ;
		}
		
		return mainPanel ;
	}
	
	private JTextField getEmailJTextField()	{
		if (emailTextField == null){
			emailTextField = new JTextField() ;
		}
		
		return emailTextField ;
	}
	
	public String getEmailAddress()	{
		return getEmailJTextField().getText() ;
	}

	@Override
	protected void updateTOWithGui() {
		((EmailTO)to).setEmail(getEmailAddress()) ;
	}

	@Override
	protected void updateGuiWithTO() {
		getEmailJTextField().setText(((EmailTO)to).getEmail()) ;
	}
	
	@Override
	protected void setCurrentInfoType() {
		this.currentInfoType = Tables.EMAIL.toString() ;		
	}
}
