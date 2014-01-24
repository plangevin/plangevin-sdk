package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;


/**
   * <p> Title: {@link StringMetaData} <p>
   * <p> Description: This class is use to parse a object into a String
   * and control the NullAllowed for the BD </p>
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

public class StringMetaData implements TOColumnMetaDataIF<String> {
	private Integer maxChars = null;
	private boolean allowNull = false;
	
	/**
	 * 
	 * @param allowNull
	 * @param maxChars maximum character allowed or null for 'unlimited' length
	 */
	public StringMetaData(boolean allowNull, Integer maxChars){
		this.maxChars = maxChars;
		this.allowNull = allowNull;
	}
	
	public Integer getMaxChars(){
		return maxChars;
	}
	
	@Override
	public int getSQLDataType() {
		return Types.VARCHAR;
	}
	@Override
	public String getSQLDeclarationString()	{
		return "VARCHAR(" + maxChars.toString() + ")" ;
	}

	@Override
	public Boolean isNullAllowed() {
		return allowNull;
	}
	
	@Override
	public String parse(Object value) {
		if(value!=null) {
			String strValue = value.toString().trim();
			
			if (strValue.isEmpty()) {
				return null;
			} else if (maxChars != null && strValue.length() > maxChars) {
				System.err.println("StringMetaData.parse() - Warning: This value has been truncated to " + maxChars + " characters: " + strValue);
				return strValue.substring(0, maxChars);
			}else{
				return strValue;
			}
		}else if (allowNull){
			return null;
		}else{
			System.err.println("StringMetaData.parse() - A mandatory value has been set to null!");
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
		return true;
	}

	/**
	 * String are immutable
	 */
	@Override
	public Object copy(Object toCopy) {
		return toCopy;
	}

}
