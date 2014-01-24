package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;
import java.util.ArrayList;

import com.philippelangevin.sdk.dataStructure.CopyUtil;

/**
 * A MetaData used for an Array type in the database, but interpreted as an ArrayList in Java.
 * @author ftaillefer
 *
 */
public class ArrayListLongMetaData implements TOColumnMetaDataIF<ArrayList<Long>> {
	private boolean allowNull = false;
	
	public ArrayListLongMetaData(boolean allowNull) {
		this.allowNull=allowNull;
	}

	@Override
	public Object copy(Object toCopy) {
		if(toCopy == null){return null;}
		
		if(toCopy instanceof ArrayList<?>){
			//we need a list of long
			for (Object o : (ArrayList<?>)toCopy) {
				if (o != null && !(o instanceof Long)) {
					return null;
				}
			}
			return CopyUtil.copyQuiet(toCopy);
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

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Long> parse(Object value) {
		if(value != null) {
			
			if(value instanceof ArrayList<?>){
				return (ArrayList<Long>)value;
			} //If value not an ArrayList, it's expected to be in the form it is stocked in the database
			else {
				try {
					String s = value.toString();
					String[] sTab = s.substring(1, s.length()-1).split(",");
					ArrayList<Long> i = new ArrayList<Long>();
					//Long[] i = new Long[sTab.length];
					
					for (int j=0; j<sTab.length; ++j) {
						if(!sTab[j].isEmpty() && !sTab[j].equalsIgnoreCase("NULL")){
							i.add(Long.parseLong(sTab[j]));
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
