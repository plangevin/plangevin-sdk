/**
 * Interface representing a column. You should implement it in an enum.
 * This is used to reference a column in a JTable with an enum.
 * @author pcharette<BR>
 * Copyright:
 * (c) 2010, C-Tec Inc. - All rights reserved
 */
package com.philippelangevin.sdk.uiUtil.columnModel;


public interface ColumnModelIF<T> {
	/**
	 * The real class of the column. DefaultTableModel always returns Object.class.
	 * @return The column parametric class
	 */
	public Class<? extends T> getColumnClass();
	
	/**
	 * The preferred width of the column or null to keep the default. You MUST call setPreferredColumnsWidthOnTable
	 * to set the preferred width on columns if the table is not an CTTable (who's handle by the
	 * TableColumnModel and not the TableModel).
	 * @return The preferred width or null to keep the default.
	 */
	public Integer getPreferredWidth();
	
	/**
	 * The max width of the column or null to keep the default. You MUST call setPreferredColumnsWidthOnTable
	 * to set the maximum width on columns if the table is not an CTTable (who's handle by the
	 * TableColumnModel and not the TableModel).
	 * @return The max width or null to keep the default.
	 */
	public Integer getMaxWidth();
	
	/**
	 * Defines whether the column is resizable or not. You MUST call setPreferredColumnsWidthOnTable
	 * to set the resize property on columns if the table is not an CTTable (who's handle by the
	 * TableColumnModel and not the TableModel).
	 * @return True if column is resizable
	 */
	public boolean isResizable();
	
	/**
	 * Defines whether the column is editable or not. To provide a more sophisticated search,
	 * you should extends EnumTableModel and override isCellEditable.
	 * @return True if column is editable
	 */
	public boolean isEditable();
	
	/**
	 * It should returns the column header.
	 * @return The column header
	 */
	@Override
	public String toString();
	
	
	/**
	 * The number (index) of the column in the enum.
	 * @return The column index
	 */
	public int getColNumber();
	
	/**
	 * Cast any given object into this column type. The cast method use in this method can be unsafe.
	 * @param obj The value to cast in this column type.
	 * @return The value casted in this object type.
	 * @throws ClassCastException If a runtime check is make and the value is not of the type of this column.
	 */
//	public T cast(Object obj) throws ClassCastException;
	//FIXME uncomment and change the remaining column interface use by the ColumnModelContainer
}
