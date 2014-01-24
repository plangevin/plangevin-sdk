package com.philippelangevin.sdk.addressBook.contact.infoType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import com.philippelangevin.sdk.addressBook.contact.infoType.editors.EditorIF;
import com.philippelangevin.sdk.addressBook.tos.ContactInfoTOIF;
import com.philippelangevin.sdk.translationManagement.MessageBundle;
import com.philippelangevin.sdk.translationManagement.TranslationBundleFactory;
import com.philippelangevin.sdk.uiUtil.CustomJTable;
import com.philippelangevin.sdk.uiUtil.DialogReturnType;

public class ContactInfoTypePanel extends JPanel {

	private static final long serialVersionUID = -5859850272669345922L;
	
	private static final MessageBundle messageBundle = TranslationBundleFactory.getTranslationBundle() ;

	private JTable list = null ;
	private ContactInfoTypeTableModel model = null ;
	private JButton defaultButton = null ;
	private JButton addButton = null ;
	private JButton deleteButton = null ;
	
	public ContactInfoTypePanel(String title){
		super(new MigLayout("inset 5,wrap 2", "grow,fill", "[20!][20!][20!]")) ;
		
		this.setBorder(new TitledBorder(messageBundle.getString(title))) ;

		add(new JScrollPane(getItemList()), "spany") ;
		
		add(getDefaultButton()) ;
		add(getAddButton()) ;
		add(getDeleteButton()) ;
	}
	
	public void setContactInfos(List<ContactInfoTOIF> contactInfos, Class<? extends ContactInfoTOIF> storedClass, Integer preferedId)	{
		model = new ContactInfoTypeTableModel(contactInfos, storedClass, preferedId) ;
		getItemList().setModel(model) ;
		getItemList().updateUI() ;
	}
	public List<ContactInfoTOIF> getContactInfos()	{
		return model.getContactInfos() ;
	}
	
	
	public void updatePreferedContactFromSelection() {
		int previousDefaultIndex = model.getDefaultIndex() ;
		
		if (previousDefaultIndex != getItemList().getSelectedRow())	{
//			setPreferedContactInfoId(((ContactInfoTypeTableModel)getItemList().getModel()).getContactInfos().get(getItemList().getSelectedRow()).getId()) ;
			model.setDefaultIndex(getItemList().getSelectedRow()) ;

			this.updateUI() ;
		}
	}

	
//	public void setPreferedContactInfoId(Integer preferedContactInfoId)	{
//		if (((ContactInfoTypeTableModel)getItemList().getModel()).setPreferedContactInfoId(preferedContactInfoId))	{
//			preferedHasChange = true ;
//		}
//	}
	
	public JTable getItemList()	{
		if (list == null){
			list = new CustomJTable() ;

			list.addMouseListener(new MouseListener() {
				@Override	public void mouseReleased(MouseEvent e)	{	;	}
				@Override	public void mousePressed(MouseEvent e)	{	;	}
				@Override	public void mouseExited(MouseEvent e)	{	;	}
				@Override	public void mouseEntered(MouseEvent e)	{	;	}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2)	{
						if (list.getSelectedRow() >= 0){
							ContactInfoTOIF to = model.getContactInfos().get(list.getSelectedRow()) ;
							
							if (EditorIF.showProperEditor(to) == DialogReturnType.OK)	{
								getItemList().updateUI() ;
								model.setDirty(true) ;
							}
						}
					}
				}
			}) ;
			
			list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (e.getFirstIndex() < 0)	{
						getDeleteButton().setEnabled(false) ;
					}
					else	{
						getDeleteButton().setEnabled(true) ;
					}
				}
			}) ;
		}
		
		return list ;
	}
	
	public JButton getDefaultButton()	{
		if (defaultButton == null){
			defaultButton = new JButton(messageBundle.getString("Default")) ;
			
			defaultButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					updatePreferedContactFromSelection() ;
				}
			}) ;
		}
		
		return defaultButton ;
	}
	
	public JButton getAddButton()	{
		if (addButton == null){
			addButton = new JButton(messageBundle.getString("Add")) ;
			addButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					ContactInfoTOIF to = null ;
					try {
						to = model.getStoredClass().newInstance();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if (EditorIF.showProperEditor(to) == DialogReturnType.OK)	{
						model.add(to) ;
						getItemList().updateUI() ;
					}
				}
			}) ;
		}
		
		return addButton ;
	}

	public JButton getDeleteButton()	{
		if (deleteButton == null){
			deleteButton = new JButton(messageBundle.getString("Delete")) ;
			deleteButton.setEnabled(false) ;
			
			deleteButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int index = list.getSelectedRow() ;
					if (index >= 0){
						String text = String.format(messageBundle.getString("DeleteItemConfirmation.Text"), model.getContactInfos().get(index).getOneLineStringRepresentation()) ;
						
						if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, text, messageBundle.getString("DeleteItemConfirmation.Title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))	{
							model.remove(index) ;
							getItemList().updateUI() ;
						}
					}
				}
			}) ;
		}
		
		return deleteButton ;
	}

	public ContactInfoTypeTableModel getModel() {
		return model ;
	}
}
