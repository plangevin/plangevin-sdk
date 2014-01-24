package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;

import org.joda.time.Duration;

public class DurationMetaData implements TOColumnMetaDataIF<Duration> {

	private boolean allowNull = false;
	
	public DurationMetaData(boolean nullAllowed){
		this.allowNull = nullAllowed;
	}

	@Override
	public Duration parse(Object value) {
		if(value!=null) {
			if(value instanceof Duration) {
				return (Duration) value;
			} else if (value instanceof Number) {
				return new Duration(((Number)value).intValue()*60000L);
			} else {
				try{
					return new Duration(Integer.parseInt(value.toString())*60000L);
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
			}
		}else if (allowNull){
			return null;
		}else{
			System.err.println("DateTimeMetaData.parse() - A mandatory value has been set to null!");
			Thread.dumpStack();
			return null;
		}
	}

	@Override
	public int getSQLDataType() {
		return Types.INTEGER;
	}
	
	@Override
	public String getSQLDeclarationString()	{
		return "INTEGER" ;
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

	public static long format(Duration o) {
		return o.getMillis() / 60000L;
	}
}
