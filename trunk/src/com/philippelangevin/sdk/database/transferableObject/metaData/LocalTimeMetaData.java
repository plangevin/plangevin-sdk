package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class LocalTimeMetaData implements TOColumnMetaDataIF<LocalTime> {
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("HH:mm:ss");
	
	private boolean allowNull = false;
	
	public LocalTimeMetaData(boolean nullAllowed){
		this.allowNull = nullAllowed;
	}

	@Override
	public LocalTime parse(Object value) {
		if(value!=null) {
			if(value instanceof LocalTime) {
				return (LocalTime) value;
			}else{
				try {
					return FORMATTER.parseDateTime(value.toString()).toLocalTime();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					return null;
				}
			}
		}else if (allowNull){
			return null;
		}else{
			System.err.println("LocalTimeMetaData.parse() - A mandatory value has been set to null!");
			Thread.dumpStack();
			return null;
		}
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
	public Boolean isNullAllowed() {
		return allowNull;
	}

	@Override
	public boolean isAutoNumber() {
		return false;
	}

	@Override
	public boolean isText() {
		return false;
	}

	@Override
	public Object copy(Object toCopy) {
		return toCopy;
	}
}
