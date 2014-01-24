package com.philippelangevin.sdk.addressBook.contactList;

import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.miginfocom.swing.MigLayout;

import com.philippelangevin.sdk.uiUtil.CustomJTable;
import com.philippelangevin.sdk.uiUtil.DialogRunningModes;
import com.philippelangevin.sdk.uiUtil.interfaces.EventRouterIF;
import com.philippelangevin.sdk.uiUtil.interfaces.GuiIF;

public class ContactListGui extends JDialog implements GuiIF {

	private static final long serialVersionUID = 6757137887865215815L;
	
	private ContactListEventRouter eventRouter = null ;
	
	private JPanel mainPanel = null ;
	
	private CustomJTable itemList = null ;

	public ContactListGui(ContactListEventRouter eventRouter)	{
		setEventRouter(eventRouter) ;
		
		this.setTitle(getTitle()) ;
		
		this.setContentPane(getMainPanel()) ;
		
		this.setModal(true) ;
		this.pack() ;
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE) ;
	}
	
	private JPanel getMainPanel()	{
		if (mainPanel == null){
			mainPanel = new JPanel(new MigLayout("inset 5,wrap 2", "grow,fill", "[20!][20!][20!]")) ;
			
			mainPanel.add(new JScrollPane(getItemList()), "spany") ;
		}
		
		return mainPanel ;
	}
	
	private JTable getItemList() {
		if (itemList == null){
			itemList = new CustomJTable() ;
			
			itemList.setDefaultRenderer(JLabel.class, new DefaultTableCellRenderer() {
				
				private static final long serialVersionUID = 7914637078300773589L;

				@Override
				public String getToolTipText() {
					return "<html>" + this.getText() + "<br /> getToolTipText()</html>" ;
				}

				@Override
				public String getToolTipText(MouseEvent event) {
					return "<html>" + this.getText() + "<br /> getToolTipText(MouseEvent)</html>" ;
				}
			}) ;
		}
		
		return itemList;
	}

	@Override
	public void setEventRouter(EventRouterIF eventRouter) {
		this.eventRouter = (ContactListEventRouter) eventRouter ;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setRunningMode(DialogRunningModes runningMode) {
		// TODO Auto-generated method stub
		return false;
	}

}
