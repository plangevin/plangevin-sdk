package com.philippelangevin.sdk.addressBook.contact.infoType.editors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.philippelangevin.sdk.addressBook.tos.AddressTO;
import com.philippelangevin.sdk.addressBook.tos.Tables;

public class AddressGui extends EditorIF {

	private static final long serialVersionUID = -8537494778651009250L;
	
	private JTextField civicNumber = null ;
	private JTextField city = null ;
	private JTextField street = null ;
	private JTextField zipCode = null ;

	protected JPanel getMainPanel() {
		if (mainPanel == null){
			mainPanel = new JPanel(new MigLayout("wrap 2", "[][grow,fill,200!]")) ;
			
			mainPanel.add(new JLabel(getTranslatedText("CivicNumber"))) ;
			mainPanel.add(getCivicNumberJTextField()) ;
			mainPanel.add(new JLabel(getTranslatedText("Street"))) ;
			mainPanel.add(getStreetJTextField()) ;
			mainPanel.add(new JLabel(getTranslatedText("City"))) ;
			mainPanel.add(getCityJTextField()) ;
			mainPanel.add(new JLabel(getTranslatedText("ZipCode"))) ;
			mainPanel.add(getZipCodeJTextField()) ;
			
			addBottomPanel() ;
		}
		
		return mainPanel ;
	}
	
	private JTextField getCivicNumberJTextField()	{
		if (civicNumber == null){
			civicNumber = new JTextField() ;
		}
		
		return civicNumber ;
	}


	private JTextField getStreetJTextField()	{
		if (street == null){
			street = new JTextField() ;
		}
		
		return street ;
	}

	
	private JTextField getCityJTextField()	{
		if (city == null){
			city = new JTextField() ;
		}
		
		return city ;
	}

	
	private JTextField getZipCodeJTextField()	{
		if (zipCode == null){
			zipCode = new JTextField() ;
		}
		
		return zipCode ;
	}

	
	@Override
	protected void updateTOWithGui() {
		AddressTO addressTO = (AddressTO) to ;
		
		addressTO.setCivicNumber(Integer.valueOf(getCivicNumberJTextField().getText())) ;
		addressTO.setStreet(getStreetJTextField().getText()) ;
		addressTO.setCity(getCityJTextField().getText()) ;
		addressTO.setZipCode(getZipCodeJTextField().getText()) ;
	}

	@Override
	protected void updateGuiWithTO() {
		AddressTO addressTO = (AddressTO) to ;
		
		if (addressTO.getCivicNumber() == null){
			getCivicNumberJTextField().setText(null) ;
		}
		else	{
			getCivicNumberJTextField().setText(addressTO.getCivicNumber().toString()) ;
		}
		
		getStreetJTextField().setText(addressTO.getStreet()) ;
		getCityJTextField().setText(addressTO.getCity()) ;
		getZipCodeJTextField().setText(addressTO.getZipCode()) ;
	}

	@Override
	protected void setCurrentInfoType() {
		this.currentInfoType = Tables.ADDRESS.toString() ;		
	}
}
