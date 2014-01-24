package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Time;
import java.sql.Types;

public class TimeMetaData implements TOColumnMetaDataIF<Time>{
	private boolean allowNull = false;

	public TimeMetaData(boolean allowNull) {
		this.allowNull = allowNull;
	}

	@Override
	public int getSQLDataType() {
		return Types.TIME;
	}
	
	@Override
	public String getSQLDeclarationString()	{
		return "TIME" ;
	}

	@Override
	public boolean isAutoNumber() {
		return false;
	}

	@Override
	public Boolean isNullAllowed() {
		return allowNull;
	}

	@Override
	public boolean isText() {
		return false;
	}

	@Override
	public Time parse(Object value) {
		if(value!=null) {
			if(value instanceof Time) {
				return (Time) value;
			}else{
				try {
					Time t = Time.valueOf(value.toString());
					return t;
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return null;
				}
			}
		}else if (allowNull){
			return null;
		}else{
			System.err.println("TimeMetaData.parse() - A mandatory value has been set to null!");
			Thread.dumpStack();
			return null;
		}
	}

	/**
	 * Time is immutable
	 */
	@Override
	public Object copy(Object toCopy) {
		return toCopy;
	}
}
