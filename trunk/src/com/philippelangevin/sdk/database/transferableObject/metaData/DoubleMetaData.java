package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;

/**
   * <p> Title: {@link DoubleMetaData} <p>
   * <p> Description: This class is use to parse a object in Double and
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

public class DoubleMetaData implements TOColumnMetaDataIF<Double> {
	private boolean allowNull = false;
	
	public DoubleMetaData(boolean allowNull){
		this.allowNull=allowNull;
	}
	
	@Override
	public Boolean isNullAllowed() {
		return this.allowNull;
	}
	@Override
	public int getSQLDataType() {
		return Types.DOUBLE;
	}
	
	@Override
	public String getSQLDeclarationString()	{
		return "DOUBLE" ;
	}
	
	@Override
	public Double parse(Object value) {
		if(value!=null) {
			if(value instanceof Double) {
				return (Double) value;
			}
			else{
				try{
					Double d = Double.parseDouble(value.toString());
					return d;
				}catch(NumberFormatException e){
					e.printStackTrace();
					return null;
				}
			}
		}else if (allowNull){
			return null;
		}else{
			System.err.println("DoubleMetaData.parse() - A mandatory value has been set to null!");
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
	 * Double are immutable
	 */
	@Override
	public Object copy(Object toCopy) {
		return toCopy;
	}
	
}
