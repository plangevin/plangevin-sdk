package com.philippelangevin.sdk.database.transferableObject;

import java.io.Serializable;

import com.philippelangevin.sdk.database.tables.TableInfo;

/**
   * <p> Title: {@link TransferableObjectStructureIF} <p>
   * <p> Description: The interface for the Structure of TrasferableObject </p>
   * <p> Company : C-Tec <p>
   *
   * @author sroger
   * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
   */

  /*
   * History
   * ------------------------------------------------
   * Date			Name		BT		Description
   * 2010-01-14		sroger				initial Revision
   * 2010-05-14		plefebvre			Removed getColumnNames() and cache from all TOs
   */

public interface TransferableObjectStructureIF extends Serializable {
	
	public static final ColumnInfo<?>[] EMPTY_COLUMNS = {};
	public static final AdditionalFieldInfo<?>[] EMPTY_ADDITIONAL_FIELDS = {};

	/**
	 * @return the table Enum represented by this TO
	 */
	public TableInfo getRepresentedTable();
	
	/**
	 * @return the class represented by the TransferableObject
	 */
	public Class<? extends TransferableObject> getRepresentedTOClass();
	
	/**
	 * @return the number of fields included in the primary key
	 */
	public Integer getPrimaryKeySize();
	
	/**
	 * @return the list of column names this table is holding. 
	 * The first column names returned must be member of the primary key.
	 */
	public ColumnInfo<?>[] getColumns();
	
	/**
	 * same as getAdditionalDataFields()
	 * @return the list of column names this table is holding. 
	 * The first column names returned must be member of the primary key.
	 */
	public AdditionalFieldInfo<?>[] getAdditionalFields();
	
}
