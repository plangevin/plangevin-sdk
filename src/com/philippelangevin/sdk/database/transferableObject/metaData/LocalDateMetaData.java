package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;

import org.joda.time.LocalDate;

import com.philippelangevin.sdk.database.xml.XMLDate;

public class LocalDateMetaData implements TOColumnMetaDataIF<LocalDate> {
	
	private boolean allowNull = false;
	
	public LocalDateMetaData(boolean nullAllowed){
		this.allowNull = nullAllowed;
	}

	@Override
	public LocalDate parse(Object value) {
		if(value!=null) {
			if(value instanceof LocalDate) {
				return (LocalDate) value;
			}
			else{
				try{
					return new LocalDate(new XMLDate(value.toString(),false).getTime());
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
			}
		}else if (allowNull){
			return null;
		}else{
			System.err.println("DateTimeFormatter.parse() - A mandatory value has been set to null!");
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
