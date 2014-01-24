package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;

/**
  * <p> Title: {@link LongMetaData} <p>
  * <p> Description: TO meta data for long (bigint) types. </p>
  * <p> Company : C-Tec <p>
  * 
  * @author plefebvre
  * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
  */

 /*
  * History
  * ------------------------------------------------
  * Date			Name		BT		Description
  * 2010-07-20		plefebvre
  */

public class LongMetaData implements TOColumnMetaDataIF<Long> {
	private boolean allowNull = false;
	private boolean autoNumber = false;
	
	public LongMetaData(boolean allowNull){
		this.allowNull = allowNull;
	}
	
	public LongMetaData(boolean allowNull, boolean autoNumber) {
		this.allowNull = allowNull;
		this.autoNumber = autoNumber;
	}
	
	@Override
	public int getSQLDataType() {
		return Types.BIGINT;
	}
	
	@Override
	public String getSQLDeclarationString()	{
		return "BIGINT" ;
	}
	
	@Override
	public Boolean isNullAllowed() {
		return this.allowNull;
	}

	@Override
	public Long parse(Object value) {
		if (value != null) {
			if (value instanceof Long) {
				return (Long) value;
			} else {
				try{
					Long l = Long.parseLong(value.toString());
					return l;
				}catch(NumberFormatException e){
					e.printStackTrace();
					return null;
				}
			}
		} else if (allowNull){
			return null;
		} else {
			System.err.println("LongMetaData.parse() - A mandatory value has been set to null!");
			Thread.dumpStack();
			return null;
		}
	}
	
	@Override
	public boolean isAutoNumber() {
		return autoNumber;
	}
	
	@Override
	public boolean isText() {
		return false;
	}

	/**
	 * Long are immutable
	 */
	@Override
	public Object copy(Object toCopy) {
		return toCopy;
	}
	
}
