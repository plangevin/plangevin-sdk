package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;

/**
   * <p> Title: {@link IntegerMetaData} <p>
   * <p> Description:This class is use to parse a object into a Integer and
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

public class IntegerMetaData implements TOColumnMetaDataIF<Integer> {
	private boolean allowNull = false;
	private boolean autoNumber = false;
	
	public IntegerMetaData(boolean allowNull){
		this.allowNull=allowNull;
	}
	
	public IntegerMetaData(boolean allowNull, boolean autoNumber) {
		this.allowNull=allowNull;
		this.autoNumber=autoNumber;
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
		return this.allowNull;
	}

	@Override
	public Integer parse(Object value) {
		if(value!=null) {
			if(value instanceof Integer) {
				return (Integer) value;
			} else {
				try{
					Integer i = Integer.parseInt(value.toString());
					return i;
				}catch(NumberFormatException e){
					e.printStackTrace();
					return null;
				}
			}
		}else if (allowNull){
			return null;
		}else{
			System.err.println("IntegerMetaData.parse() - A mandatory value has been set to null!");
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
	 * Integer are immutable
	 */
	@Override
	public Object copy(Object toCopy) {
		return toCopy;
	}
	
}
