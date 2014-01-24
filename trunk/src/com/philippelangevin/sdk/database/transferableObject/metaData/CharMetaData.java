package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;

/**
   * <p> Title: {@link CharMetaData} <p>
   * <p> Description: This class is used to parse a object in character and
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

public class CharMetaData implements TOColumnMetaDataIF<Character> {
	private boolean allowNull = false;
	
	public CharMetaData (boolean allowNull){
		this.allowNull=allowNull;
	}
	
	@Override
	public int getSQLDataType() {
		return Types.CHAR;
	}
	
	@Override
	public String getSQLDeclarationString()	{
		return "CHAR" ;
	}
	
	@Override
	public Boolean isNullAllowed() {
		return this.allowNull;
	}

	@Override
	public Character parse(Object value) {
		if(value!=null) {
			if(value instanceof Character) {
				return (Character) value;
			} else{
				String valueStr = value.toString();
				
				if (valueStr.isEmpty()) {
					if (allowNull) {
						return null;
					} else {
						System.err.println("CharacterMetaData.parse() - A mandatory value has been set to null!");
						return null;
					}
				} else if (valueStr.length() == 1) {
					return valueStr.charAt(0);
				} else {
					System.err.println("CharacterMetaData.parse() - Warning: This value has been truncated to 1 character: " + valueStr);
					return valueStr.charAt(0);
				}
			}
		}else if (allowNull){
			return null;
		}else{
			System.err.println("CharacterMetaData.parse() - A mandatory value has been set to null!");
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
	 * Char are immutable
	 */
	@Override
	public Object copy(Object toCopy) {
		return toCopy;
	}
	
}
