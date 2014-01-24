package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;

/**
   * <p> Title: {@link FloatMetaData} <p>
   * <p> Description: This class is use to parse a object into a float and
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

public class FloatMetaData implements TOColumnMetaDataIF<Float> {
	private boolean allowNull = false;
	
	public FloatMetaData(boolean allowNull){
		this.allowNull=allowNull;
	}
	
	@Override
	public int getSQLDataType() {
		return Types.FLOAT;
	}
	
	@Override
	public String getSQLDeclarationString()	{
		return "FLOAT" ;
	}
	
	@Override
	public Boolean isNullAllowed() {
		return this.allowNull;
	}
	
	@Override
	public Float parse(Object value) {
		if(value!=null) {
			if(value instanceof Float) {
				return (Float) value;
			}
			else{
				try{
					Float f = Float.parseFloat(value.toString());
					return f;
				}catch(NumberFormatException e){
					e.printStackTrace();
					return null;
				}
			}
		}else if (allowNull){
			return null;
		}else{
			System.err.println("FloatMetaData.parse() - A mandatory value has been set to null!");
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
	 * Float are immutable
	 */
	@Override
	public Object copy(Object toCopy) {
		return toCopy;
	}
	
}
