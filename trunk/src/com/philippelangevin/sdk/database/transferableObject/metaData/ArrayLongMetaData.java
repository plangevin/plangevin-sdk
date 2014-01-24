package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;
import java.util.Arrays;

public class ArrayLongMetaData implements TOColumnMetaDataIF<Long[]> {
	private boolean allowNull = false;
	
	public ArrayLongMetaData(boolean allowNull) {
		this.allowNull=allowNull;
	}
	
	@Override
	public Object copy(Object toCopy) {
		if(toCopy == null){return null;}
		
		if(toCopy instanceof Long[]){
			Long[] a = (Long[])toCopy;
			return Arrays.copyOf(a, a.length);
		}
		return null;
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
	public Long[] parse(Object value) {
		if(value != null) {
			if(value instanceof Long[]) {
				return (Long[]) value;
			} else {
				try {
					String s = value.toString();
					String[] sTab = s.substring(1, s.length()-1).split(",");
					Long[] i = new Long[sTab.length];
					
					for (int j=0; j<sTab.length; ++j) {
						if(!sTab[j].isEmpty() && !sTab[j].equalsIgnoreCase("NULL")){
							i[j] = (Long.parseLong(sTab[j]));
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
			System.err.println("ArrayLongMetaData.parse() - A mandatory value has been set to null!");
			Thread.dumpStack();
			return null;
		}
	}

}
