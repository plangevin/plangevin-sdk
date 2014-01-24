package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;

/**
   * <p> Title: {@link ObjectMetaData} <p>
   * <p> Description: This class is used to control the NullAllowed and
   * control the NullAllowed</p>
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

public class ObjectMetaData implements TOColumnMetaDataIF<Object> {
	private boolean allowNull = false;
	
	public ObjectMetaData(boolean allowNull){
		this.allowNull=allowNull;
	}
	
	@Override
	public int getSQLDataType() {
		return Types.VARCHAR;
	}
	
	@Override
	public String getSQLDeclarationString()	{
		return "VARCHAR" ;
	}
	
	@Override
	public Boolean isNullAllowed() {
		return this.allowNull;
	}
	
	@Override
	public Object parse(Object value) {
		if(value!=null || allowNull) {
			return value;
		}else{
			System.err.println("ObjectMetaData.parse() - A mandatory value has been set to null!");
			Thread.dumpStack();
			return null;
		}
	}
	
	@Override
	public boolean isAutoNumber() {
		return false;
	}
	
	@Override
	public boolean isText() {
		return false;
	}
	
	/**
	 * This is probably not a copy but there no way to know how to copy
	 * this Object
	 */
	@Override
	public Object copy(Object toCopy) {
		return toCopy;
	}
	
}
