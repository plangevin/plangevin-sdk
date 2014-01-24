package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;
import java.util.Arrays;

/*
 * @plefebvre 2010-08-06
 * Expected (and tested) results for this class:
 * 
 * Integer[] params = null:       DB will contain "", will return: null
 * Integer[] params = []:         DB will contain "", will return: null
 * Integer[] params = [null]:     DB will contain {} or {NULL}, will return: [null]
 * Integer[] params = [1,null,3]: DB will contain {1,NULL,3}, will return: [1,null,3]
 */

public class ArrayIntegerMetaData implements TOColumnMetaDataIF<Integer[]> {
	private boolean allowNull = false;
	
	public ArrayIntegerMetaData(boolean allowNull) {
		this.allowNull=allowNull;
	}

	@Override
	public int getSQLDataType() {
		return Types.ARRAY;
	}
	
	@Override
	public String getSQLDeclarationString()	{
		return "ARRAY" ;
	}

	@Override
	public boolean isAutoNumber() {
		return false;
	}

	@Override
	public Boolean isNullAllowed() {
		return this.allowNull;
	}

	@Override
	public boolean isText() {
		return false;
	}

	@Override
	public Integer[] parse(Object value) {
		if(value != null) {
			if(value instanceof Integer[]) {
				return (Integer[]) value;
			} else {
				try {
					String s = value.toString();
					String[] sTab = s.substring(1, s.length()-1).split(",");
					Integer[] i = new Integer[sTab.length];
					
					for (int j=0; j<sTab.length; ++j) {
						if(!sTab[j].isEmpty() && !sTab[j].equalsIgnoreCase("NULL")){
							i[j] = (Integer.parseInt(sTab[j]));
						}
					}
					return i;
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return null;
				}
			}
		} else if (allowNull) {
			return null;
		} else {
			System.err.println("ArrayIntegerMetaData.parse() - A mandatory value has been set to null!");
			Thread.dumpStack();
			return null;
		}
	}

	@Override
	public Object copy(Object toCopy) {
		if(toCopy == null){return null;}
		
		if(toCopy instanceof Integer[]){
			Integer[] a = (Integer[])toCopy;
			return Arrays.copyOf(a, a.length);
		}
		return null;
	}
}
