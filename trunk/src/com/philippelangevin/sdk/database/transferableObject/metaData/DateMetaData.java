package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Timestamp;
import java.sql.Types;

import com.philippelangevin.sdk.database.xml.XMLDate;

/**
   * <p> Title: {@link DateMetaData} <p>
   * <p> Description: This class is used to parse a object in Date and
   * control the NullAllowed </p>
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

public class DateMetaData implements TOColumnMetaDataIF<Timestamp> {
	private Boolean allowNull = false;
	
	public DateMetaData(Boolean nullAllowed){
		this.allowNull = nullAllowed;
	}
	
	@Override
	public int getSQLDataType() {
		return Types.DATE;
	}
	
	@Override
	public String getSQLDeclarationString()	{
		return "DATE" ;
	}
	
	@Override
	public Boolean isNullAllowed() {
		return this.allowNull;
	}

	@Override
	public Timestamp parse(Object value) {
		if(value!=null) {
			if(value instanceof Timestamp) {
				return (Timestamp) value;
			}
			else{
				try{
					return new XMLDate(value.toString(),false);
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
			}
		}else if (allowNull){
			return null;
		}else{
			System.err.println("DateMetaData.parse() - A mandatory value has been set to null!");
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

	@Override
	public Object copy(Object toCopy) {
		if(toCopy == null){return null;}
		
		if(toCopy instanceof Timestamp){
			return new Timestamp(((Timestamp)toCopy).getTime());
		}
		else if(toCopy instanceof XMLDate){
			//Here we assume that we used it as immutable Object
			return toCopy;
		}
		return null;
	}
	
}
