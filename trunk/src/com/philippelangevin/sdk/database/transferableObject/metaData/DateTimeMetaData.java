package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;

import org.joda.time.DateTime;

import com.philippelangevin.sdk.database.xml.XMLDate;

public class DateTimeMetaData implements TOColumnMetaDataIF<DateTime> {
	
	private boolean allowNull = false;
	
	public DateTimeMetaData(boolean nullAllowed){
		this.allowNull = nullAllowed;
	}

	@Override
	public DateTime parse(Object value) {
		if(value!=null) {
			if(value instanceof DateTime) {
				return (DateTime) value;
			}
			else{
				try{
					return new DateTime(new XMLDate(value.toString(),false).getTime());
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
		return Types.TIMESTAMP;
	}
	
	@Override
	public String getSQLDeclarationString()	{
		return "TIMESTAMP" ;
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