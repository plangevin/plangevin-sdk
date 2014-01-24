package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;

/**
   * <p> Title: {@link BooleanMetaData} <p>
   * <p> Description: This class is use to parse a object in boolean and
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

public class BooleanMetaData implements TOColumnMetaDataIF<Boolean> {
	private boolean allowNull = false;
	
	public BooleanMetaData(boolean allowNull){
		this.allowNull=allowNull;
	}
	
	@Override
	public int getSQLDataType() {
		return Types.BOOLEAN;
	}
	
	@Override
	public String getSQLDeclarationString()	{
		return "BOOLEAN" ;
	}
	
	@Override
	public Boolean isNullAllowed() {
		return this.allowNull;
	}

	@Override
	public Boolean parse(Object value) {
		if(value!=null) {
			if(value instanceof Boolean) {
				return (Boolean) value;
			}else{
				String stringValue = String.valueOf(value);
				return stringValue.equals("1") || stringValue.equalsIgnoreCase("true") || stringValue.equalsIgnoreCase("t");
			}
		}else if (allowNull){
			return null;
		}else{
			System.err.println("BooleanMetaData.parse() - A mandatory value has been set to null!");
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
	 * Boolean are immutable
	 */
	@Override
	public Object copy(Object toCopy) {
		return toCopy;
	}
	
}
