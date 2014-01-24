package com.philippelangevin.sdk.uiUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;

import net.miginfocom.swing.MigLayout;

import com.philippelangevin.sdk.database.dbAccess.AbstractDatabaseDAO;
import com.philippelangevin.sdk.database.dbAccess.DatabaseAccessObjectIF.QueryBuilder.QueryMathOperatorStruct;
import com.philippelangevin.sdk.database.transferableObject.ColumnInfo;
import com.philippelangevin.sdk.database.transferableObject.TOColumnFilter;
import com.philippelangevin.sdk.database.transferableObject.TransferableObject;
import com.philippelangevin.sdk.translationManagement.MessageBundle;
import com.philippelangevin.sdk.translationManagement.TranslationBundleFactory;

public class SearchableItemListPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final MessageBundle messageBundle = TranslationBundleFactory.getTranslationBundle() ;
	private MessageBundle databaseMessageBundle = null ;

	private JPanel searchToolPanel = null ;
	private JTextField searchText = null ;
//	private JComboBox inventoryCategoryComboBox = null ;
	private JButton searchButton = null ;
	private JScrollPane itemTableScrollPane = null ;
	private CustomJTable itemTable = null ;
	private TOSearcheableJTableModel itemTableModel = null ;
	
	private AbstractDatabaseDAO dao = null ;
//	private Vector<ObjectNameContainer<ColumnInfo<?>>> searcheableColumn = new Vector<ObjectNameContainer<ColumnInfo<?>>>() ;
	
	public SearchableItemListPanel(AbstractDatabaseDAO dao, Class<? extends TransferableObject> sourceTO) throws SQLException	{
		this.dao = dao ;

		databaseMessageBundle = TranslationBundleFactory.getTranslationBundle(this.dao.getClass()) ;
		
		this.setLayout(new MigLayout("wrap 1", "fill,grow", "[][grow,fill]")) ;
		
		this.add(getSearchToolPanel()) ;
		this.add(getItemTableScrollPane(), "spanx") ;
		
		this.setSourceTOClass(sourceTO) ;
	}
	

	public void setSourceTOClass(Class<? extends TransferableObject> sourceTO) throws SQLException	{
		itemTableModel = new TOSearcheableJTableModel(sourceTO) ;
		itemTable.setModel(itemTableModel) ;
	}
	
	public void addDisplayableColumn(ColumnInfo<?> column, int columnWidth) throws SQLException	{
		itemTableModel.addDisplayableColumn(column) ;
//		itemTable.getColumnModel().getColumn(0).set
//		searcheableColumn.add(new ObjectNameContainer<ColumnInfo<?>>(databaseMessageBundle.getString("ColumnNames." + column.toString()), column)) ;
	}
	
	private JPanel getSearchToolPanel() {
		if (searchToolPanel == null){
			searchToolPanel = new JPanel(new MigLayout("", "[grow][fill,grow,100:150:400][fill,150!]", "")) ;
			
			searchToolPanel.add(new JLabel()) ;	// Spacer
			searchToolPanel.add(getSearchText()) ;
//			searchToolPanel.add(getInventoryCategoryComboBox()) ;
			searchToolPanel.add(getSearchButton(), "wrap") ;
		}

		return searchToolPanel;
	}

	private JScrollPane getItemTableScrollPane() {
		if (itemTableScrollPane == null)	{
			itemTableScrollPane = new JScrollPane(getItemTable()) ;
			itemTableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS) ;
		}
		
		return itemTableScrollPane;
	}

	private JTable getItemTable() {
		if (itemTable == null)	{
			itemTable = new CustomJTable(itemTableModel) ;
		}
			
		return itemTable;
	}

	public JTextField getSearchText()	{
		if (searchText == null){
			searchText = new JTextField() ;
		}
		
		return searchText ;
	}
	
//	public JComboBox getInventoryCategoryComboBox()	{
//		if (inventoryCategoryComboBox == null)	{
//			inventoryCategoryComboBox = new JComboBox(/*itemTableModel.getDisplayableColumns()*/) ;
//		}
//		
//		return inventoryCategoryComboBox ;
//	}
	
	public JButton getSearchButton()	{
		if (searchButton == null){
			searchButton = new JButton(messageBundle.getString("ButtonFilter")) ;
			searchButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					itemTableModel.setFilterText(getSearchText().getText()) ;
				}
			}) ;
		}
		return searchButton ;
	}
	
	private class TOSearcheableJTableModel extends AbstractTableModel	{
		private static final long serialVersionUID = -3999544904092355868L;

		private Class<? extends TransferableObject> sourceTO = null ;
		private List<TransferableObject> items = null ;
		
		private Vector<ColumnInfo<?>> displayableColumns = new Vector<ColumnInfo<?>>() ;
		private List<TOColumnFilter<?>> filters = new ArrayList<TOColumnFilter<?>>() ; 
		
		public TOSearcheableJTableModel(Class<? extends TransferableObject> sourceTO) throws SQLException	{
			this.sourceTO = sourceTO ;
			refresh();
		}
		
		@SuppressWarnings("unchecked")
		private void refresh() throws SQLException	{
			items = (List<TransferableObject>) dao.selectTOList(this.sourceTO, filters) ;
			this.fireTableStructureChanged() ;			
		}
		
		@SuppressWarnings("unused")
		public Vector<ColumnInfo<?>> getDisplayableColumns() {
			return displayableColumns ;
		}

		public void addDisplayableColumn(ColumnInfo<?> displayableColumn) throws SQLException{
			if (sourceTO == null)	{
				throw new SQLException("Datasource not set") ;
			}
			
			displayableColumns.add(displayableColumn) ;
			this.fireTableStructureChanged() ;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void setFilterText(String text)	{
			filters.clear();
			for (ColumnInfo col : displayableColumns)	{
				filters.add(new TOColumnFilter(col, QueryMathOperatorStruct.like, text));
			} 
			
			try {
				refresh();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public String getColumnName(int column) {
			String columnName = null ;
			
			if (displayableColumns.size() > 0)	{
				columnName = displayableColumns.get(column).toString() ;
			}
			else if (items.size() > 0){
				columnName = items.get(0).getTOStructure().getColumns()[column].toString() ;
			}
			
			if (!columnName.isEmpty()){
				columnName = databaseMessageBundle.getString("ColumnNames." + columnName) ;
			}
			
			return columnName ;
		}

		@Override
		public int getColumnCount() {
			if (displayableColumns.size() > 0)	{
				return displayableColumns.size() ;
			}
			else if (items.size() > 0){
				return items.get(0).getTOStructure().getColumns().length ;
			}
			
			return 0 ;
		}

		@Override
		public int getRowCount() {
			return items.size() ;
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (displayableColumns.size() > 0)	{
				return items.get(row).get(displayableColumns.get(column)) ;
			}
			
			return items.get(row).get(items.get(row).getTOStructure().getColumns()[column]) ;
		}
		
	}
}
