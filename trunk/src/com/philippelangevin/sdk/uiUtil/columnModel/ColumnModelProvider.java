/**
 * 
 */
package com.philippelangevin.sdk.uiUtil.columnModel;


/**
 * Interface used by the {@link EnumTableModel} to get the columns needed for its initialization.
 * The {@link ColumnModelProvider} implements this interface and thus, can be used directly on an
 * {@link EnumTableModel}.
 * @author pcharette
 */
/*
 * History
 * ------------------------------------------------
 * Date			Name		BT		Description
 * 2011-01-16	pcharette
 */
public interface ColumnModelProvider {

	/**
	 * Returns the list of columns to used. This list must remains constant over time.
	 * @return The array of column model.
	 */
	ColumnModelIF<?>[] getColumnsArray();

}
