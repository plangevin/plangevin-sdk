package com.philippelangevin.sdk.database.transferableObject.metaData;


/**
   * <p> Title: {@link TOColumnMetaDataIF} <p>
   * <p> Description: This is the interface of the MetaData class</p>
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
   */

public interface TOColumnMetaDataIF<T> {
	public T parse(Object value);
	public int getSQLDataType();
	public Boolean isNullAllowed();
	
	public String getSQLDeclarationString();
	
	/**
	 * Returns whether this field represents an auto-generated number.
	 * @return
	 */
	public boolean isAutoNumber();
	
	/**
	 * Returns whether this meta data is a text and therefore whether it can
	 * be affected by case sensitivity in SQL queries. Typically any string or
	 * character data will return true, and other types (int, date, etc) will
	 * return false. This method is used to avoid case sensitive searches on some
	 * DB connectors and for empty-value searches.
	 * @return
	 */
	public boolean isText();
	
	/**
	 * The metaData knows how to copy its content.
	 * @return
	 */
	public Object copy(Object toCopy);
	
}
